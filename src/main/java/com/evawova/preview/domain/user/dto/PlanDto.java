package com.evawova.preview.domain.user.dto;

import com.evawova.preview.domain.user.entity.Plan;
import com.evawova.preview.domain.user.entity.PlanType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanDto {
    private Long id;
    private PlanType planType;
    private Integer monthlyPrice;
    private Integer annualPrice;
    private Integer monthlyTokenLimit;
    private Boolean isActive;

    public static PlanDto fromEntity(Plan plan) {
        return PlanDto.builder()
                .id(plan.getId())
                .planType(plan.getPlanType())
                .monthlyPrice(plan.getMonthlyPrice().intValue())
                .annualPrice(plan.getAnnualPrice().intValue())
                .monthlyTokenLimit(plan.getMonthlyTokenLimit())
                .isActive(plan.getIsActive())
                .build();
    }
}