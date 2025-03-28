package com.evawova.preview.common.exception;

import com.evawova.preview.common.response.ApiResponse;
import com.evawova.preview.common.response.ResponseEntityBuilder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleApiException(ApiException e, HttpServletRequest request) {
        log.error("API 예외 발생: {}", e.getMessage());
        
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("message", e.getMessage());
        errorDetails.put("path", request.getRequestURI());
        errorDetails.put("method", request.getMethod());
        errorDetails.put("status", e.getStatus().value());
        
        return ResponseEntityBuilder.error(errorDetails, e.getStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleValidationException(
            MethodArgumentNotValidException e, HttpServletRequest request) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("message", "유효성 검증 실패");
        errorDetails.put("path", request.getRequestURI());
        errorDetails.put("method", request.getMethod());
        errorDetails.put("status", HttpStatus.BAD_REQUEST.value());
        
        Map<String, String> validationErrors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error -> 
            validationErrors.put(error.getField(), error.getDefaultMessage())
        );
        errorDetails.put("validationErrors", validationErrors);
        
        log.error("유효성 검증 실패: {}", validationErrors);
        return ResponseEntityBuilder.error(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleBindException(
            BindException e, HttpServletRequest request) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("message", "바인딩 실패");
        errorDetails.put("path", request.getRequestURI());
        errorDetails.put("method", request.getMethod());
        errorDetails.put("status", HttpStatus.BAD_REQUEST.value());
        
        Map<String, String> bindingErrors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error -> 
            bindingErrors.put(error.getField(), error.getDefaultMessage())
        );
        errorDetails.put("bindingErrors", bindingErrors);
        
        log.error("바인딩 실패: {}", bindingErrors);
        return ResponseEntityBuilder.error(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("message", String.format("'%s'의 값 '%s'가 '%s' 타입으로 변환될 수 없습니다.",
                e.getName(), e.getValue(), e.getRequiredType().getSimpleName()));
        errorDetails.put("path", request.getRequestURI());
        errorDetails.put("method", request.getMethod());
        errorDetails.put("status", HttpStatus.BAD_REQUEST.value());
        errorDetails.put("parameter", e.getName());
        errorDetails.put("value", e.getValue());
        errorDetails.put("requiredType", e.getRequiredType().getSimpleName());
        
        log.error("인자 타입 불일치: {}", errorDetails);
        return ResponseEntityBuilder.error(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException e, HttpServletRequest request) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("message", String.format("필수 파라미터 '%s'가 누락되었습니다.", e.getParameterName()));
        errorDetails.put("path", request.getRequestURI());
        errorDetails.put("method", request.getMethod());
        errorDetails.put("status", HttpStatus.BAD_REQUEST.value());
        errorDetails.put("parameter", e.getParameterName());
        errorDetails.put("parameterType", e.getParameterType().toString());
        
        log.error("필수 파라미터 누락: {}", errorDetails);
        return ResponseEntityBuilder.error(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("message", String.format("지원하지 않는 HTTP 메서드입니다: %s", e.getMessage()));
        errorDetails.put("path", request.getRequestURI());
        errorDetails.put("method", request.getMethod());
        errorDetails.put("status", HttpStatus.METHOD_NOT_ALLOWED.value());
        errorDetails.put("supportedMethods", e.getSupportedHttpMethods());
        
        log.error("지원하지 않는 HTTP 메서드: {}", errorDetails);
        return ResponseEntityBuilder.error(errorDetails, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleNoResourceFoundException(
            NoResourceFoundException e, HttpServletRequest request) {
        // favicon.ico 요청에 대한 404는 무시
        if (request.getRequestURI().equals("/favicon.ico")) {
            Map<String, Object> emptyDetails = new HashMap<>();
            return ResponseEntityBuilder.error(emptyDetails, HttpStatus.NOT_FOUND);
        }
        
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("message", String.format("요청한 리소스를 찾을 수 없습니다: %s", request.getRequestURI()));
        errorDetails.put("path", request.getRequestURI());
        errorDetails.put("method", request.getMethod());
        errorDetails.put("status", HttpStatus.NOT_FOUND.value());
        
        log.error("리소스를 찾을 수 없음: {}", errorDetails);
        return ResponseEntityBuilder.error(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleException(
            Exception e, HttpServletRequest request) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("message", "서버 내부 오류가 발생했습니다.");
        errorDetails.put("path", request.getRequestURI());
        errorDetails.put("method", request.getMethod());
        errorDetails.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        
        log.error("예상치 못한 예외 발생", e);
        return ResponseEntityBuilder.error(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
} 