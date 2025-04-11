package com.evawova.preview.domain.user.service;

import com.evawova.preview.domain.user.dto.SubscriptionDto;
import com.evawova.preview.domain.user.entity.*;
import com.evawova.preview.domain.user.repository.SubscriptionRepository;
import com.evawova.preview.domain.user.repository.UserRepository;
import com.evawova.preview.domain.user.repository.PlanRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Validated
@Slf4j
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final PlanRepository planRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 특정 사용자의 모든 구독 기록을 조회합니다.
     */
    public List<SubscriptionDto> getUserSubscriptions(@NotNull Long userId) {
        log.info("사용자 모든 구독 조회 시작: 사용자 ID: {}", userId);
        if (!userRepository.existsById(userId)) {
            log.error("구독 조회 실패: 사용자를 찾을 수 없음 - 사용자 ID: {}", userId);
            throw new EntityNotFoundException("사용자를 찾을 수 없습니다: " + userId);
        }
        List<Subscription> subscriptions = subscriptionRepository.findByUserId(userId);
        log.info("총 {}개의 구독 조회 완료: 사용자 ID: {}", subscriptions.size(), userId);
        return subscriptions.stream()
                .map(SubscriptionDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 특정 사용자의 현재 활성 구독을 조회합니다.
     */
    public SubscriptionDto getActiveSubscription(@NotNull Long userId) {
        log.info("활성 구독 조회 시작: 사용자 ID: {}", userId);
        if (!userRepository.existsById(userId)) {
            log.error("활성 구독 조회 실패: 사용자를 찾을 수 없음 - 사용자 ID: {}", userId);
            throw new EntityNotFoundException("사용자를 찾을 수 없습니다: " + userId);
        }

        SubscriptionDto activeSubscriptionDto = subscriptionRepository.findActiveByUserId(userId)
                .map(SubscriptionDto::fromEntity)
                .orElse(null);

        if (activeSubscriptionDto != null) {
            log.info("활성 구독 조회 성공: 사용자 ID: {}, 구독 ID: {}", userId, activeSubscriptionDto.getId());
        } else {
            log.info("활성 구독 없음: 사용자 ID: {}", userId);
        }
        return activeSubscriptionDto;
    }

    /**
     * 새로운 구독을 생성합니다. 기존 활성 구독이 있다면 자동으로 취소됩니다.
     */
    @Transactional
    public SubscriptionDto createSubscription(
            @NotNull Long userId,
            @NotNull Long planId,
            @NotNull Subscription.SubscriptionCycle cycle) {
        log.info("신규 구독 생성 시작: 사용자 ID: {}, 플랜 ID: {}, 주기: {}", userId, planId, cycle);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("구독 생성 실패: 사용자를 찾을 수 없음 - 사용자 ID: {}", userId);
                    return new EntityNotFoundException("사용자를 찾을 수 없습니다: " + userId);
                });

        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> {
                    log.error("구독 생성 실패: 플랜을 찾을 수 없음 - 플랜 ID: {}", planId);
                    return new EntityNotFoundException("플랜을 찾을 수 없습니다: " + planId);
                });

        if (!plan.isActive()) {
            log.error("구독 생성 실패: 비활성화된 플랜 - 플랜 ID: {}", planId);
            throw new IllegalArgumentException("비활성화된 플랜으로는 구독을 생성할 수 없습니다: " + planId);
        }

        subscriptionRepository.findActiveByUserId(userId)
                .ifPresent(existingSubscription -> {
                    log.info("기존 활성 구독 발견 (구독 ID: {}). 새 구독 생성을 위해 취소 처리합니다.", existingSubscription.getId());
                    existingSubscription.cancel();
                    log.info("기존 구독 취소 완료: 구독 ID: {}", existingSubscription.getId());
                });

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endDate = cycle == Subscription.SubscriptionCycle.MONTHLY
                ? now.plusMonths(1)
                : now.plusYears(1);
        BigDecimal paymentAmount = plan.getPriceForCycle(cycle);
        log.debug("구독 만료일 및 결제 금액 계산 완료: 만료일: {}, 금액: {}", endDate, paymentAmount);

        Subscription newSubscription = Subscription.createSubscription(
                user,
                plan,
                now,
                endDate,
                paymentAmount,
                cycle);

        Subscription savedSubscription = subscriptionRepository.save(newSubscription);
        log.info("신규 구독 생성 및 저장 성공: 구독 ID: {}. 사용자 ID: {}, 플랜 ID: {}",
                savedSubscription.getId(), userId, planId);

        eventPublisher.publishEvent(savedSubscription);
        log.info("구독 관련 이벤트 발행 완료: 구독 ID: {}", savedSubscription.getId());

        return SubscriptionDto.fromEntity(savedSubscription);
    }

    /**
     * 특정 구독을 취소합니다.
     */
    @Transactional
    public SubscriptionDto cancelSubscription(@NotNull Long subscriptionId) {
        log.info("구독 취소 시작: 구독 ID: {}", subscriptionId);

        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> {
                    log.error("구독 취소 실패: 구독을 찾을 수 없음 - 구독 ID: {}", subscriptionId);
                    return new EntityNotFoundException("구독을 찾을 수 없습니다: " + subscriptionId);
                });

        if (!subscription.isActive()) {
            log.warn("이미 비활성 상태인 구독 취소 요청: 구독 ID: {}. 현재 상태: {}",
                    subscriptionId, subscription.getStatus());
            throw new IllegalStateException("이미 취소되었거나 만료된 구독입니다: " + subscriptionId);
        }

        subscription.cancel();
        log.info("구독 취소 성공: 구독 ID: {}", subscriptionId);

        eventPublisher.publishEvent(subscription);
        log.info("구독 취소 이벤트 발행 완료: 구독 ID: {}", subscriptionId);

        return SubscriptionDto.fromEntity(subscription);
    }

    /**
     * 특정 사용자의 현재 활성 구독을 찾아 취소합니다.
     * 활성 구독이 없는 경우 아무 작업도 수행하지 않습니다.
     *
     * @param userId 취소할 사용자의 ID
     * @param reason 취소 사유 (로깅용)
     */
    @Transactional
    public void cancelActiveSubscriptionByUserId(@NotNull Long userId, String reason) {
        log.info("사용자 ID 기반 활성 구독 취소 시작: 사용자 ID: {}, 사유: {}", userId, reason);

        subscriptionRepository.findActiveByUserId(userId).ifPresent(subscription -> {
            log.info("취소 대상 활성 구독 확인: 구독 ID: {}, 사용자 ID: {}. 취소 처리 진행.",
                    subscription.getId(), userId);
            subscription.cancel();
            log.info("활성 구독 취소 성공: 구독 ID: {}, 사용자 ID: {}. 사유: {}",
                    subscription.getId(), userId, reason);

            eventPublisher.publishEvent(subscription);
            log.info("구독 취소 이벤트 발행 완료: 구독 ID: {}", subscription.getId());
        });

        if (subscriptionRepository.findActiveByUserId(userId).isEmpty()) {
            log.info("취소 요청 시 활성 구독 없음: 사용자 ID: {} (사유: {}). 처리 없음.", userId, reason);
        }
    }

    /**
     * 만료된 구독을 갱신합니다. (주의: 활성 구독은 갱신 불가)
     */
    @Transactional
    public SubscriptionDto renewSubscription(@NotNull Long subscriptionId) {
        log.info("구독 갱신 시작: 구독 ID: {}", subscriptionId);

        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> {
                    log.error("구독 갱신 실패: 구독을 찾을 수 없음 - 구독 ID: {}", subscriptionId);
                    return new EntityNotFoundException("구독을 찾을 수 없습니다: " + subscriptionId);
                });

        if (subscription.isActive()) {
            log.error("구독 갱신 실패: 이미 활성 상태인 구독 - 구독 ID: {}", subscriptionId);
            throw new IllegalStateException("이미 활성화된 구독은 갱신할 수 없습니다: " + subscriptionId);
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime newEndDate = subscription.getCycle() == Subscription.SubscriptionCycle.MONTHLY
                ? now.plusMonths(1)
                : now.plusYears(1);
        log.debug("구독 갱신 처리 중: 구독 ID: {}. 새 만료일: {}", subscriptionId, newEndDate);

        subscription.renew(newEndDate);
        log.info("구독 갱신 성공: 구독 ID: {}", subscriptionId);

        eventPublisher.publishEvent(subscription);
        log.info("구독 갱신 이벤트 발행 완료: 구독 ID: {}", subscriptionId);

        return SubscriptionDto.fromEntity(subscription);
    }
}