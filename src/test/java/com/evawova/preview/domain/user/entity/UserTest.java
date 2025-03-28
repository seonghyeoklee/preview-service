package com.evawova.preview.domain.user.entity;

import com.evawova.preview.domain.user.event.UserCreatedEvent;
import com.evawova.preview.domain.user.event.UserPlanChangedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

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
                "Premium",
                PlanType.PREMIUM,
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
        assertThat(event.getNewPlanType()).isEqualTo(PlanType.PREMIUM);
    }

    @Test
    @DisplayName("소셜 로그인으로 새로운 사용자를 생성할 수 있다")
    void createSocialUser() {
        // given
        String uid = "123456789";
        String email = "test@example.com";
        String name = "Test User";
        User.Provider provider = User.Provider.GOOGLE;

        // when
        User user = User.createSocialUser(uid, email, name, provider, freePlan);

        // then
        assertThat(user.getUid()).isEqualTo(uid);
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getDisplayName()).isEqualTo(name);
        assertThat(user.getProvider()).isEqualTo(provider);
        assertThat(user.getPlan()).isEqualTo(freePlan);
        assertThat(user.isActive()).isTrue();
        assertThat(user.getPassword()).startsWith("SOCIAL_USER_");
    }

    @Test
    @DisplayName("소셜 로그인 정보를 업데이트할 수 있다")
    void updateSocialInfo() {
        // given
        User user = User.createSocialUser(
            "123456789",
            "test@example.com",
            "Test User",
            User.Provider.GOOGLE,
            freePlan
        );

        String newName = "Updated Name";
        User.Provider newProvider = User.Provider.APPLE;

        // when
        user.updateSocialInfo(newName, newProvider);

        // then
        assertThat(user.getDisplayName()).isEqualTo(newName);
        assertThat(user.getProvider()).isEqualTo(newProvider);
    }

    @Test
    @DisplayName("추가 정보를 업데이트할 수 있다")
    void updateAdditionalInfo() {
        // given
        User user = User.createSocialUser(
            "123456789",
            "test@example.com",
            "Test User",
            User.Provider.GOOGLE,
            freePlan
        );

        String newDisplayName = "New Display Name";
        String photoUrl = "https://example.com/photo.jpg";
        boolean isEmailVerified = true;
        LocalDateTime lastLoginAt = LocalDateTime.now();

        // when
        user.updateAdditionalInfo(newDisplayName, photoUrl, isEmailVerified, lastLoginAt);

        // then
        assertThat(user.getDisplayName()).isEqualTo(newDisplayName);
        assertThat(user.getPhotoUrl()).isEqualTo(photoUrl);
        assertThat(user.isEmailVerified()).isTrue();
        assertThat(user.getLastLoginAt()).isEqualTo(lastLoginAt);
    }

    @Test
    @DisplayName("계정을 탈퇴할 수 있다")
    void withdraw() {
        // given
        User user = User.createSocialUser(
            "123456789",
            "test@example.com",
            "Test User",
            User.Provider.GOOGLE,
            freePlan
        );

        // when
        user.withdraw();

        // then
        assertThat(user.isActive()).isFalse();
        assertThat(user.getEmail()).startsWith("withdrawn_");
        assertThat(user.getDisplayName()).isEqualTo("탈퇴한 사용자");
        assertThat(user.getPassword()).isNull();
        assertThat(user.getProvider()).isNull();
        assertThat(user.getPhotoUrl()).isNull();
        assertThat(user.isEmailVerified()).isFalse();
        assertThat(user.getLastLoginAt()).isNull();
    }

    @Test
    @DisplayName("플랜을 변경할 수 있다")
    void changePlan() {
        // given
        User user = User.createSocialUser(
            "123456789",
            "test@example.com",
            "Test User",
            User.Provider.GOOGLE,
            freePlan
        );

        Plan premiumPlan = Plan.createPlan(
            "Premium Plan",
            PlanType.PREMIUM,
            10000,
            100000,
            100,
            100,
            true,
            true,
            true
        );

        // when
        user.changePlan(premiumPlan);

        // then
        assertThat(user.getPlan()).isEqualTo(premiumPlan);
    }
} 