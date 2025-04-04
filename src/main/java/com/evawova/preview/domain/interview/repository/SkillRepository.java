package com.evawova.preview.domain.interview.repository;

import com.evawova.preview.domain.interview.entity.Skill;
import com.evawova.preview.domain.interview.model.JobRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SkillRepository extends JpaRepository<Skill, Long> {
    Optional<Skill> findByNameEn(String nameEn);

    /**
     * 특정 직무와 관련된 모든 스킬을 조회합니다.
     */
    List<Skill> findByPrimaryJobRole(JobRole jobRole);

    /**
     * 인기 있는 스킬 목록을 조회합니다.
     */
    List<Skill> findByIsPopularTrue();

    /**
     * 특정 직무와 관련된 인기 스킬 목록을 조회합니다.
     */
    List<Skill> findByPrimaryJobRoleAndIsPopularTrue(JobRole jobRole);
}