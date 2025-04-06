package com.evawova.preview.domain.interview.repository;

import com.evawova.preview.domain.interview.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {

    /**
     * 영문 이름으로 스킬 조회
     */
    Optional<Skill> findByNameEn(String nameEn);

    /**
     * 코드로 스킬 조회
     */
    Optional<Skill> findByCode(String code);

    /**
     * 직군 유형으로 스킬 목록 조회
     */
    List<Skill> findByPrimaryFieldType(String primaryFieldType);

    /**
     * 인기 스킬 목록 조회
     */
    List<Skill> findByIsPopularTrue();

    /**
     * 활성화된 스킬만 조회
     */
    List<Skill> findByActiveTrue();

    /**
     * 정렬 순서대로 스킬 조회
     */
    List<Skill> findAllByOrderBySortOrderAsc();

    /**
     * 직군 유형별로 정렬 순서대로 스킬 조회
     */
    List<Skill> findByPrimaryFieldTypeOrderBySortOrderAsc(String primaryFieldType);

    /**
     * 인기 스킬을 직군 유형별로 조회
     */
    List<Skill> findByPrimaryFieldTypeAndIsPopularTrue(String primaryFieldType);
}