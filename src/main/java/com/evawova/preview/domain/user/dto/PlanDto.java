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
    private String name;
    private PlanType type;
    private Integer monthlyPrice;
    private Integer annualPrice;
    private Integer monthlyTokenLimit;
    private boolean active;
    
    public static PlanDto fromEntity(Plan plan) {
        return PlanDto.builder()
                .id(plan.getId())
                .name(plan.getName())
                .type(plan.getType())
                .monthlyPrice(plan.getMonthlyPrice())
                .annualPrice(plan.getAnnualPrice())
                .monthlyTokenLimit(plan.getMonthlyTokenLimit())
                .active(plan.isActive())
                .build();
    }
} 