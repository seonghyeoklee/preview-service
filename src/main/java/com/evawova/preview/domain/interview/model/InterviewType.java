package com.evawova.preview.domain.interview.model;

/**
 * 면접 타입
 */
public enum InterviewType {
    DEVELOPMENT("개발"),
    DESIGN("디자인"),
    MARKETING("마케팅"),
    BUSINESS("경영지원"),
    SALES("영업/세일즈"),
    CUSTOMER_SERVICE("고객 지원"),
    MEDIA("미디어/콘텐츠"),
    EDUCATION("교육"),
    LOGISTICS("물류/유통");

    private final String displayName;

    InterviewType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}