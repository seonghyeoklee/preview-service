package com.evawova.preview.domain.user.service;

import com.evawova.preview.domain.user.entity.Subscription;
import com.evawova.preview.domain.user.entity.SubscriptionStatus;
import com.evawova.preview.domain.user.entity.User;
import com.evawova.preview.domain.user.entity.UserUsage;
import com.evawova.preview.domain.user.repository.SubscriptionRepository;
import com.evawova.preview.domain.user.repository.UserUsageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UsageService {
    private final UserUsageRepository userUsageRepository;
    private final SubscriptionRepository subscriptionRepository;

    /**
     * 사용자의 토큰 사용 가능 여부를 확인합니다.
     */
    public boolean checkTokenUsage(User user, int requiredTokens) {
        // 현재 활성 구독 확인
        Subscription subscription = subscriptionRepository.findByUserIdAndStatusAndEndDateAfter(
                user.getId(), 
                SubscriptionStatus.ACTIVE, 
                LocalDateTime.now()
            )
            .orElseThrow(() -> new IllegalStateException("활성 구독이 없습니다."));

        // 현재 달의 사용량 확인
        int currentUsage = getCurrentMonthUsage(user.getId());

        // 플랜의 한도 확인
        int limit = subscription.getPlan().getMonthlyTokenLimit();

        return (currentUsage + requiredTokens) <= limit;
    }

    /**
     * 토큰 사용량을 기록합니다.
     */
    public void recordUsage(User user, int tokens, String usageType, String description) {
        UserUsage usage = UserUsage.builder()
                .user(user)
                .tokenUsage(tokens)
                .usageType(usageType)
                .description(description)
                .build();

        userUsageRepository.save(usage);
    }

    /**
     * 현재 달의 토큰 사용량을 조회합니다.
     */
    public int getCurrentMonthUsage(Long userId) {
        LocalDateTime startOfMonth = LocalDate.now().atStartOfDay();
        LocalDateTime endOfMonth = LocalDate.now().plusMonths(1).atStartOfDay();

        return userUsageRepository.findByUserIdAndUsageDateBetween(userId, startOfMonth, endOfMonth)
                .stream()
                .mapToInt(UserUsage::getTokenUsage)
                .sum();
    }

    /**
     * 사용자의 사용량 히스토리를 조회합니다.
     */
    public List<UserUsage> getUserUsageHistory(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        return userUsageRepository.findByUserIdAndUsageDateBetween(userId, startDate, endDate);
    }

    /**
     * 사용자의 남은 토큰 수를 조회합니다.
     */
    public int getRemainingTokens(Long userId) {
        Subscription subscription = subscriptionRepository.findByUserIdAndStatusAndEndDateAfter(
                userId, 
                SubscriptionStatus.ACTIVE, 
                LocalDateTime.now()
            )
            .orElseThrow(() -> new IllegalStateException("활성 구독이 없습니다."));

        int currentUsage = getCurrentMonthUsage(userId);
        int limit = subscription.getPlan().getMonthlyTokenLimit();

        return Math.max(0, limit - currentUsage);
    }
} 