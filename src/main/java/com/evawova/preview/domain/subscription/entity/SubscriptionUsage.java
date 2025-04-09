package com.evawova.preview.domain.subscription.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;

@Entity
@Table(name = "subscription_usages", uniqueConstraints = @UniqueConstraint(columnNames = { "subscription_id",
        "year_month" }))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class SubscriptionUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id", nullable = false)
    @Comment("구독")
    private Subscription subscription;

    @Column(name = "year_month", nullable = false)
    @Comment("청구 주기 (YYYY-MM)")
    private String yearMonth;

    @Column(nullable = false)
    @Comment("사용한 면접 횟수")
    private int interviewsUsed;

    @Column(nullable = false)
    @Comment("남은 면접 횟수")
    private int interviewsRemaining;

    @Column(nullable = false)
    @Comment("사용량 리셋일")
    private LocalDate resetDate;

    @Column(nullable = false)
    @Comment("최대 면접 횟수")
    private int maxInterviews;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    @Comment("생성 시간")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    @Comment("수정 시간")
    private LocalDateTime updatedAt;

    // 사용량 증가
    public boolean useInterview() {
        if (interviewsRemaining <= 0) {
            return false;
        }

        this.interviewsUsed++;
        this.interviewsRemaining--;
        return true;
    }

    // 사용량 리셋
    public void reset(int maxInterviews, LocalDate nextResetDate) {
        this.interviewsUsed = 0;
        this.interviewsRemaining = maxInterviews;
        this.maxInterviews = maxInterviews;
        this.resetDate = nextResetDate;

        // 연도와 월 업데이트
        YearMonth currentYearMonth = YearMonth.now();
        this.yearMonth = currentYearMonth.toString();
    }

    // 현재 사용률 계산 (%)
    public int getUsagePercentage() {
        if (maxInterviews <= 0) {
            return 0;
        }
        return (int) (((float) interviewsUsed / maxInterviews) * 100);
    }
}