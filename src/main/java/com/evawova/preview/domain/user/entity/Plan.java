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

    @Column(nullable = false)
    @Comment("플랜 이름")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Comment("플랜 타입")
    private PlanType type;

    @Column(nullable = false)
    @Comment("월간 가격")
    private Integer monthlyPrice;

    @Column(nullable = false)
    @Comment("연간 가격")
    private Integer annualPrice;

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
            String name,
            PlanType type,
            Integer monthlyPrice,
            Integer annualPrice,
            Integer monthlyTokenLimit,
            boolean active
    ) {
        validatePlan(type, monthlyPrice, annualPrice, monthlyTokenLimit);

        Plan plan = Plan.builder()
                .name(name)
                .type(type)
                .monthlyPrice(monthlyPrice)
                .annualPrice(annualPrice)
                .monthlyTokenLimit(monthlyTokenLimit)
                .active(active)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        // 도메인 이벤트 등록
        plan.registerEvent(new PlanCreatedEvent(plan.id, plan.name, plan.type));
        
        return plan;
    }

    public void updatePlan(
            String name,
            PlanType type,
            Integer monthlyPrice,
            Integer annualPrice,
            Integer monthlyTokenLimit,
            boolean active
    ) {
        validatePlan(type, monthlyPrice, annualPrice, monthlyTokenLimit);

        this.name = name;
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
            Integer monthlyPrice,
            Integer annualPrice,
            Integer monthlyTokenLimit
    ) {
        if (type == PlanType.FREE && (monthlyPrice > 0 || annualPrice > 0)) {
            throw new IllegalArgumentException("무료 플랜의 가격은 0이어야 합니다");
        }

        if (type == PlanType.PRO && monthlyTokenLimit < 100000) {
            throw new IllegalArgumentException("프로 플랜은 최소 100,000개의 월간 토큰을 가질 수 있어야 합니다");
        }
    }

    @Override
    public Long getId() {
        return this.id;
    }
} 