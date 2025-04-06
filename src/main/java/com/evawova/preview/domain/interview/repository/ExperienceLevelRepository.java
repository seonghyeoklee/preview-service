package com.evawova.preview.domain.interview.repository;

import com.evawova.preview.domain.interview.entity.ExperienceLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExperienceLevelRepository extends JpaRepository<ExperienceLevel, Long> {

    /**
     * 코드로 경력 수준 조회
     */
    Optional<ExperienceLevel> findByCode(String code);

    /**
     * 활성화된 경력 수준만 조회
     */
    List<ExperienceLevel> findByActiveTrue();

    /**
     * 정렬 순서대로 경력 수준 조회
     */
    List<ExperienceLevel> findAllByOrderBySortOrderAsc();

    /**
     * 활성화된 경력 수준을 정렬 순서대로 조회
     */
    List<ExperienceLevel> findByActiveTrueOrderBySortOrderAsc();

    /**
     * 최소 경력 이상인 경력 수준 조회
     */
    List<ExperienceLevel> findByMinYearsGreaterThanEqualOrderBySortOrderAsc(Integer minYears);

    /**
     * 최대 경력 이하인 경력 수준 조회
     */
    List<ExperienceLevel> findByMaxYearsLessThanEqualOrderBySortOrderAsc(Integer maxYears);

    /**
     * 경력 범위에 해당하는 경력 수준 조회
     */
    List<ExperienceLevel> findByMinYearsLessThanEqualAndMaxYearsGreaterThanEqualOrderBySortOrderAsc(
            Integer years, Integer yearsAgain);
}