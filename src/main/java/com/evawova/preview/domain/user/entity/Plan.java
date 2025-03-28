package com.evawova.preview.domain.user.entity;

import com.evawova.preview.domain.common.model.AggregateRoot;
import com.evawova.preview.domain.user.event.PlanCreatedEvent;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "plans")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Plan extends AggregateRoot<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlanType type;

    @Column(nullable = false)
    private Integer monthlyPrice;

    @Column(nullable = false)
    private Integer annualPrice;

    @Column(nullable = false)
    private Integer storageSizeGB;

    @Column(nullable = false)
    private Integer maxProjectCount;

    @Column(nullable = false)
    private Boolean teamCollaboration;

    @Column(nullable = false)
    private Boolean prioritySupport;

    @Column(nullable = false)
    private Boolean customDomain;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // 플랜 생성 메서드
    public static Plan createPlan(String name, PlanType type, Integer monthlyPrice, Integer annualPrice,
                                 Integer storageSizeGB, Integer maxProjectCount, Boolean teamCollaboration,
                                 Boolean prioritySupport, Boolean customDomain) {
        Plan plan = new Plan();
        plan.name = name;
        plan.type = type;
        plan.monthlyPrice = monthlyPrice;
        plan.annualPrice = annualPrice;
        plan.storageSizeGB = storageSizeGB;
        plan.maxProjectCount = maxProjectCount;
        plan.teamCollaboration = teamCollaboration;
        plan.prioritySupport = prioritySupport;
        plan.customDomain = customDomain;
        
        // 도메인 이벤트 등록
        plan.registerEvent(new PlanCreatedEvent(plan.id, plan.name, plan.type));
        
        return plan;
    }

    @Override
    public Long getId() {
        return this.id;
    }
} 