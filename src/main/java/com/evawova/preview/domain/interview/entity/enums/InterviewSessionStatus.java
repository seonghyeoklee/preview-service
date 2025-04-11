package com.evawova.preview.domain.interview.entity.enums;

import lombok.Getter;

@Getter
public enum InterviewSessionStatus {
    IN_PROGRESS("진행 중"),
    COMPLETED("완료"),
    CANCELLED("취소"),
    EXPIRED("만료");

    private final String description;

    InterviewSessionStatus(String description) {
        this.description = description;
    }
}