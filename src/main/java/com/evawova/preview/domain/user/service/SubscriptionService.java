package com.evawova.preview.domain.user.service;

import com.evawova.preview.domain.user.entity.Plan;
import com.evawova.preview.domain.user.entity.Subscription;
import com.evawova.preview.domain.user.entity.SubscriptionStatus;
import com.evawova.preview.domain.user.entity.User;
import com.evawova.preview.domain.user.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;

    /**
     * 새로운 구독을 생성합니다.
     */
    public Subscription createSubscription(User user, Plan plan, String paymentProvider, 
                                         String subscriptionId, Integer amount, String billingCycle) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endDate = billingCycle.equals("MONTHLY") ? 
            now.plusMonths(1) : now.plusYears(1);

        Subscription subscription = Subscription.builder()
                .user(user)
                .plan(plan)
                .startDate(now)
                .endDate(endDate)
                .paymentProvider(paymentProvider)
                .subscriptionId(subscriptionId)
                .amount(amount)
                .billingCycle(billingCycle)
                .build();

        return subscriptionRepository.save(subscription);
    }

    /**
     * 구독을 취소합니다.
     */
    public void cancelSubscription(Long subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new IllegalArgumentException("구독을 찾을 수 없습니다."));
        
        subscription.cancel();
        subscriptionRepository.save(subscription);
    }

    /**
     * 구독을 갱신합니다.
     */
    public void renewSubscription(Long subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new IllegalArgumentException("구독을 찾을 수 없습니다."));
        
        subscription.renew();
        subscriptionRepository.save(subscription);
    }

    /**
     * 사용자의 활성 구독을 조회합니다.
     */
    public Subscription getActiveSubscription(Long userId) {
        return subscriptionRepository.findByUserIdAndStatusAndEndDateAfter(
                userId, 
                SubscriptionStatus.ACTIVE, 
                LocalDateTime.now()
            )
            .orElseThrow(() -> new IllegalStateException("활성 구독이 없습니다."));
    }

    /**
     * 구독이 만료되었는지 확인합니다.
     */
    public boolean isSubscriptionExpired(Long subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new IllegalArgumentException("구독을 찾을 수 없습니다."));
        
        return !subscription.isActive();
    }

    /**
     * 구독 상태를 업데이트합니다.
     */
    public void updateSubscriptionStatus(Long subscriptionId, SubscriptionStatus status) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new IllegalArgumentException("구독을 찾을 수 없습니다."));
        
        subscription.updateStatus(status);
        subscriptionRepository.save(subscription);
    }
} 