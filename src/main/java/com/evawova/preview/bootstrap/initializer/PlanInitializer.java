package com.evawova.preview.bootstrap.initializer;

import com.evawova.preview.bootstrap.EntityInitializer;
import com.evawova.preview.domain.user.entity.Plan;
import com.evawova.preview.domain.user.entity.PlanType;
import com.evawova.preview.domain.user.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Plan 엔티티 초기화 클래스
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PlanInitializer implements EntityInitializer {

    private final PlanRepository planRepository;

    @Override
    @Transactional
    public void initialize() {
        if (planRepository.count() > 0) {
            log.info("이미 Plan 데이터가 존재합니다. 초기화를 건너뜁니다.");
            return;
        }

        log.info("Plan 데이터 초기화를 시작합니다.");

        // 무료 플랜
        Plan freePlan = Plan.createPlan(
                "Free Plan",
                PlanType.FREE,
                0,
                0,
                10000, // 월 1만 토큰
                true);

        // 스탠다드 플랜
        Plan standardPlan = Plan.createPlan(
                "Standard Plan",
                PlanType.STANDARD,
                9900,
                99000,
                50000, // 월 5만 토큰
                true);

        // 프로 플랜
        Plan proPlan = Plan.createPlan(
                "Pro Plan",
                PlanType.PRO,
                1900,
                19000,
                100000, // 월 10만 토큰
                true);

        planRepository.saveAll(java.util.List.of(freePlan, standardPlan, proPlan));
        log.info("Plan 데이터 초기화가 완료되었습니다. 총 {}개의 플랜이 생성되었습니다.", 3);
    }

    @Override
    public String getEntityName() {
        return "Plan";
    }

    @Override
    public int getOrder() {
        return 10; // 높은 우선순위로 초기화
    }
}