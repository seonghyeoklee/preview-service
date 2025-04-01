package com.evawova.preview.domain.interview.model;

/**
 * 면접 난이도
 */
public enum InterviewDifficulty {
    BEGINNER("초급"),
    INTERMEDIATE("중급"),
    ADVANCED("고급"),
    EXPERT("전문가");

    private final String displayName;

    InterviewDifficulty(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
} 