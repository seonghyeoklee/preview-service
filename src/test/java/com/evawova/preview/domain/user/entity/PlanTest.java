package com.evawova.preview.domain.user.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

class PlanTest {

    @Test
    @DisplayName("새로운 플랜을 생성할 수 있다")
    void createPlan() {
        // when
        Plan plan = Plan.createPlan(
                PlanType.STANDARD,
                BigDecimal.valueOf(9900),
                BigDecimal.valueOf(99000),
                50000,
                true);

        // then
        assertThat(plan.getPlanType()).isEqualTo(PlanType.STANDARD);
        assertThat(plan.getMonthlyPrice()).isEqualTo(9900);
        assertThat(plan.getAnnualPrice()).isEqualTo(99000);
        assertThat(plan.getMonthlyTokenLimit()).isEqualTo(50000);
        assertThat(plan.getIsActive()).isTrue();
    }

    @Test
    @DisplayName("플랜 정보를 업데이트할 수 있다")
    void updatePlan() {
        // given
        Plan plan = Plan.createPlan(
                PlanType.STANDARD,
                BigDecimal.valueOf(9900),
                BigDecimal.valueOf(99000),
                50000,
                true);

        // when
        plan.updatePlan(
                PlanType.PRO,
                BigDecimal.valueOf(19900),
                BigDecimal.valueOf(199000),
                100000,
                true);

        // then
        assertThat(plan.getPlanType()).isEqualTo(PlanType.PRO);
        assertThat(plan.getMonthlyPrice()).isEqualTo(19900);
        assertThat(plan.getAnnualPrice()).isEqualTo(199000);
        assertThat(plan.getMonthlyTokenLimit()).isEqualTo(100000);
        assertThat(plan.getIsActive()).isTrue();
    }

    @Test
    @DisplayName("플랜을 비활성화할 수 있다")
    void deactivatePlan() {
        // given
        Plan plan = Plan.createPlan(
                PlanType.STANDARD,
                BigDecimal.valueOf(9900),
                BigDecimal.valueOf(99000),
                50000,
                true);

        // when
        plan.deactivate();

        // then
        assertThat(plan.getIsActive()).isFalse();
    }

}