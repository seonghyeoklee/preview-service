package com.evawova.preview.common.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public class ResponseEntityBuilder {

    public static <T> ResponseEntity<ApiResponse<T>> success(T data, String message) {
        ApiResponse<T> response = ApiResponse.success(data, message);
        return ResponseEntity.ok(response);
    }

    public static <T> ResponseEntity<ApiResponse<T>> success(T data) {
        return success(data, "요청이 성공적으로 처리되었습니다.");
    }

    public static <T> ResponseEntity<ApiResponse<T>> success(T data, HttpStatus status) {
        return ResponseEntity.status(status).body(ApiResponse.success(data, status));
    }

    public static <T> ResponseEntity<ApiResponse<T>> success(T data, HttpStatus status, String message) {
        return ResponseEntity.status(status).body(ApiResponse.success(data, status, message));
    }

    public static <T> ResponseEntity<ApiResponse<T>> created(T data, String message) {
        ApiResponse<T> response = ApiResponse.success(data, message);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    public static <T> ResponseEntity<ApiResponse<T>> created(T data) {
        return created(data, "리소스가 성공적으로 생성되었습니다.");
    }

    public static <T> ResponseEntity<ApiResponse<T>> error(String message) {
        return ResponseEntity.badRequest().body(ApiResponse.error(message));
    }

    public static ResponseEntity<ApiResponse<Void>> error(String message, HttpStatus status) {
        ApiResponse<Void> response = ApiResponse.error(message, status);
        return ResponseEntity.status(status).body(response);
    }

    public static <T> ResponseEntity<ApiResponse<T>> error(String message, HttpStatus status, T data) {
        return ResponseEntity.status(status).body(ApiResponse.error(data, status));
    }

    public static ResponseEntity<ApiResponse<Void>> notFound(String message) {
        return error(message, HttpStatus.NOT_FOUND);
    }

    public static ResponseEntity<ApiResponse<Void>> notFound() {
        return notFound("요청한 리소스를 찾을 수 없습니다.");
    }

    public static ResponseEntity<ApiResponse<Void>> serverError(String message) {
        return error(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static ResponseEntity<ApiResponse<Void>> serverError() {
        return serverError("서버 내부 오류가 발생했습니다.");
    }

    public static <T> ResponseEntity<ApiResponse<T>> error(T data, HttpStatus status) {
        ApiResponse<T> response = ApiResponse.error(data, status);
        return ResponseEntity.status(status).body(response);
    }

    public static ResponseEntity<ApiResponse<Map<String, Object>>> error(Map<String, Object> errorDetails, HttpStatus status) {
        ApiResponse<Map<String, Object>> response = ApiResponse.error(errorDetails, status);
        return ResponseEntity.status(status).body(response);
    }
} 