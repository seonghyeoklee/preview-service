package com.evawova.preview.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiException extends RuntimeException {
    private final HttpStatus status;
    private final String message;
    private final Object data;

    public ApiException(String message) {
        this(HttpStatus.BAD_REQUEST, message, null);
    }

    public ApiException(HttpStatus status, String message) {
        this(status, message, null);
    }

    public ApiException(HttpStatus status, String message, Object data) {
        super(message);
        this.status = status;
        this.message = message;
        this.data = data;
    }
} 