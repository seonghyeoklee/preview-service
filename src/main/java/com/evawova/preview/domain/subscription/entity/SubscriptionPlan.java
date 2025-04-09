package com.evawova.preview.domain.subscription.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "subscription_plans")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class SubscriptionPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @Comment("플랜 코드")
    private String code;

    @Column(nullable = false)
    @Comment("플랜 이름")
    private String name;

    @Column(nullable = false)
    @Comment("플랜 이름(영문)")
    private String nameEn;

    @Column(columnDefinition = "TEXT")
    @Comment("플랜 설명")
    private String description;

    @Column(columnDefinition = "TEXT")
    @Comment("플랜 설명(영문)")
    private String descriptionEn;

    @Column(nullable = false)
    @Comment("월 요금")
    private BigDecimal monthlyPrice;

    @Column(nullable = false)
    @Comment("통화")
    private String currency;

    @Column(nullable = false)
    @Comment("한달 최대 면접 횟수")
    private Integer maxInterviewsPerMonth;

    @Column(nullable = false)
    @Comment("하루 최대 면접 횟수")
    private Integer maxInterviewsPerDay;

    @Column(nullable = false)
    @Comment("기술 면접 지원 여부")
    private boolean technicalInterviewEnabled;

    @Column(nullable = false)
    @Comment("디자인 면접 지원 여부")
    private boolean designInterviewEnabled;

    @Column(nullable = false)
    @Comment("마케팅 면접 지원 여부")
    private boolean marketingInterviewEnabled;

    @Column(nullable = false)
    @Comment("비즈니스 면접 지원 여부")
    private boolean businessInterviewEnabled;

    @Column(nullable = false)
    @Comment("활성화 여부")
    private boolean active;

    @Column(nullable = false)
    @Comment("정렬 순서")
    private int sortOrder;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    @Comment("생성 시간")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    @Comment("수정 시간")
    private LocalDateTime updatedAt;
}