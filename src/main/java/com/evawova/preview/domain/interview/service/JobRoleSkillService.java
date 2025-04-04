package com.evawova.preview.domain.interview.service;

import com.evawova.preview.domain.interview.entity.JobRoleSkill;
import com.evawova.preview.domain.interview.entity.Skill;
import com.evawova.preview.domain.interview.model.JobRole;
import com.evawova.preview.domain.interview.repository.JobRoleSkillRepository;
import com.evawova.preview.domain.interview.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 직무별 기술 스택 관리 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JobRoleSkillService {

    private final JobRoleSkillRepository jobRoleSkillRepository;
    private final SkillRepository skillRepository;

    /**
     * 특정 직무에 해당하는 기술 스택 목록을 조회합니다.
     *
     * @param jobRole 직무 역할
     * @return 기술 스택 이름 목록
     */
    public List<String> getSkillsByJobRole(JobRole jobRole) {
        return jobRoleSkillRepository.findSkillsByJobRole(jobRole).stream()
                .map(Skill::getName)
                .collect(Collectors.toList());
    }

    /**
     * 특정 직무에 해당하는 기술 스택 목록을 영문명으로 조회합니다.
     *
     * @param jobRole 직무 역할
     * @return 기술 스택 영문명 목록
     */
    public List<String> getSkillsEnByJobRole(JobRole jobRole) {
        return jobRoleSkillRepository.findSkillsByJobRole(jobRole).stream()
                .map(Skill::getNameEn)
                .collect(Collectors.toList());
    }

    /**
     * 모든 직무에 대한 기술 스택 목록을 조회합니다.
     *
     * @return 직무별 기술 스택 목록 맵
     */
    public Map<String, Object> getAllJobRoleSkills() {
        return Map.of(
                "기술 스택 전체", getAllSkills(),
                "직무별 기술 스택", getJobRoleSkillMap());
    }

    /**
     * 모든 기술 스택 목록을 조회합니다.
     *
     * @return 기술 스택 이름 목록
     */
    public List<String> getAllSkills() {
        return skillRepository.findAll().stream()
                .map(Skill::getName)
                .collect(Collectors.toList());
    }

    /**
     * 직무별 기술 스택 목록 맵을 생성합니다.
     *
     * @return 직무별 기술 스택 목록 맵
     */
    private Map<String, List<String>> getJobRoleSkillMap() {
        return java.util.Arrays.stream(JobRole.values())
                .collect(Collectors.toMap(
                        JobRole::getDisplayName,
                        this::getSkillsByJobRole));
    }

    /**
     * 특정 직무에 특정 기술 스택을 매핑합니다.
     *
     * @param jobRole    직무 역할
     * @param skillId    기술 스택 ID
     * @param importance 중요도 (1: 필수, 2: 권장, 3: 선택)
     */
    @Transactional
    public void mapSkillToJobRole(JobRole jobRole, Long skillId, Integer importance) {
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 기술 스택입니다: " + skillId));

        // 이미 매핑이 존재하는지 확인
        if (jobRoleSkillRepository.existsByJobRoleAndSkill(jobRole, skill)) {
            throw new IllegalArgumentException("이미 매핑된 기술 스택입니다.");
        }

        JobRoleSkill jobRoleSkill = JobRoleSkill.builder()
                .jobRole(jobRole)
                .skill(skill)
                .importance(importance)
                .build();

        jobRoleSkillRepository.save(jobRoleSkill);
    }

    /**
     * 직무별 기술 스택 매핑을 일괄 생성합니다.
     *
     * @param jobRoleSkillMappings 직무별 기술 스택 매핑 목록
     */
    @Transactional
    public void createJobRoleSkillMappings(List<JobRoleSkillMapping> jobRoleSkillMappings) {
        List<JobRoleSkill> entities = new ArrayList<>();

        for (JobRoleSkillMapping mapping : jobRoleSkillMappings) {
            Skill skill = skillRepository.findByNameEn(mapping.getSkillName())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 기술 스택입니다: " + mapping.getSkillName()));

            // 이미 매핑이 존재하면 스킵
            if (jobRoleSkillRepository.existsByJobRoleAndSkill(mapping.getJobRole(), skill)) {
                continue;
            }

            JobRoleSkill jobRoleSkill = JobRoleSkill.builder()
                    .jobRole(mapping.getJobRole())
                    .skill(skill)
                    .importance(mapping.getImportance())
                    .build();

            entities.add(jobRoleSkill);
        }

        jobRoleSkillRepository.saveAll(entities);
    }

    /**
     * 직무별 기술 스택 매핑 정보를 담는 내부 클래스
     */
    public static class JobRoleSkillMapping {
        private JobRole jobRole;
        private String skillName;
        private Integer importance;

        public JobRoleSkillMapping(JobRole jobRole, String skillName, Integer importance) {
            this.jobRole = jobRole;
            this.skillName = skillName;
            this.importance = importance;
        }

        public JobRole getJobRole() {
            return jobRole;
        }

        public String getSkillName() {
            return skillName;
        }

        public Integer getImportance() {
            return importance;
        }
    }
}