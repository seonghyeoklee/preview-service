package com.evawova.preview.domain.interview.model;

/**
 * 면접관 스타일
 */
public enum InterviewerStyle {
    FRIENDLY("친근한 면접관"),
    TECHNICAL("기술 중심 면접관"),
    CHALLENGING("도전적인 면접관");

    private final String displayName;

    InterviewerStyle(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
} 