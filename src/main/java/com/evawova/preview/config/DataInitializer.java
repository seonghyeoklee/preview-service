package com.evawova.preview.config;

import com.evawova.preview.domain.user.entity.Plan;
import com.evawova.preview.domain.user.entity.PlanType;
import com.evawova.preview.domain.user.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final PlanRepository planRepository;

    @Bean
    @Transactional
    public CommandLineRunner initData() {
        return args -> {
            // 플랜 데이터 초기화
            if (planRepository.count() == 0) {
                // Free 플랜
                planRepository.save(Plan.createPlan(
                    "Free",
                    PlanType.FREE,
                    0,
                    0,
                    10000,  // 10,000 토큰
                    true
                ));

                // Standard 플랜
                planRepository.save(Plan.createPlan(
                    "Standard",
                    PlanType.STANDARD,
                    9900,
                    99000,
                    50000,  // 50,000 토큰
                    true
                ));

                // Pro 플랜
                planRepository.save(Plan.createPlan(
                    "Pro",
                    PlanType.PRO,
                    19900,
                    199000,
                    100000, // 100,000 토큰
                    true
                ));
            }
        };
    }
} 