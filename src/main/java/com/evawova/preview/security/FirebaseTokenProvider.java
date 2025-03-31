package com.evawova.preview.security;

import com.evawova.preview.domain.user.entity.User;
import com.evawova.preview.domain.user.repository.UserRepository;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FirebaseTokenProvider {

    private final UserRepository userRepository;

    @Value("${FIREBASE_PROJECT_ID}")
    private String projectId;

    @Value("${FIREBASE_PRIVATE_KEY_ID}")
    private String privateKeyId;

    @Value("${FIREBASE_PRIVATE_KEY}")
    private String privateKey;

    @Value("${FIREBASE_CLIENT_EMAIL}")
    private String clientEmail;

    @Value("${FIREBASE_CLIENT_ID}")
    private String clientId;

    @Value("${FIREBASE_CLIENT_CERT_URL}")
    private String clientCertUrl;

    @PostConstruct
    public void init() {
        try {
            String jsonContent = String.format("""
                    {
                      "type": "service_account",
                      "project_id": "%s",
                      "private_key_id": "%s",
                      "private_key": "%s",
                      "client_email": "%s",
                      "client_id": "%s",
                      "auth_uri": "https://accounts.google.com/o/oauth2/auth",
                      "token_uri": "https://oauth2.googleapis.com/token",
                      "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
                      "client_x509_cert_url": "%s"
                    }
                    """, projectId, privateKeyId, privateKey, clientEmail, clientId, clientCertUrl);

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(
                            new ByteArrayInputStream(jsonContent.getBytes(StandardCharsets.UTF_8))))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
        } catch (IOException e) {
            log.error("Failed to initialize Firebase Admin SDK", e);
            throw new RuntimeException("Failed to initialize Firebase Admin SDK", e);
        }
    }

    public String getUidFromToken(String token) {
        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
            return decodedToken.getUid();
        } catch (FirebaseAuthException e) {
            log.error("Failed to verify Firebase token", e);
            throw new IllegalArgumentException("Invalid token", e);
        }
    }

    public Authentication getAuthentication(String token) {
        try {
            String uid = getUidFromToken(token);
            
            // DB에서 사용자 조회
            Optional<User> userOptional = userRepository.findByUid(uid);
            
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                // 사용자가 존재하면 해당 정보로 인증 토큰 생성
                return new FirebaseAuthenticationToken(
                    new FirebaseUserDetails(user)
                );
            } else {
                // 사용자가 존재하지 않으면 uid만으로 인증 토큰 생성
                return new FirebaseAuthenticationToken(uid);
            }
        } catch (IllegalArgumentException e) {
            log.error("Invalid token", e);
            return null;
        }
    }

    public boolean validateToken(String token) {
        try {
            FirebaseAuth.getInstance().verifyIdToken(token);
            return true;
        } catch (FirebaseAuthException e) {
            log.error("Failed to verify Firebase token", e);
            return false;
        }
    }
} 