package com.evawova.preview.common.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestResponseLoggingFilter extends OncePerRequestFilter {

    private static final List<MediaType> VISIBLE_TYPES = Arrays.asList(
            MediaType.valueOf("text/*"),
            MediaType.APPLICATION_FORM_URLENCODED,
            MediaType.APPLICATION_JSON,
            MediaType.APPLICATION_XML,
            MediaType.valueOf("application/*+json"),
            MediaType.valueOf("application/*+xml"),
            MediaType.MULTIPART_FORM_DATA
    );

    private static final List<String> EXCLUDE_URI_PATTERNS = Arrays.asList(
            "/h2-console",
            "/actuator",
            "/favicon.ico"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return EXCLUDE_URI_PATTERNS.stream().anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // 요청 ID 생성 (추적용)
        String requestId = UUID.randomUUID().toString().substring(0, 8);
        
        // 요청과 응답을 캐싱하기 위한 래퍼
        ContentCachingRequestWrapper requestWrapper;
        if (request instanceof ContentCachingRequestWrapper) {
            requestWrapper = (ContentCachingRequestWrapper) request;
        } else {
            requestWrapper = new ContentCachingRequestWrapper(request);
        }
        
        ContentCachingResponseWrapper responseWrapper;
        if (response instanceof ContentCachingResponseWrapper) {
            responseWrapper = (ContentCachingResponseWrapper) response;
        } else {
            responseWrapper = new ContentCachingResponseWrapper(response);
        }

        // 요청 로깅
        try {
            logRequest(requestWrapper, requestId);
        } catch (Exception e) {
            log.error("요청 로깅 중 오류 발생", e);
        }

        // 필터 체인 실행
        long startTime = System.currentTimeMillis();
        try {
            filterChain.doFilter(requestWrapper, responseWrapper);
        } finally {
            // 응답 로깅
            long duration = System.currentTimeMillis() - startTime;
            try {
                logResponse(responseWrapper, requestId, duration);
            } catch (Exception e) {
                log.error("응답 로깅 중 오류 발생", e);
            } finally {
                // 응답 내용 복원 (중요!)
                responseWrapper.copyBodyToResponse();
            }
        }
    }

    private void logRequest(ContentCachingRequestWrapper request, String requestId) throws IOException {
        String queryString = request.getQueryString();
        String url = queryString == null ? request.getRequestURI() : request.getRequestURI() + "?" + queryString;
        
        log.info("[{}] 요청: {} {} ({})", requestId, request.getMethod(), url, request.getRemoteAddr());
        
        // 헤더 로깅
        Collections.list(request.getHeaderNames())
                .forEach(headerName -> {
                    Collections.list(request.getHeaders(headerName))
                            .forEach(headerValue -> {
                                // 민감한 정보(Authorization 등)는 일부만 로깅
                                if (headerName.equalsIgnoreCase("Authorization")) {
                                    headerValue = headerValue.substring(0, Math.min(headerValue.length(), 10)) + "...";
                                }
                                log.debug("[{}] 요청 헤더: {}: {}", requestId, headerName, headerValue);
                            });
                });

        // 파라미터 로깅
        request.getParameterMap().forEach((key, values) -> {
            log.debug("[{}] 요청 파라미터: {} = {}", requestId, key, Arrays.toString(values));
        });

        // 본문 로깅 (POST, PUT 요청인 경우만)
        if (isRequestBodyMethod(request.getMethod()) && isReadableContentType(request.getContentType())) {
            // 요청 처리 후에 본문이 로깅되도록 구현 (doFilter 후에 실행)
            byte[] content = request.getContentAsByteArray();
            if (content.length > 0) {
                String contentString = new String(content, StandardCharsets.UTF_8);
                // 로그 길이 제한
                if (contentString.length() > 1000) {
                    contentString = contentString.substring(0, 1000) + "... (truncated)";
                }
                log.debug("[{}] 요청 본문: {}", requestId, contentString);
            }
        }
    }

    private boolean isRequestBodyMethod(String method) {
        return "POST".equals(method) || "PUT".equals(method) || "PATCH".equals(method);
    }

    private void logResponse(ContentCachingResponseWrapper response, String requestId, long duration) {
        int status = response.getStatus();
        log.info("[{}] 응답: {} ({}ms)", requestId, status, duration);
        
        // 헤더 로깅
        response.getHeaderNames()
                .forEach(headerName -> {
                    response.getHeaders(headerName)
                            .forEach(headerValue -> {
                                log.debug("[{}] 응답 헤더: {}: {}", requestId, headerName, headerValue);
                            });
                });

        // 응답 본문 로깅
        byte[] content = response.getContentAsByteArray();
        if (content.length > 0 && isReadableContentType(response.getContentType())) {
            String contentString = new String(content, StandardCharsets.UTF_8);
            // 로그 길이 제한
            if (contentString.length() > 1000) {
                contentString = contentString.substring(0, 1000) + "... (truncated)";
            }
            log.debug("[{}] 응답 본문: {}", requestId, contentString);
        }
    }

    private boolean hasRequestBody(HttpServletRequest request) {
        return request.getContentLength() > 0 || request.getContentType() != null;
    }

    private boolean isReadableContentType(String contentType) {
        if (contentType == null) {
            return false;
        }
        try {
            MediaType requestContentType = MediaType.valueOf(contentType);
            return VISIBLE_TYPES.stream()
                    .anyMatch(visibleType -> visibleType.includes(requestContentType));
        } catch (Exception e) {
            return false;
        }
    }
} 