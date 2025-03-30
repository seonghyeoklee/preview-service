package com.evawova.preview.domain.user.service;

import com.evawova.preview.domain.user.entity.Plan;
import com.evawova.preview.domain.user.entity.PlanType;
import com.evawova.preview.domain.user.entity.Subscription;
import com.evawova.preview.domain.user.entity.SubscriptionStatus;
import com.evawova.preview.domain.user.entity.User;
import com.evawova.preview.domain.user.repository.SubscriptionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @InjectMocks
    private SubscriptionService subscriptionService;

    @Test
    @DisplayName("새로운 구독을 생성할 수 있다")
    void createSubscription() {
        // given
        User user = createTestUser();
        Plan plan = createTestPlan();
        Subscription subscription = createTestSubscription(user, plan);

        given(subscriptionRepository.save(any(Subscription.class)))
                .willReturn(subscription);

        // when
        Subscription created = subscriptionService.createSubscription(
                user, plan, "STRIPE", "sub_123", 9900, "MONTHLY");

        // then
        assertThat(created.getUser()).isEqualTo(user);
        assertThat(created.getPlan()).isEqualTo(plan);
        assertThat(created.getPaymentProvider()).isEqualTo("STRIPE");
        assertThat(created.getSubscriptionId()).isEqualTo("sub_123");
        assertThat(created.getAmount()).isEqualTo(9900);
        assertThat(created.getBillingCycle()).isEqualTo("MONTHLY");
        assertThat(created.getStatus()).isEqualTo(SubscriptionStatus.ACTIVE);
        assertThat(created.getStartDate()).isNotNull();
        assertThat(created.getEndDate()).isNotNull();
        assertThat(created.getLastPaymentDate()).isNotNull();
        assertThat(created.getNextPaymentDate()).isNotNull();
    }

    @Test
    @DisplayName("구독을 취소할 수 있다")
    void cancelSubscription() {
        // given
        Subscription subscription = createTestSubscription(createTestUser(), createTestPlan());
        given(subscriptionRepository.findById(1L))
                .willReturn(Optional.of(subscription));

        // when
        subscriptionService.cancelSubscription(1L);

        // then
        assertThat(subscription.getStatus()).isEqualTo(SubscriptionStatus.CANCELLED);
        assertThat(subscription.getEndDate()).isBeforeOrEqualTo(LocalDateTime.now());
        verify(subscriptionRepository).save(subscription);
    }

    @Test
    @DisplayName("존재하지 않는 구독을 취소하면 예외가 발생한다")
    void cancelNonExistentSubscription() {
        // given
        given(subscriptionRepository.findById(1L))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> subscriptionService.cancelSubscription(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("구독을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("구독을 갱신할 수 있다")
    void renewSubscription() {
        // given
        Subscription subscription = createTestSubscription(createTestUser(), createTestPlan());
        given(subscriptionRepository.findById(1L))
                .willReturn(Optional.of(subscription));

        // when
        subscriptionService.renewSubscription(1L);

        // then
        assertThat(subscription.getStatus()).isEqualTo(SubscriptionStatus.ACTIVE);
        assertThat(subscription.getLastPaymentDate()).isNotNull();
        assertThat(subscription.getNextPaymentDate()).isAfter(subscription.getLastPaymentDate());
        verify(subscriptionRepository).save(subscription);
    }

    @Test
    @DisplayName("활성 구독을 조회할 수 있다")
    void getActiveSubscription() {
        // given
        User user = createTestUser();
        Subscription subscription = createTestSubscription(user, createTestPlan());
        given(subscriptionRepository.findByUserIdAndStatusAndEndDateAfter(
                user.getId(), 
                SubscriptionStatus.ACTIVE, 
                any(LocalDateTime.class)))
                .willReturn(Optional.of(subscription));

        // when
        Subscription active = subscriptionService.getActiveSubscription(user.getId());

        // then
        assertThat(active).isEqualTo(subscription);
    }

    @Test
    @DisplayName("활성 구독이 없으면 예외가 발생한다")
    void getActiveSubscriptionNotFound() {
        // given
        given(subscriptionRepository.findByUserIdAndStatusAndEndDateAfter(
                1L, 
                SubscriptionStatus.ACTIVE, 
                any(LocalDateTime.class)))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> subscriptionService.getActiveSubscription(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("활성 구독이 없습니다.");
    }

    @Test
    @DisplayName("구독이 만료되었는지 확인할 수 있다")
    void isSubscriptionExpired() {
        // given
        Subscription subscription = createTestSubscription(createTestUser(), createTestPlan());
        given(subscriptionRepository.findById(1L))
                .willReturn(Optional.of(subscription));

        // when
        boolean isExpired = subscriptionService.isSubscriptionExpired(1L);

        // then
        assertThat(isExpired).isFalse();
    }

    @Test
    @DisplayName("구독 상태를 업데이트할 수 있다")
    void updateSubscriptionStatus() {
        // given
        Subscription subscription = createTestSubscription(createTestUser(), createTestPlan());
        given(subscriptionRepository.findById(1L))
                .willReturn(Optional.of(subscription));

        // when
        subscriptionService.updateSubscriptionStatus(1L, SubscriptionStatus.PAST_DUE);

        // then
        assertThat(subscription.getStatus()).isEqualTo(SubscriptionStatus.PAST_DUE);
        verify(subscriptionRepository).save(subscription);
    }

    private User createTestUser() {
        return User.builder()
                .id(1L)
                .email("test@example.com")
                .password("password")
                .displayName("Test User")
                .build();
    }

    private Plan createTestPlan() {
        return Plan.builder()
                .id(1L)
                .name("Standard")
                .type(PlanType.STANDARD)
                .monthlyPrice(9900)
                .annualPrice(99000)
                .monthlyTokenLimit(50000)
                .active(true)
                .build();
    }

    private Subscription createTestSubscription(User user, Plan plan) {
        return Subscription.builder()
                .id(1L)
                .user(user)
                .plan(plan)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusMonths(1))
                .status(SubscriptionStatus.ACTIVE)
                .paymentProvider("STRIPE")
                .subscriptionId("sub_123")
                .amount(9900)
                .billingCycle("MONTHLY")
                .build();
    }
} 