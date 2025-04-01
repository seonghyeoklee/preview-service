package com.evawova.preview.domain.interview.model;

/**
 * 면접 시간
 */
public enum InterviewDuration {
    SHORT("15분 (짧은 면접)"),
    MEDIUM("30분 (일반 면접)"),
    LONG("45분 (심층 면접)"),
    EXTENDED("60분 (확장 면접)");

    private final String displayName;

    InterviewDuration(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
} 