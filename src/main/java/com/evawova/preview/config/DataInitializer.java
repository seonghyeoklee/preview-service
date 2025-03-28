package com.evawova.preview.config;

import com.evawova.preview.domain.user.entity.Plan;
import com.evawova.preview.domain.user.entity.PlanType;
import com.evawova.preview.domain.user.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final PlanRepository planRepository;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            // 기존 플랜이 없는 경우에만 초기 데이터 생성
            if (!planRepository.existsByType(PlanType.FREE)) {
                // FREE 플랜
                planRepository.save(Plan.createPlan(
                        "Free",
                        PlanType.FREE,
                        0,      // 월 요금 (무료)
                        0,      // 년 요금 (무료)
                        1,      // 1GB 스토리지
                        3,      // 최대 3개 프로젝트
                        false,  // 팀 협업 불가
                        false,  // 우선 지원 불가
                        false   // 커스텀 도메인 불가
                ));
            }

            if (!planRepository.existsByType(PlanType.PREMIUM)) {
                // STANDARD 플랜
                planRepository.save(Plan.createPlan(
                        "Premium",
                        PlanType.PREMIUM,
                        9900,   // 월 요금 9,900원
                        99000,  // 년 요금 99,000원
                        10,     // 10GB 스토리지
                        10,     // 최대 10개 프로젝트
                        true,   // 팀 협업 가능
                        false,  // 우선 지원 불가
                        false   // 커스텀 도메인 불가
                ));
            }

            if (!planRepository.existsByType(PlanType.ENTERPRISE)) {
                // PRO 플랜
                planRepository.save(Plan.createPlan(
                        "Enterprise",
                        PlanType.ENTERPRISE,
                        19900,  // 월 요금 19,900원
                        199000, // 년 요금 199,000원
                        100,    // 100GB 스토리지
                        100,    // 최대 100개 프로젝트
                        true,   // 팀 협업 가능
                        true,   // 우선 지원 가능
                        true    // 커스텀 도메인 가능
                ));
            }
        };
    }
} 