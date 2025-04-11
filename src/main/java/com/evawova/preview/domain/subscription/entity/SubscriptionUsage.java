package com.evawova.preview.domain.subscription.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "subscription_usages")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class SubscriptionUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String yearMonth;

    @Column(nullable = false)
    private Integer interviewsUsed;

    @Column(nullable = false)
    private Integer interviewsRemaining;

    @Column(nullable = false)
    private LocalDate resetDate;

    @Column(nullable = false)
    private Integer maxInterviews;

    public boolean useInterview() {
        if (interviewsRemaining <= 0) {
            return false;
        }
        interviewsUsed++;
        interviewsRemaining--;
        return true;
    }

    public void reset(int maxInterviews, LocalDate nextResetDate) {
        this.interviewsUsed = 0;
        this.interviewsRemaining = maxInterviews;
        this.maxInterviews = maxInterviews;
        this.resetDate = nextResetDate;
    }

    public double getUsagePercentage() {
        return (double) interviewsUsed / maxInterviews * 100;
    }
}