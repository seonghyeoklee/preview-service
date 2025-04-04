package com.evawova.preview.global.api;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * API 응답을 표준화하는 클래스
 * 
 * @param <T> 응답 데이터 타입
 */
@Getter
@NoArgsConstructor
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private ErrorResponse error;

    private ApiResponse(boolean success, String message, T data, ErrorResponse error) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.error = error;
    }

    /**
     * 성공 응답 생성 (데이터만)
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "요청이 성공적으로 처리되었습니다.", data, null);
    }

    /**
     * 성공 응답 생성 (메시지 포함)
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, null);
    }

    /**
     * 실패 응답 생성 (메시지만)
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null, null);
    }

    /**
     * 실패 응답 생성 (에러 코드와 메시지)
     */
    public static <T> ApiResponse<T> error(String code, String message) {
        ErrorResponse errorResponse = new ErrorResponse(code, message);
        return new ApiResponse<>(false, "요청 처리 중 오류가 발생했습니다.", null, errorResponse);
    }

    /**
     * 실패 응답 생성 (에러 객체)
     */
    public static <T> ApiResponse<T> error(ErrorResponse errorResponse) {
        return new ApiResponse<>(false, "요청 처리 중 오류가 발생했습니다.", null, errorResponse);
    }

    /**
     * API 오류 응답 정보
     */
    @Getter
    public static class ErrorResponse {
        private String code;
        private String message;

        public ErrorResponse(String code, String message) {
            this.code = code;
            this.message = message;
        }
    }
}