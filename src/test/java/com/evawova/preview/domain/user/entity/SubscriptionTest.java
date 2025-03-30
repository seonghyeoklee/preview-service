package com.evawova.preview.domain.user.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class SubscriptionTest {

    @Test
    @DisplayName("Subscription 엔티티를 생성할 수 있다")
    void createSubscription() {
        // given
        User user = User.builder()
                .email("test@example.com")
                .password("password")
                .displayName("Test User")
                .build();

        Plan plan = Plan.builder()
                .name("Standard")
                .type(PlanType.STANDARD)
                .monthlyPrice(9900)
                .annualPrice(99000)
                .monthlyTokenLimit(50000)
                .active(true)
                .build();

        LocalDateTime now = LocalDateTime.now();

        // when
        Subscription subscription = Subscription.builder()
                .user(user)
                .plan(plan)
                .startDate(now)
                .endDate(now.plusMonths(1))
                .status(SubscriptionStatus.ACTIVE)
                .paymentProvider("STRIPE")
                .subscriptionId("sub_123")
                .amount(9900)
                .billingCycle("MONTHLY")
                .build();

        // then
        assertThat(subscription.getUser()).isEqualTo(user);
        assertThat(subscription.getPlan()).isEqualTo(plan);
        assertThat(subscription.getStartDate()).isEqualTo(now);
        assertThat(subscription.getEndDate()).isEqualTo(now.plusMonths(1));
        assertThat(subscription.getStatus()).isEqualTo(SubscriptionStatus.ACTIVE);
        assertThat(subscription.getPaymentProvider()).isEqualTo("STRIPE");
        assertThat(subscription.getSubscriptionId()).isEqualTo("sub_123");
        assertThat(subscription.getAmount()).isEqualTo(9900);
        assertThat(subscription.getBillingCycle()).isEqualTo("MONTHLY");
        assertThat(subscription.getLastPaymentDate()).isNotNull();
        assertThat(subscription.getNextPaymentDate()).isNotNull();
    }

    @Test
    @DisplayName("구독을 취소할 수 있다")
    void cancelSubscription() {
        // given
        Subscription subscription = createTestSubscription();

        // when
        subscription.cancel();

        // then
        assertThat(subscription.getStatus()).isEqualTo(SubscriptionStatus.CANCELLED);
        assertThat(subscription.getEndDate()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    @DisplayName("구독을 갱신할 수 있다")
    void renewSubscription() {
        // given
        Subscription subscription = createTestSubscription();
        LocalDateTime beforeRenew = LocalDateTime.now();

        // when
        subscription.renew();

        // then
        assertThat(subscription.getStatus()).isEqualTo(SubscriptionStatus.ACTIVE);
        assertThat(subscription.getLastPaymentDate()).isAfterOrEqualTo(beforeRenew);
        assertThat(subscription.getNextPaymentDate()).isAfter(subscription.getLastPaymentDate());
    }

    @Test
    @DisplayName("구독 상태를 업데이트할 수 있다")
    void updateSubscriptionStatus() {
        // given
        Subscription subscription = createTestSubscription();

        // when
        subscription.updateStatus(SubscriptionStatus.PAST_DUE);

        // then
        assertThat(subscription.getStatus()).isEqualTo(SubscriptionStatus.PAST_DUE);
    }

    @Test
    @DisplayName("구독 상태를 CANCELLED로 업데이트하면 종료일이 현재 시간으로 설정된다")
    void updateSubscriptionStatusToCancelled() {
        // given
        Subscription subscription = createTestSubscription();
        LocalDateTime beforeUpdate = LocalDateTime.now();

        // when
        subscription.updateStatus(SubscriptionStatus.CANCELLED);

        // then
        assertThat(subscription.getStatus()).isEqualTo(SubscriptionStatus.CANCELLED);
        assertThat(subscription.getEndDate()).isAfterOrEqualTo(beforeUpdate);
    }

    @Test
    @DisplayName("활성 구독인지 확인할 수 있다")
    void isActive() {
        // given
        Subscription subscription = createTestSubscription();

        // when & then
        assertThat(subscription.isActive()).isTrue();

        // when
        subscription.cancel();

        // then
        assertThat(subscription.isActive()).isFalse();
    }

    private Subscription createTestSubscription() {
        User user = User.builder()
                .email("test@example.com")
                .password("password")
                .displayName("Test User")
                .build();

        Plan plan = Plan.builder()
                .name("Standard")
                .type(PlanType.STANDARD)
                .monthlyPrice(9900)
                .annualPrice(99000)
                .monthlyTokenLimit(50000)
                .active(true)
                .build();

        LocalDateTime now = LocalDateTime.now();

        return Subscription.builder()
                .user(user)
                .plan(plan)
                .startDate(now)
                .endDate(now.plusMonths(1))
                .status(SubscriptionStatus.ACTIVE)
                .paymentProvider("STRIPE")
                .subscriptionId("sub_123")
                .amount(9900)
                .billingCycle("MONTHLY")
                .build();
    }
} 