package com.evawova.preview.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Entity
@Table(name = "subscriptions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
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

    @Column
    @Comment("결제 제공자 (예: STRIPE, PAYPAL)")
    private String paymentProvider;

    @Column
    @Comment("결제 제공자의 구독 ID")
    private String subscriptionId;

    @Column
    @Comment("마지막 결제일")
    private LocalDateTime lastPaymentDate;

    @Column
    @Comment("다음 결제일")
    private LocalDateTime nextPaymentDate;

    @Column
    @Comment("결제 금액")
    private Integer amount;

    @Column
    @Comment("결제 주기 (MONTHLY, YEARLY)")
    private String billingCycle;

    @Builder
    public Subscription(Long id, User user, Plan plan, LocalDateTime startDate, LocalDateTime endDate, SubscriptionStatus status,
                       String paymentProvider, String subscriptionId, Integer amount, String billingCycle) {
        this.id = id;
        this.user = user;
        this.plan = plan;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.paymentProvider = paymentProvider;
        this.subscriptionId = subscriptionId;
        this.lastPaymentDate = LocalDateTime.now();
        this.nextPaymentDate = billingCycle.equals("MONTHLY") ? 
            LocalDateTime.now().plusMonths(1) : LocalDateTime.now().plusYears(1);
        this.amount = amount;
        this.billingCycle = billingCycle;
    }

    public void cancel() {
        this.status = SubscriptionStatus.CANCELLED;
        this.endDate = LocalDateTime.now();
    }

    public void renew() {
        this.status = SubscriptionStatus.ACTIVE;
        this.lastPaymentDate = LocalDateTime.now();
        this.nextPaymentDate = billingCycle.equals("MONTHLY") ? 
            LocalDateTime.now().plusMonths(1) : LocalDateTime.now().plusYears(1);
    }

    public boolean isActive() {
        return status == SubscriptionStatus.ACTIVE && 
               LocalDateTime.now().isBefore(endDate);
    }

    public void updateStatus(SubscriptionStatus status) {
        this.status = status;
        if (status == SubscriptionStatus.CANCELLED) {
            this.endDate = LocalDateTime.now();
        }
    }
} 