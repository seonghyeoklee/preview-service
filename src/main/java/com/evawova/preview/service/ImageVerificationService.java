package com.evawova.preview.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 이미지 URL 검증 서비스
 * - SVG 이미지가 실제로 존재하는지 검증하는 기능 제공
 */
@Slf4j
@Service
public class ImageVerificationService {

    private final RestTemplate restTemplate;
    private final ExecutorService executorService;

    public ImageVerificationService() {
        this.restTemplate = new RestTemplate();
        this.executorService = Executors.newFixedThreadPool(10);
    }

    /**
     * 이미지 URL이 유효한지 비동기적으로 확인
     * 
     * @param imageUrl 확인할 이미지 URL
     * @return CompletableFuture<Boolean> 검증 결과 (유효한 경우 true)
     */
    public CompletableFuture<Boolean> verifyImageUrlAsync(String imageUrl) {
        return CompletableFuture.supplyAsync(() -> {
            return verifyImageUrl(imageUrl);
        }, executorService);
    }

    /**
     * 이미지 URL이 유효한지 동기적으로 확인
     * 
     * @param imageUrl 확인할 이미지 URL
     * @return 검증 결과 (유효한 경우 true)
     */
    public boolean verifyImageUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return false;
        }

        try {
            URI uri = new URI(imageUrl);
            ResponseEntity<Void> response = restTemplate.exchange(uri, HttpMethod.HEAD, null, Void.class);
            HttpStatusCode statusCode = response.getStatusCode();

            // 2xx 응답은 이미지가 존재함을 의미
            boolean isValid = statusCode.is2xxSuccessful();

            if (!isValid) {
                log.warn("이미지 검증 실패: {} (응답 코드: {})", imageUrl, statusCode);
            }

            return isValid;
        } catch (Exception e) {
            log.warn("이미지 검증 오류: {} ({})", imageUrl, e.getMessage());
            return false;
        }
    }
}