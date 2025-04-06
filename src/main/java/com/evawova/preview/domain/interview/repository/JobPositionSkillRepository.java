package com.evawova.preview.domain.interview.repository;

import com.evawova.preview.domain.interview.entity.JobPosition;
import com.evawova.preview.domain.interview.entity.JobPositionSkill;
import com.evawova.preview.domain.interview.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobPositionSkillRepository extends JpaRepository<JobPositionSkill, Long> {

    /**
     * 직무로 스킬 연결 정보 조회
     */
    List<JobPositionSkill> findByJobPosition(JobPosition jobPosition);

    /**
     * 스킬로 직무 연결 정보 조회
     */
    List<JobPositionSkill> findBySkill(Skill skill);

    /**
     * 직무와 스킬로 연결 정보 조회
     */
    Optional<JobPositionSkill> findByJobPositionAndSkill(JobPosition jobPosition, Skill skill);

    /**
     * 중요도 순으로 직무별 스킬 조회
     */
    List<JobPositionSkill> findByJobPositionOrderByImportanceDesc(JobPosition jobPosition);

    /**
     * 직무에 연결된 스킬 모두 삭제
     */
    void deleteByJobPosition(JobPosition jobPosition);

    /**
     * 특정 스킬에 연결된 직무 관계 모두 삭제
     */
    void deleteBySkill(Skill skill);

    /**
     * 직무 ID로 관계 조회
     */
    List<JobPositionSkill> findAllByJobPosition_Id(Long jobPositionId);

    /**
     * 스킬 ID로 관계 조회
     */
    List<JobPositionSkill> findAllBySkill_Id(Long skillId);
}