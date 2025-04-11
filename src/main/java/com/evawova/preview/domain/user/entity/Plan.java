package com.evawova.preview.domain.user.entity;

import com.evawova.preview.domain.common.model.AggregateRoot;
import com.evawova.preview.domain.user.event.PlanCreatedEvent;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "plans")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Plan extends AggregateRoot<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("플랜 고유 식별자")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "plan_type")
    @Comment("플랜 타입")
    private PlanType planType;

    @Column(nullable = false, name = "monthly_price")
    @Comment("월간 가격")
    private BigDecimal monthlyPrice;

    @Column(nullable = false, name = "annual_price")
    @Comment("연간 가격")
    private BigDecimal annualPrice;

    @Column(nullable = false, name = "monthly_token_limit")
    @Comment("월간 토큰 사용량 제한")
    private Integer monthlyTokenLimit;

    @Column(nullable = false, name = "is_active")
    @Comment("활성화 여부")
    private Boolean isActive;

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL)
    @Builder.Default
    @Comment("플랜의 구독 목록")
    private List<Subscription> subscriptions = new ArrayList<>();

    @CreatedDate
    @Column(nullable = false, name = "created_at")
    @Comment("생성일시")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false, name = "updated_at")
    @Comment("수정일시")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public static Plan createPlan(
            PlanType planType,
            BigDecimal monthlyPrice,
            BigDecimal annualPrice,
            Integer monthlyTokenLimit,
            Boolean isActive) {
        validatePlan(planType, monthlyPrice, annualPrice, monthlyTokenLimit);

        Plan plan = Plan.builder()
                .planType(planType)
                .monthlyPrice(monthlyPrice)
                .annualPrice(annualPrice)
                .monthlyTokenLimit(monthlyTokenLimit)
                .isActive(isActive)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // 도메인 이벤트 등록
        plan.registerEvent(new PlanCreatedEvent(plan.id, plan.planType));

        return plan;
    }

    public void updatePlan(
            PlanType planType,
            BigDecimal monthlyPrice,
            BigDecimal annualPrice,
            Integer monthlyTokenLimit,
            Boolean isActive) {
        validatePlan(planType, monthlyPrice, annualPrice, monthlyTokenLimit);

        this.planType = planType;
        this.monthlyPrice = monthlyPrice;
        this.annualPrice = annualPrice;
        this.monthlyTokenLimit = monthlyTokenLimit;
        this.isActive = isActive;
    }

    public void deactivate() {
        this.isActive = false;
    }

    private static void validatePlan(
            PlanType planType,
            BigDecimal monthlyPrice,
            BigDecimal annualPrice,
            Integer monthlyTokenLimit) {
        if (planType == PlanType.FREE
                && (monthlyPrice.compareTo(BigDecimal.ZERO) > 0 || annualPrice.compareTo(BigDecimal.ZERO) > 0)) {
            throw new IllegalArgumentException("무료 플랜의 가격은 0이어야 합니다");
        }

        if (planType == PlanType.PRO && monthlyTokenLimit < 100000) {
            throw new IllegalArgumentException("프로 플랜은 최소 100,000개의 월간 토큰을 가질 수 있어야 합니다");
        }
    }

    public BigDecimal getPriceForCycle(Subscription.SubscriptionCycle cycle) {
        return cycle == Subscription.SubscriptionCycle.MONTHLY ? monthlyPrice : annualPrice;
    }
}