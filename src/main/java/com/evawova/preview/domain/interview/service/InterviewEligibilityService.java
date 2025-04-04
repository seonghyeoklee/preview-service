package com.evawova.preview.domain.interview.service;

import com.evawova.preview.domain.user.entity.PlanType;
import com.evawova.preview.domain.user.entity.Subscription;
import com.evawova.preview.domain.user.entity.SubscriptionStatus;
import com.evawova.preview.domain.user.entity.User;
import com.evawova.preview.domain.user.repository.SubscriptionRepository;
import com.evawova.preview.domain.user.repository.UserRepository;
import com.evawova.preview.domain.user.service.UsageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 면접 진행 자격 검증 서비스
 * - 사용자의 구독 상태와 사용량을 확인하여 면접 진행 가능 여부를 검증
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InterviewEligibilityService {

    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final UsageService usageService;

    // 면접 1회 진행 시 사용되는 토큰 수 (예상)
    private static final int INTERVIEW_TOKEN_USAGE = 5000;

    /**
     * 사용자 ID로 면접 진행 가능 여부 확인
     * 
     * @param userId 사용자 ID
     * @return 면접 진행 가능 여부 (가능하면 true)
     */
    public InterviewEligibilityResult checkEligibility(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));

        return checkEligibility(user);
    }

    /**
     * 사용자 객체로 면접 진행 가능 여부 확인
     * 
     * @param user 사용자 객체
     * @return 면접 진행 가능 여부 결과 객체
     */
    public InterviewEligibilityResult checkEligibility(User user) {
        if (user == null) {
            return InterviewEligibilityResult.notEligible("사용자 정보가 없습니다.");
        }

        // 1. 구독 상태 검증
        InterviewEligibilityResult subscriptionResult = checkSubscriptionStatus(user);
        if (!subscriptionResult.isEligible()) {
            return subscriptionResult;
        }

        // 2. 토큰 사용량 검증
        try {
            boolean hasEnoughTokens = usageService.checkTokenUsage(user, INTERVIEW_TOKEN_USAGE);
            if (!hasEnoughTokens) {
                return InterviewEligibilityResult.notEligible("이번 달 토큰 사용량을 초과했습니다. 플랜을 업그레이드하거나 다음 달까지 기다려주세요.");
            }
        } catch (IllegalStateException e) {
            log.warn("사용자 {} 토큰 사용량 확인 중 오류: {}", user.getId(), e.getMessage());
            return InterviewEligibilityResult.notEligible("구독 정보를 확인할 수 없습니다: " + e.getMessage());
        }

        return InterviewEligibilityResult.eligible();
    }

    /**
     * 사용자의 구독 상태 검증
     * 
     * @param user 사용자 객체
     * @return 구독 상태 검증 결과
     */
    private InterviewEligibilityResult checkSubscriptionStatus(User user) {
        // 관리자는 항상 가능
        if (User.Role.ADMIN.equals(user.getRole())) {
            return InterviewEligibilityResult.eligible();
        }

        // 사용자 계정이 비활성화된 경우
        if (!user.isActive()) {
            return InterviewEligibilityResult.notEligible("계정이 비활성화되었습니다.");
        }

        // Subscription 테이블에서 활성 구독 정보 확인
        Optional<Subscription> activeSubscriptionOpt = subscriptionRepository.findByUserIdAndStatusAndEndDateAfter(
                user.getId(), SubscriptionStatus.ACTIVE, LocalDateTime.now());

        // 활성 구독이 없는 경우, User의 Plan 정보로 검증
        if (activeSubscriptionOpt.isEmpty()) {
            // 무료 플랜인 경우 제한적으로 허용
            if (user.getPlan() != null && PlanType.FREE.equals(user.getPlan().getType())) {
                return InterviewEligibilityResult.eligible("무료 플랜으로 면접이 제한적으로 제공됩니다.");
            } else if (user.getPlan() != null && user.getPlan().isActive()) {
                return InterviewEligibilityResult.eligible();
            } else {
                return InterviewEligibilityResult.notEligible("활성화된 구독이 없습니다. 구독을 신청해주세요.");
            }
        }

        // 활성 구독이 있으면 검증 통과
        return InterviewEligibilityResult.eligible();
    }

    /**
     * 면접 진행 자격 검증 결과 클래스
     */
    public static class InterviewEligibilityResult {
        private final boolean eligible;
        private final String message;
        private final LocalDateTime timestamp;

        private InterviewEligibilityResult(boolean eligible, String message) {
            this.eligible = eligible;
            this.message = message;
            this.timestamp = LocalDateTime.now();
        }

        public static InterviewEligibilityResult eligible() {
            return new InterviewEligibilityResult(true, "면접 진행이 가능합니다.");
        }

        public static InterviewEligibilityResult eligible(String message) {
            return new InterviewEligibilityResult(true, message);
        }

        public static InterviewEligibilityResult notEligible(String reason) {
            return new InterviewEligibilityResult(false, reason);
        }

        public boolean isEligible() {
            return eligible;
        }

        public String getMessage() {
            return message;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }
    }
}