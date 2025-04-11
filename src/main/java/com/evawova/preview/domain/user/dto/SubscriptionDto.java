package com.evawova.preview.domain.user.dto;

import com.evawova.preview.domain.user.entity.Subscription;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionDto {
    private Long id;
    private Long userId;
    private PlanDto plan;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Subscription.SubscriptionStatus status;
    private BigDecimal paymentAmount;
    private Subscription.SubscriptionCycle cycle;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static SubscriptionDto fromEntity(Subscription subscription) {
        return SubscriptionDto.builder()
                .id(subscription.getId())
                .userId(subscription.getUser().getId())
                .plan(PlanDto.fromEntity(subscription.getPlan()))
                .startDate(subscription.getStartDate())
                .endDate(subscription.getEndDate())
                .status(subscription.getStatus())
                .paymentAmount(subscription.getPaymentAmount())
                .cycle(subscription.getCycle())
                .active(subscription.isActive())
                .createdAt(subscription.getCreatedAt())
                .updatedAt(subscription.getUpdatedAt())
                .build();
    }
}