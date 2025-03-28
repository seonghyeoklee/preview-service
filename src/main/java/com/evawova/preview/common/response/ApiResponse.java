package com.evawova.preview.common.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL) // null 값은 JSON에서 제외
public class ApiResponse<T> {
    private boolean success;
    private int status;
    private String message;
    private T data;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    @Builder
    private ApiResponse(boolean success, int status, String message, T data) {
        this.success = success;
        this.status = status;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    // 성공 응답 생성 메서드들
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .status(HttpStatus.OK.value())
                .message("요청이 성공적으로 처리되었습니다.")
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .status(HttpStatus.OK.value())
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> success(T data, HttpStatus status) {
        return ApiResponse.<T>builder()
                .success(true)
                .status(status.value())
                .message("요청이 성공적으로 처리되었습니다.")
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> success(T data, HttpStatus status, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .status(status.value())
                .message(message)
                .data(data)
                .build();
    }

    // 실패 응답 생성 메서드들
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .status(HttpStatus.BAD_REQUEST.value())
                .message(message)
                .build();
    }

    public static <T> ApiResponse<T> error(String message, HttpStatus status) {
        return ApiResponse.<T>builder()
                .success(false)
                .status(status.value())
                .message(message)
                .build();
    }

    public static <T> ApiResponse<T> error(T data, HttpStatus status) {
        return ApiResponse.<T>builder()
                .success(false)
                .status(status.value())
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> error(Map<String, Object> errorDetails, HttpStatus status) {
        return ApiResponse.<T>builder()
                .success(false)
                .status(status.value())
                .message((String) errorDetails.get("message"))
                .data(null)
                .build();
    }
} 