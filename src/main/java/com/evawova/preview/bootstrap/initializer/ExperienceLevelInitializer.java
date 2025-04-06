package com.evawova.preview.bootstrap.initializer;

import com.evawova.preview.bootstrap.EntityInitializer;
import com.evawova.preview.domain.interview.entity.ExperienceLevel;
import com.evawova.preview.domain.interview.repository.ExperienceLevelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ExperienceLevel 엔티티 초기화 클래스
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExperienceLevelInitializer implements EntityInitializer {

    private final ExperienceLevelRepository experienceLevelRepository;

    @Override
    @Transactional
    public void initialize() {
        if (experienceLevelRepository.count() > 0) {
            log.info("이미 ExperienceLevel 데이터가 존재합니다. 초기화를 건너뜁니다.");
            return;
        }

        log.info("ExperienceLevel 데이터 초기화를 시작합니다.");
        LocalDateTime now = LocalDateTime.now();
        List<ExperienceLevel> levels = new ArrayList<>();

        levels.add(ExperienceLevel.builder()
                .code(ExperienceLevel.ENTRY)
                .displayName("신입")
                .displayNameEn("Entry Level")
                .description("경력이 없거나 1년 미만인 직급")
                .descriptionEn("Position with no experience or less than 1 year")
                .minYears(0)
                .maxYears(0)
                .active(true)
                .sortOrder(1)
                .createdAt(now)
                .updatedAt(now)
                .build());

        levels.add(ExperienceLevel.builder()
                .code(ExperienceLevel.JUNIOR)
                .displayName("주니어 (1-3년)")
                .displayNameEn("Junior (1-3 years)")
                .description("1년에서 3년 사이의 경력을 가진 직급")
                .descriptionEn("Position with 1 to 3 years of experience")
                .minYears(1)
                .maxYears(3)
                .active(true)
                .sortOrder(2)
                .createdAt(now)
                .updatedAt(now)
                .build());

        levels.add(ExperienceLevel.builder()
                .code(ExperienceLevel.MID_LEVEL)
                .displayName("미드레벨 (4-7년)")
                .displayNameEn("Mid-Level (4-7 years)")
                .description("4년에서 7년 사이의 경력을 가진 직급")
                .descriptionEn("Position with 4 to 7 years of experience")
                .minYears(4)
                .maxYears(7)
                .active(true)
                .sortOrder(3)
                .createdAt(now)
                .updatedAt(now)
                .build());

        levels.add(ExperienceLevel.builder()
                .code(ExperienceLevel.SENIOR)
                .displayName("시니어 (8년 이상)")
                .displayNameEn("Senior (8+ years)")
                .description("8년 이상의 경력을 가진 직급")
                .descriptionEn("Position with 8 or more years of experience")
                .minYears(8)
                .maxYears(null)
                .active(true)
                .sortOrder(4)
                .createdAt(now)
                .updatedAt(now)
                .build());

        levels.add(ExperienceLevel.builder()
                .code(ExperienceLevel.EXECUTIVE)
                .displayName("임원급")
                .displayNameEn("Executive")
                .description("회사의 의사결정에 참여하는 고위 관리직")
                .descriptionEn("High-level management position involved in company decision-making")
                .minYears(10)
                .maxYears(null)
                .active(true)
                .sortOrder(5)
                .createdAt(now)
                .updatedAt(now)
                .build());

        experienceLevelRepository.saveAll(levels);
        log.info("ExperienceLevel 데이터 초기화가 완료되었습니다. 총 {}개의 레벨이 생성되었습니다.", levels.size());
    }

    @Override
    public String getEntityName() {
        return "ExperienceLevel";
    }

    @Override
    public int getOrder() {
        return 20;
    }
}