package com.evawova.preview.security;

import com.evawova.preview.domain.user.entity.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.time.LocalDateTime;
import java.util.Collections;

public class WithMockFirebaseUserSecurityContextFactory implements WithSecurityContextFactory<WithMockFirebaseUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockFirebaseUser annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        User user = User.builder()
                .id(1L)
                .uid(annotation.uid())
                .email(annotation.email())
                .displayName(annotation.displayName())
                .provider(User.Provider.GOOGLE)
                .role(User.Role.valueOf(annotation.role()))
                .active(true)
                .photoUrl("https://example.com/photo.jpg")
                .isEmailVerified(true)
                .lastLoginAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        FirebaseUserDetails principal = new FirebaseUserDetails(user);
        
        Authentication auth = new UsernamePasswordAuthenticationToken(
                principal, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + annotation.role()))
        );
        
        context.setAuthentication(auth);
        return context;
    }
} 