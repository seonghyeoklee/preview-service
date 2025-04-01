package com.evawova.preview.domain.interview.model;

/**
 * 인터뷰 모드
 */
public enum InterviewMode {
    TEXT("텍스트 기반 면접"),
    VOICE("음성 기반 면접");

    private final String displayName;

    InterviewMode(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
} 