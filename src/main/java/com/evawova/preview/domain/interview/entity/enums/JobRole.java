package com.evawova.preview.domain.interview.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JobRole {
    FRONTEND_DEVELOPER("프론트엔드 개발자", "Frontend Developer"),
    BACKEND_DEVELOPER("백엔드 개발자", "Backend Developer"),
    FULLSTACK_DEVELOPER("풀스택 개발자", "Fullstack Developer"),
    MOBILE_DEVELOPER("모바일 개발자", "Mobile Developer"),
    DEVOPS_DEVELOPER("데브옵스 개발자", "DevOps Developer"),
    DATA_SCIENTIST("데이터 사이언티스트", "Data Scientist"),
    AI_ENGINEER("AI 엔지니어", "AI Engineer"),
    SECURITY_ENGINEER("보안 엔지니어", "Security Engineer"),
    QA_ENGINEER("QA 엔지니어", "QA Engineer"),
    UI_UX_DESIGNER("UI/UX 디자이너", "UI/UX Designer"),
    GRAPHIC_DESIGNER("그래픽 디자이너", "Graphic Designer"),
    PRODUCT_DESIGNER("제품 디자이너", "Product Designer"),
    BRAND_DESIGNER("브랜드 디자이너", "Brand Designer"),
    DIGITAL_MARKETER("디지털 마케터", "Digital Marketer"),
    CONTENT_MARKETER("콘텐츠 마케터", "Content Marketer"),
    BRAND_MARKETER("브랜드 마케터", "Brand Marketer"),
    GROWTH_HACKER("그로스 해커", "Growth Hacker"),
    HR_MANAGER("인사 관리자", "HR Manager"),
    FINANCE_MANAGER("재무 관리자", "Finance Manager"),
    BUSINESS_DEVELOPMENT("비즈니스 개발", "Business Development"),
    PROJECT_MANAGER("프로젝트 관리자", "Project Manager");

    private final String korDescription;
    private final String engDescription;
}