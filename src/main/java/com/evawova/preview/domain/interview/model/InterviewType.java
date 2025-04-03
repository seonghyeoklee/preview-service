package com.evawova.preview.domain.interview.model;

/**
 * 면접 타입
 */
public enum InterviewType {
    // IT 개발 직군 세분화
    BACKEND("백엔드 개발"),
    FRONTEND("프론트엔드 개발"),
    FULLSTACK("풀스택 개발"),
    MOBILE("모바일 앱 개발"),
    DEVOPS("데브옵스/인프라"),
    DATA_ENGINEERING("데이터 엔지니어링"),
    DATA_SCIENCE("데이터 사이언스/AI"),
    GAME_DEVELOPMENT("게임 개발"),
    BLOCKCHAIN("블록체인 개발"),
    EMBEDDED("임베디드/IoT"),
    QA("QA/테스트"),
    SECURITY("보안 엔지니어링"),

    // 기존 카테고리
    DESIGN("디자인"),
    MARKETING("마케팅"),
    BUSINESS("경영지원");

    private final String displayName;

    InterviewType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}