package com.evawova.preview.domain.user.entity;

public enum SubscriptionStatus {
    ACTIVE,         // 활성 구독
    CANCELLED,      // 구독 취소
    EXPIRED,        // 구독 만료
    PAST_DUE,       // 결제 지연
    TRIAL,          // 체험 기간
    PENDING         // 결제 대기
} 