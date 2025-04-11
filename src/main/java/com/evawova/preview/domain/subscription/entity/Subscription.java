package com.evawova.preview.domain.subscription.entity;

import com.evawova.preview.domain.user.entity.Plan;
import com.evawova.preview.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "subscriptions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @Comment("구독자")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    @Comment("구독 플랜")
    private Plan plan;

    @Column(nullable = false)
    @Comment("구독 상태")
    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status;

    @Column(nullable = false)
    @Comment("구독 시작일")
    private LocalDate startDate;

    @Column(nullable = false)
    @Comment("구독 종료일")
    private LocalDate endDate;

    @Column(nullable = false)
    @Comment("다음 갱신일")
    private LocalDate nextRenewalDate;

    @Column(nullable = false)
    @Comment("자동 갱신 여부")
    private boolean autoRenewal;

    @Column
    @Comment("취소일")
    private LocalDate cancelledAt;

    @Column
    @Comment("취소 사유")
    private String cancellationReason;

    @Column
    @Comment("결제 방법")
    private String paymentMethod;

    @Column
    @Comment("결제 ID")
    private String paymentId;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    @Comment("생성 시간")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    @Comment("수정 시간")
    private LocalDateTime updatedAt;

    public enum SubscriptionStatus {
        ACTIVE, // 활성
        CANCELLED, // 취소됨
        EXPIRED, // 만료됨
        SUSPENDED, // 일시 중지
        TRIAL // 체험판
    }

    // 구독 취소
    public void cancel(String reason) {
        this.status = SubscriptionStatus.CANCELLED;
        this.cancelledAt = LocalDate.now();
        this.cancellationReason = reason;
        this.autoRenewal = false;
    }

    // 구독 갱신
    public void renew(LocalDate newEndDate) {
        this.startDate = this.endDate.plusDays(1);
        this.endDate = newEndDate;
        this.nextRenewalDate = newEndDate.plusDays(1);
    }

    // 구독 만료 처리
    public void expire() {
        this.status = SubscriptionStatus.EXPIRED;
        this.autoRenewal = false;
    }

    // 자동 갱신 설정 변경
    public void setAutoRenewal(boolean autoRenewal) {
        this.autoRenewal = autoRenewal;
    }
}