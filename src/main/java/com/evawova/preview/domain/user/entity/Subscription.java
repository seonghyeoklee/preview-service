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
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

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

    @Column(nullable = false, name = "start_date")
    @Comment("구독 시작 날짜")
    private LocalDateTime startDate;

    @Column(nullable = false, name = "end_date")
    @Comment("구독 종료 날짜")
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "status")
    @Comment("구독 상태 (ACTIVE, CANCELLED, EXPIRED)")
    private SubscriptionStatus status;

    @Column(nullable = false, name = "payment_amount")
    @Comment("구독 결제 금액")
    private BigDecimal paymentAmount;

    @Column(nullable = false, name = "subscription_cycle")
    @Comment("구독 결제 주기")
    private SubscriptionCycle subscriptionCycle;

    @Column(nullable = false, name = "is_active")
    @Comment("구독 활성화 여부")
    private Boolean isActive;

    @Column(name = "stripe_subscription_id", unique = true)
    @Comment("Stripe 구독 ID")
    private String stripeSubscriptionId;

    @Column(name = "renewal_date")
    @Comment("다음 갱신 날짜")
    private LocalDateTime renewalDate;

    @CreatedDate
    @Column(nullable = false, updatable = false, name = "created_at")
    @Comment("생성 시간")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false, name = "updated_at")
    @Comment("수정 시간")
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
            SubscriptionCycle subscriptionCycle) {

        Subscription subscription = Subscription.builder()
                .user(user)
                .plan(plan)
                .startDate(startDate)
                .endDate(endDate)
                .status(SubscriptionStatus.ACTIVE)
                .paymentAmount(paymentAmount)
                .subscriptionCycle(subscriptionCycle)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // 도메인 이벤트 등록
        subscription.registerEvent(new SubscriptionCreatedEvent(subscription));

        return subscription;
    }

    public void cancel() {
        this.status = SubscriptionStatus.CANCELLED;
        this.isActive = false;
        this.updatedAt = LocalDateTime.now();

        // 도메인 이벤트 등록
        this.registerEvent(new SubscriptionCancelledEvent(this, "User cancelled subscription"));
    }

    public void expire() {
        this.status = SubscriptionStatus.EXPIRED;
        this.isActive = false;
        this.updatedAt = LocalDateTime.now();

        // 도메인 이벤트 등록
        this.registerEvent(new SubscriptionCancelledEvent(this, "Subscription expired"));
    }

    public void renew(LocalDateTime newEndDate) {
        this.endDate = newEndDate;
        this.status = SubscriptionStatus.ACTIVE;
        this.isActive = true;
        this.updatedAt = LocalDateTime.now();

        // 도메인 이벤트 등록
        this.registerEvent(new SubscriptionRenewedEvent(this));
    }

    public void updateRenewal(LocalDateTime newEndDate, LocalDateTime newRenewalDate) {
        this.endDate = newEndDate;
        this.renewalDate = newRenewalDate;
        this.status = SubscriptionStatus.ACTIVE; // Assuming renewal implies active
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isActive() {
        return this.isActive && this.status == SubscriptionStatus.ACTIVE;
    }
}