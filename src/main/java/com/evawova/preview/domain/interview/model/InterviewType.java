package com.evawova.preview.domain.interview.model;

/**
 * 면접 타입
 */
public enum InterviewType {
    TECHNICAL("기술 면접 (IT 개발)"),
    DESIGN("디자인 면접"),
    MARKETING("마케팅 면접"),
    BUSINESS("경영 면접");

    private final String displayName;

    InterviewType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
} 