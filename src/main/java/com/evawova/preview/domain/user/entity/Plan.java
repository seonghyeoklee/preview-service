package com.evawova.preview.domain.user.entity;

import com.evawova.preview.domain.common.model.AggregateRoot;
import com.evawova.preview.domain.user.event.PlanCreatedEvent;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "plans")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Plan extends AggregateRoot<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("플랜 고유 식별자")
    private Long id;

    @Column(nullable = false, unique = true)
    @Comment("플랜 타입 (FREE, PREMIUM)")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Comment("플랜 타입 (FREE, PREMIUM)")
    private PlanType type;

    @Column(nullable = false)
    @Comment("플랜 가격 (월 단위)")
    private Integer monthlyPrice;

    @Column(nullable = false)
    @Comment("플랜 활성화 상태")
    private Integer annualPrice;

    @Column(nullable = false)
    @Comment("플랜 저장 용량 (GB)")
    private Integer storageSizeGB;

    @Column(nullable = false)
    @Comment("플랜 프로젝트 수")
    private Integer maxProjectCount;

    @Column(nullable = false)
    @Comment("플랜 팀 협업 여부")
    private Boolean teamCollaboration;

    @Column(nullable = false)
    @Comment("플랜 우선 지원 여부")
    private Boolean prioritySupport;

    @Column(nullable = false)
    @Comment("플랜 커스텀 도메인 여부")
    private Boolean customDomain;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    @Comment("플랜 생성 시간")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    @Comment("플랜 정보 수정 시간")
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