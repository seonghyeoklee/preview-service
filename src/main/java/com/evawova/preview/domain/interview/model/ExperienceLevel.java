package com.evawova.preview.domain.interview.model;

/**
 * 경력 수준
 */
public enum ExperienceLevel {
    ENTRY("신입"),
    JUNIOR("주니어 (1-3년)"),
    MID_LEVEL("미드레벨 (4-7년)"),
    SENIOR("시니어 (8년 이상)"),
    EXECUTIVE("임원급");

    private final String displayName;

    ExperienceLevel(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
} 