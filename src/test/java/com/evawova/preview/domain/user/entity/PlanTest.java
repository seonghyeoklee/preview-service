package com.evawova.preview.domain.user.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PlanTest {

    @Test
    @DisplayName("새로운 플랜을 생성할 수 있다")
    void createPlan() {
        // when
        Plan plan = Plan.createPlan(
                "Standard",
                PlanType.STANDARD,
                9900,
                99000,
                50000,  // 50,000 토큰
                true
        );

        // then
        assertThat(plan.getName()).isEqualTo("Standard");
        assertThat(plan.getType()).isEqualTo(PlanType.STANDARD);
        assertThat(plan.getMonthlyPrice()).isEqualTo(9900);
        assertThat(plan.getAnnualPrice()).isEqualTo(99000);
        assertThat(plan.getMonthlyTokenLimit()).isEqualTo(50000);
        assertThat(plan.isActive()).isTrue();
    }

    @Test
    @DisplayName("플랜 정보를 업데이트할 수 있다")
    void updatePlan() {
        // given
        Plan plan = Plan.createPlan(
                "Standard",
                PlanType.STANDARD,
                9900,
                99000,
                50000,  // 50,000 토큰
                true
        );

        // when
        plan.updatePlan(
                "Pro",
                PlanType.PRO,
                19900,
                199000,
                100000, // 100,000 토큰
                true
        );

        // then
        assertThat(plan.getName()).isEqualTo("Pro");
        assertThat(plan.getType()).isEqualTo(PlanType.PRO);
        assertThat(plan.getMonthlyPrice()).isEqualTo(19900);
        assertThat(plan.getAnnualPrice()).isEqualTo(199000);
        assertThat(plan.getMonthlyTokenLimit()).isEqualTo(100000);
        assertThat(plan.isActive()).isTrue();
    }

    @Test
    @DisplayName("플랜을 비활성화할 수 있다")
    void deactivatePlan() {
        // given
        Plan plan = Plan.createPlan(
                "Standard",
                PlanType.STANDARD,
                9900,
                99000,
                50000,  // 50,000 토큰
                true
        );

        // when
        plan.deactivate();

        // then
        assertThat(plan.isActive()).isFalse();
    }

    @Test
    @DisplayName("무료 플랜은 가격이 0이어야 한다")
    void freePlanPriceValidation() {
        // when & then
        assertThatThrownBy(() -> Plan.createPlan(
                "Free",
                PlanType.FREE,
                1000,
                10000,
                10000,  // 10,000 토큰
                true
        )).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("무료 플랜의 가격은 0이어야 합니다");

        // when & then
        assertThatThrownBy(() -> Plan.createPlan(
                "Free",
                PlanType.FREE,
                0,
                10000,
                10000,  // 10,000 토큰
                true
        )).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("무료 플랜의 가격은 0이어야 합니다");

        // when & then
        assertThatThrownBy(() -> Plan.createPlan(
                "Free",
                PlanType.FREE,
                1000,
                0,
                10000,  // 10,000 토큰
                true
        )).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("무료 플랜의 가격은 0이어야 합니다");
    }

    @Test
    @DisplayName("프로 플랜은 최소 100,000개의 월간 토큰을 가져야 한다")
    void proPlanLimitsValidation() {
        // when & then
        assertThatThrownBy(() -> Plan.createPlan(
                "Pro",
                PlanType.PRO,
                19900,
                199000,
                50000,  // 50,000 토큰
                true
        )).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("프로 플랜은 최소 100,000개의 월간 토큰을 가질 수 있어야 합니다");
    }
} 