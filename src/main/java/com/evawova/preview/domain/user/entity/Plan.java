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

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    @Column(nullable = false)
    @Comment("플랜 타입")
    private PlanType type;

    @Column(nullable = false)
    @Comment("월간 가격")
    private BigDecimal monthlyPrice;

    @Column(nullable = false)
    @Comment("연간 가격")
    private BigDecimal annualPrice;

    @Column(nullable = false)
    @Comment("월간 토큰 사용량 제한")
    private Integer monthlyTokenLimit;

    @Column(nullable = false)
    @Comment("활성화 여부")
    private boolean active;

    @Column(nullable = false)
    @Comment("생성일시")
    private LocalDateTime createdAt;

    @Column(nullable = false)
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
            PlanType type,
            BigDecimal monthlyPrice,
            BigDecimal annualPrice,
            Integer monthlyTokenLimit,
            boolean active) {
        validatePlan(type, monthlyPrice, annualPrice, monthlyTokenLimit);

        Plan plan = Plan.builder()
                .type(type)
                .monthlyPrice(monthlyPrice)
                .annualPrice(annualPrice)
                .monthlyTokenLimit(monthlyTokenLimit)
                .active(active)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // 도메인 이벤트 등록
        plan.registerEvent(new PlanCreatedEvent(plan.id, plan.type));

        return plan;
    }

    public void updatePlan(
            PlanType type,
            BigDecimal monthlyPrice,
            BigDecimal annualPrice,
            Integer monthlyTokenLimit,
            boolean active) {
        validatePlan(type, monthlyPrice, annualPrice, monthlyTokenLimit);

        this.type = type;
        this.monthlyPrice = monthlyPrice;
        this.annualPrice = annualPrice;
        this.monthlyTokenLimit = monthlyTokenLimit;
        this.active = active;
    }

    public void deactivate() {
        this.active = false;
    }

    private static void validatePlan(
            PlanType type,
            BigDecimal monthlyPrice,
            BigDecimal annualPrice,
            Integer monthlyTokenLimit) {
        if (type == PlanType.FREE
                && (monthlyPrice.compareTo(BigDecimal.ZERO) > 0 || annualPrice.compareTo(BigDecimal.ZERO) > 0)) {
            throw new IllegalArgumentException("무료 플랜의 가격은 0이어야 합니다");
        }

        if (type == PlanType.PRO && monthlyTokenLimit < 100000) {
            throw new IllegalArgumentException("프로 플랜은 최소 100,000개의 월간 토큰을 가질 수 있어야 합니다");
        }
    }
}