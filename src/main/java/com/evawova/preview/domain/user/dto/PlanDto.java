package com.evawova.preview.domain.user.dto;

import com.evawova.preview.domain.user.entity.Plan;
import com.evawova.preview.domain.user.entity.PlanType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlanDto {
    private Long id;
    private String name;
    private PlanType type;
    private Integer monthlyPrice;
    private Integer annualPrice;
    private Integer storageSizeGB;
    private Integer maxProjectCount;
    private Boolean teamCollaboration;
    private Boolean prioritySupport;
    private Boolean customDomain;

    public static PlanDto fromEntity(Plan plan) {
        return PlanDto.builder()
                .id(plan.getId())
                .name(plan.getName())
                .type(plan.getType())
                .monthlyPrice(plan.getMonthlyPrice())
                .annualPrice(plan.getAnnualPrice())
                .storageSizeGB(plan.getStorageSizeGB())
                .maxProjectCount(plan.getMaxProjectCount())
                .teamCollaboration(plan.getTeamCollaboration())
                .prioritySupport(plan.getPrioritySupport())
                .customDomain(plan.getCustomDomain())
                .build();
    }
} 