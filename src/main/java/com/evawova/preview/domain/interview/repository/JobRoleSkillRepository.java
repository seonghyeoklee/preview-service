package com.evawova.preview.domain.interview.repository;

import com.evawova.preview.domain.interview.entity.JobRoleSkill;
import com.evawova.preview.domain.interview.entity.Skill;
import com.evawova.preview.domain.interview.model.JobRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JobRoleSkillRepository extends JpaRepository<JobRoleSkill, Long> {

    /**
     * 특정 직무에 해당하는 모든 기술 스택 매핑을 조회합니다.
     */
    List<JobRoleSkill> findByJobRoleOrderByImportanceAsc(JobRole jobRole);

    /**
     * 특정 직무에 해당하는 중요도별 기술 스택 매핑을 조회합니다.
     */
    List<JobRoleSkill> findByJobRoleAndImportanceOrderByImportanceAsc(JobRole jobRole, Integer importance);

    /**
     * 특정 직무에 해당하는 모든 기술 스택을 조회합니다.
     */
    @Query("SELECT js.skill FROM JobRoleSkill js WHERE js.jobRole = :jobRole ORDER BY js.importance ASC")
    List<Skill> findSkillsByJobRole(@Param("jobRole") JobRole jobRole);

    /**
     * 특정 기술 스택이 매핑된 모든 직무 역할을 조회합니다.
     */
    @Query("SELECT DISTINCT js.jobRole FROM JobRoleSkill js WHERE js.skill.id = :skillId")
    List<JobRole> findJobRolesBySkillId(@Param("skillId") Long skillId);

    /**
     * 특정 기술 스택이 해당 직무에 매핑되어 있는지 확인합니다.
     */
    boolean existsByJobRoleAndSkill(JobRole jobRole, Skill skill);
}