package com.evawova.preview.domain.user.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PlanTest {

    @Test
    @DisplayName("새로운 플랜을 생성할 수 있다")
    void createPlan() {
        // given
        String name = "Premium Plan";
        PlanType type = PlanType.PREMIUM;
        Integer monthlyPrice = 10000;
        Integer annualPrice = 100000;
        Integer storageSizeGB = 100;
        Integer maxProjectCount = 100;
        Boolean teamCollaboration = true;
        Boolean prioritySupport = true;
        Boolean customDomain = true;

        // when
        Plan plan = Plan.createPlan(
            name,
            type,
            monthlyPrice,
            annualPrice,
            storageSizeGB,
            maxProjectCount,
            teamCollaboration,
            prioritySupport,
            customDomain
        );

        // then
        assertThat(plan.getName()).isEqualTo(name);
        assertThat(plan.getType()).isEqualTo(type);
        assertThat(plan.getMonthlyPrice()).isEqualTo(monthlyPrice);
        assertThat(plan.getAnnualPrice()).isEqualTo(annualPrice);
        assertThat(plan.getStorageSizeGB()).isEqualTo(storageSizeGB);
        assertThat(plan.getMaxProjectCount()).isEqualTo(maxProjectCount);
        assertThat(plan.getTeamCollaboration()).isEqualTo(teamCollaboration);
        assertThat(plan.getPrioritySupport()).isEqualTo(prioritySupport);
        assertThat(plan.getCustomDomain()).isEqualTo(customDomain);
    }
} 