package com.evawova.preview.domain.user.entity;

import com.evawova.preview.domain.user.event.UserCreatedEvent;
import com.evawova.preview.domain.user.event.UserPlanChangedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    private Plan freePlan;
    private Plan standardPlan;

    @BeforeEach
    void setUp() {
        // 테스트를 위한 플랜 데이터 설정
        freePlan = Plan.createPlan(
                "Free",
                PlanType.FREE,
                0,
                0,
                1,
                3,
                false,
                false,
                false
        );
        standardPlan = Plan.createPlan(
                "Standard",
                PlanType.STANDARD,
                9900,
                99000,
                10,
                10,
                true,
                false,
                false
        );
    }

    @Test
    @DisplayName("사용자 생성 시 이벤트 발행 확인")
    void createUser_ShouldRegisterUserCreatedEvent() {
        // when
        User user = User.createUser("test@example.com", "password", "Test User", freePlan);

        // then
        assertThat(user.getDomainEvents()).hasSize(1);
        assertThat(user.getDomainEvents().get(0)).isInstanceOf(UserCreatedEvent.class);

        UserCreatedEvent event = (UserCreatedEvent) user.getDomainEvents().get(0);
        assertThat(event.getEmail()).isEqualTo("test@example.com");
        assertThat(event.getName()).isEqualTo("Test User");
        assertThat(event.getPlanType()).isEqualTo(PlanType.FREE);
    }

    @Test
    @DisplayName("플랜 변경 시 이벤트 발행 확인")
    void changePlan_ShouldRegisterPlanChangedEvent() {
        // given
        User user = User.createUser("test@example.com", "password", "Test User", freePlan);
        user.clearEvents(); // 생성 이벤트 제거

        // when
        user.changePlan(standardPlan);

        // then
        assertThat(user.getDomainEvents()).hasSize(1);
        assertThat(user.getDomainEvents().get(0)).isInstanceOf(UserPlanChangedEvent.class);

        UserPlanChangedEvent event = (UserPlanChangedEvent) user.getDomainEvents().get(0);
        assertThat(event.getEmail()).isEqualTo("test@example.com");
        assertThat(event.getOldPlanType()).isEqualTo(PlanType.FREE);
        assertThat(event.getNewPlanType()).isEqualTo(PlanType.STANDARD);
    }
} 