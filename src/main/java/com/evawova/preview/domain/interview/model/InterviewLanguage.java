package com.evawova.preview.domain.interview.model;

/**
 * 면접 언어
 */
public enum InterviewLanguage {
    KO("한국어"),
    EN("영어");

    private final String displayName;

    InterviewLanguage(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}