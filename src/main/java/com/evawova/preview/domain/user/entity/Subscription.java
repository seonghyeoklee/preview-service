package com.evawova.preview.domain.user.entity;

import com.evawova.preview.domain.common.model.AggregateRoot;
import com.evawova.preview.domain.user.event.SubscriptionCancelledEvent;
import com.evawova.preview.domain.user.event.SubscriptionCreatedEvent;
import com.evawova.preview.domain.user.event.SubscriptionRenewedEvent;
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
@Table(name = "subscriptions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Subscription extends AggregateRoot<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("구독 고유 식별자")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @Comment("구독 사용자")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    @Comment("구독 플랜")
    private Plan plan;

    @Column(nullable = false)
    @Comment("구독 시작일")
    private LocalDateTime startDate;

    @Column(nullable = false)
    @Comment("구독 종료일")
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Comment("구독 상태")
    private SubscriptionStatus status;

    @Column(nullable = false)
    @Comment("구독 결제 금액")
    private BigDecimal paymentAmount;

    @Column(nullable = false)
    @Comment("구독 결제 주기")
    private SubscriptionCycle cycle;

    @Column(nullable = false)
    @Comment("구독 활성화 여부")
    private boolean active;

    @Column(nullable = false)
    @Comment("생성일시")
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @Comment("수정일시")
    private LocalDateTime updatedAt;

    public enum SubscriptionStatus {
        ACTIVE,
        CANCELLED,
        EXPIRED,
        PENDING
    }

    public enum SubscriptionCycle {
        MONTHLY,
        ANNUAL
    }

    public static Subscription createSubscription(
            User user,
            Plan plan,
            LocalDateTime startDate,
            LocalDateTime endDate,
            BigDecimal paymentAmount,
            SubscriptionCycle cycle) {

        Subscription subscription = Subscription.builder()
                .user(user)
                .plan(plan)
                .startDate(startDate)
                .endDate(endDate)
                .status(SubscriptionStatus.ACTIVE)
                .paymentAmount(paymentAmount)
                .cycle(cycle)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // 도메인 이벤트 등록
        subscription.registerEvent(new SubscriptionCreatedEvent(subscription));

        return subscription;
    }

    public void cancel() {
        this.status = SubscriptionStatus.CANCELLED;
        this.active = false;
        this.updatedAt = LocalDateTime.now();

        // 도메인 이벤트 등록
        this.registerEvent(new SubscriptionCancelledEvent(this, "User cancelled subscription"));
    }

    public void expire() {
        this.status = SubscriptionStatus.EXPIRED;
        this.active = false;
        this.updatedAt = LocalDateTime.now();

        // 도메인 이벤트 등록
        this.registerEvent(new SubscriptionCancelledEvent(this, "Subscription expired"));
    }

    public void renew(LocalDateTime newEndDate) {
        this.endDate = newEndDate;
        this.status = SubscriptionStatus.ACTIVE;
        this.active = true;
        this.updatedAt = LocalDateTime.now();

        // 도메인 이벤트 등록
        this.registerEvent(new SubscriptionRenewedEvent(this));
    }

    public boolean isActive() {
        return this.active && this.status == SubscriptionStatus.ACTIVE;
    }
}