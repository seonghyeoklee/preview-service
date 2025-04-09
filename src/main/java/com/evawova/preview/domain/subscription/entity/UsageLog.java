package com.evawova.preview.domain.subscription.entity;

import com.evawova.preview.domain.interview.entity.InterviewSession;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "usage_logs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class UsageLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id", nullable = false)
    @Comment("구독")
    private Subscription subscription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_usage_id", nullable = false)
    @Comment("구독 사용량")
    private SubscriptionUsage subscriptionUsage;

    @Column(nullable = false)
    @Comment("사용 유형")
    @Enumerated(EnumType.STRING)
    private UsageType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interview_session_id")
    @Comment("인터뷰 세션")
    private InterviewSession interviewSession;

    @Column(columnDefinition = "TEXT")
    @Comment("사용 내역 설명")
    private String description;

    @Column(nullable = false)
    @Comment("사용 일시")
    private LocalDateTime usedAt;

    @Column
    @Comment("IP 주소")
    private String ipAddress;

    @Column
    @Comment("사용자 에이전트")
    private String userAgent;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    @Comment("생성 시간")
    private LocalDateTime createdAt;

    public enum UsageType {
        INTERVIEW_START, // 인터뷰 시작
        INTERVIEW_COMPLETE, // 인터뷰 완료
        INTERVIEW_CANCEL, // 인터뷰 취소
        SUBSCRIPTION_RENEW, // 구독 갱신
        SUBSCRIPTION_CANCEL, // 구독 취소
        USAGE_RESET // 사용량 리셋
    }

    // 인터뷰 세션 로그 생성을 위한 팩토리 메서드
    public static UsageLog createInterviewStartLog(Subscription subscription,
            SubscriptionUsage usage,
            InterviewSession interviewSession,
            String ipAddress,
            String userAgent) {
        return UsageLog.builder()
                .subscription(subscription)
                .subscriptionUsage(usage)
                .type(UsageType.INTERVIEW_START)
                .interviewSession(interviewSession)
                .description("인터뷰 세션 시작")
                .usedAt(LocalDateTime.now())
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();
    }

    // 인터뷰 완료 로그 생성을 위한 팩토리 메서드
    public static UsageLog createInterviewCompleteLog(Subscription subscription,
            SubscriptionUsage usage,
            InterviewSession interviewSession) {
        return UsageLog.builder()
                .subscription(subscription)
                .subscriptionUsage(usage)
                .type(UsageType.INTERVIEW_COMPLETE)
                .interviewSession(interviewSession)
                .description("인터뷰 세션 완료")
                .usedAt(LocalDateTime.now())
                .build();
    }

    // 구독 갱신 로그 생성을 위한 팩토리 메서드
    public static UsageLog createSubscriptionRenewLog(Subscription subscription,
            SubscriptionUsage usage) {
        return UsageLog.builder()
                .subscription(subscription)
                .subscriptionUsage(usage)
                .type(UsageType.SUBSCRIPTION_RENEW)
                .description("구독 갱신")
                .usedAt(LocalDateTime.now())
                .build();
    }
}