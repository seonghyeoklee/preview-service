package com.evawova.preview.domain.interview.model;

/**
 * 직무 역할
 */
public enum JobRole {
    FRONTEND_DEVELOPER("프론트엔드 개발자"),
    BACKEND_DEVELOPER("백엔드 개발자"),
    FULLSTACK_DEVELOPER("풀스택 개발자"),
    MOBILE_DEVELOPER("모바일 개발자"),
    DEVOPS_DEVELOPER("DevOps 엔지니어"),
    DATA_SCIENTIST("데이터 사이언티스트"),
    AI_ENGINEER("AI/ML 엔지니어"),
    SECURITY_ENGINEER("보안 엔지니어"),
    QA_ENGINEER("QA 엔지니어"),
    UI_UX_DESIGNER("UI/UX 디자이너"),
    GRAPHIC_DESIGNER("그래픽 디자이너"),
    PRODUCT_DESIGNER("제품 디자이너"),
    BRAND_DESIGNER("브랜드 디자이너"),
    DIGITAL_MARKETER("디지털 마케터"),
    CONTENT_MARKETER("콘텐츠 마케터"),
    BRAND_MARKETER("브랜드 마케터"),
    GROWTH_HACKER("그로스 해커"),
    HR_MANAGER("인사 담당자"),
    FINANCE_MANAGER("재무 담당자"),
    BUSINESS_DEVELOPMENT("사업 개발자"),
    PROJECT_MANAGER("프로젝트 관리자");

    private final String displayName;

    JobRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
} 