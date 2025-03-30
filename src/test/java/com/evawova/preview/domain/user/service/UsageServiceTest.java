package com.evawova.preview.domain.user.service;

import com.evawova.preview.domain.user.entity.Plan;
import com.evawova.preview.domain.user.entity.PlanType;
import com.evawova.preview.domain.user.entity.Subscription;
import com.evawova.preview.domain.user.entity.SubscriptionStatus;
import com.evawova.preview.domain.user.entity.User;
import com.evawova.preview.domain.user.entity.UserUsage;
import com.evawova.preview.domain.user.repository.SubscriptionRepository;
import com.evawova.preview.domain.user.repository.UserUsageRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class UsageServiceTest {

    @Mock
    private UserUsageRepository userUsageRepository;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @InjectMocks
    private UsageService usageService;

    @Test
    @DisplayName("토큰 사용 가능 여부를 확인할 수 있다")
    void checkTokenUsage() {
        // given
        User user = createTestUser();
        Plan plan = createTestPlan();
        Subscription subscription = createTestSubscription(user, plan);
        
        given(subscriptionRepository.findByUserIdAndStatusAndEndDateAfter(
                user.getId(), 
                SubscriptionStatus.ACTIVE, 
                any(LocalDateTime.class)))
                .willReturn(java.util.Optional.of(subscription));
        given(userUsageRepository.findByUserIdAndUsageDateBetween(any(), any(), any()))
                .willReturn(Arrays.asList(
                    createTestUsage(user, 1000),
                    createTestUsage(user, 2000)
                ));

        // when
        boolean canUse = usageService.checkTokenUsage(user, 1000);

        // then
        assertThat(canUse).isTrue();
    }

    @Test
    @DisplayName("토큰 사용량이 한도를 초과하면 false를 반환한다")
    void checkTokenUsageExceedLimit() {
        // given
        User user = createTestUser();
        Plan plan = createTestPlan();
        Subscription subscription = createTestSubscription(user, plan);
        
        given(subscriptionRepository.findByUserIdAndStatusAndEndDateAfter(
                user.getId(), 
                SubscriptionStatus.ACTIVE, 
                any(LocalDateTime.class)))
                .willReturn(java.util.Optional.of(subscription));
        given(userUsageRepository.findByUserIdAndUsageDateBetween(any(), any(), any()))
                .willReturn(Arrays.asList(
                    createTestUsage(user, 45000),
                    createTestUsage(user, 6000)
                ));

        // when
        boolean canUse = usageService.checkTokenUsage(user, 1000);

        // then
        assertThat(canUse).isFalse();
    }

    @Test
    @DisplayName("활성 구독이 없으면 예외가 발생한다")
    void checkTokenUsageNoActiveSubscription() {
        // given
        User user = createTestUser();
        given(subscriptionRepository.findByUserIdAndStatusAndEndDateAfter(
                user.getId(), 
                SubscriptionStatus.ACTIVE, 
                any(LocalDateTime.class)))
                .willReturn(java.util.Optional.empty());

        // when & then
        assertThatThrownBy(() -> usageService.checkTokenUsage(user, 1000))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("활성 구독이 없습니다.");
    }

    @Test
    @DisplayName("토큰 사용량을 기록할 수 있다")
    void recordUsage() {
        // given
        User user = createTestUser();
        UserUsage usage = createTestUsage(user, 1000);

        // when
        usageService.recordUsage(user, 1000, "CHAT", "테스트 채팅");

        // then
        verify(userUsageRepository).save(any(UserUsage.class));
    }

    @Test
    @DisplayName("현재 달의 토큰 사용량을 조회할 수 있다")
    void getCurrentMonthUsage() {
        // given
        User user = createTestUser();
        List<UserUsage> usages = Arrays.asList(
            createTestUsage(user, 1000),
            createTestUsage(user, 2000)
        );
        
        given(userUsageRepository.findByUserIdAndUsageDateBetween(any(), any(), any()))
                .willReturn(usages);

        // when
        int totalUsage = usageService.getCurrentMonthUsage(user.getId());

        // then
        assertThat(totalUsage).isEqualTo(3000);
    }

    @Test
    @DisplayName("사용량 히스토리를 조회할 수 있다")
    void getUserUsageHistory() {
        // given
        User user = createTestUser();
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        List<UserUsage> expectedUsages = Arrays.asList(
            createTestUsage(user, 1000),
            createTestUsage(user, 2000)
        );
        
        given(userUsageRepository.findByUserIdAndUsageDateBetween(user.getId(), startDate, endDate))
                .willReturn(expectedUsages);

        // when
        List<UserUsage> usages = usageService.getUserUsageHistory(user.getId(), startDate, endDate);

        // then
        assertThat(usages).isEqualTo(expectedUsages);
    }

    @Test
    @DisplayName("남은 토큰 수를 조회할 수 있다")
    void getRemainingTokens() {
        // given
        User user = createTestUser();
        Plan plan = createTestPlan();
        Subscription subscription = createTestSubscription(user, plan);
        
        given(subscriptionRepository.findByUserIdAndStatusAndEndDateAfter(
                user.getId(), 
                SubscriptionStatus.ACTIVE, 
                any(LocalDateTime.class)))
                .willReturn(java.util.Optional.of(subscription));
        given(userUsageRepository.findByUserIdAndUsageDateBetween(any(), any(), any()))
                .willReturn(Arrays.asList(
                    createTestUsage(user, 1000),
                    createTestUsage(user, 2000)
                ));

        // when
        int remainingTokens = usageService.getRemainingTokens(user.getId());

        // then
        assertThat(remainingTokens).isEqualTo(47000); // 50000 - 3000
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

    private UserUsage createTestUsage(User user, int tokenUsage) {
        return UserUsage.builder()
                .id(1L)
                .user(user)
                .tokenUsage(tokenUsage)
                .usageType("CHAT")
                .description("테스트 채팅")
                .build();
    }
} 