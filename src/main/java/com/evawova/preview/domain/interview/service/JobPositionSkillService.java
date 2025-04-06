package com.evawova.preview.domain.interview.service;

import com.evawova.preview.domain.interview.dto.JobPositionSkillDto;
import com.evawova.preview.domain.interview.entity.JobPosition;
import com.evawova.preview.domain.interview.entity.JobPositionSkill;
import com.evawova.preview.domain.interview.entity.Skill;
import com.evawova.preview.domain.interview.repository.JobPositionRepository;
import com.evawova.preview.domain.interview.repository.JobPositionSkillRepository;
import com.evawova.preview.domain.interview.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 직무와 스킬 관계를 관리하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JobPositionSkillService {

    private final JobPositionRepository jobPositionRepository;
    private final SkillRepository skillRepository;
    private final JobPositionSkillRepository jobPositionSkillRepository;

    /**
     * ID로 직무-스킬 관계 조회
     */
    @Transactional(readOnly = true)
    public JobPositionSkillDto getJobPositionSkillById(Long id) {
        return jobPositionSkillRepository.findById(id)
                .map(JobPositionSkillDto::fromEntity)
                .orElseThrow(() -> new IllegalArgumentException("직무-스킬 관계를 찾을 수 없습니다. ID: " + id));
    }

    /**
     * 직무 ID로 연결된 모든 스킬 관계 조회
     */
    @Transactional(readOnly = true)
    public List<JobPositionSkillDto> getSkillsByJobPositionId(Long jobPositionId) {
        // 직무 존재 여부 확인
        if (!jobPositionRepository.existsById(jobPositionId)) {
            throw new IllegalArgumentException("직무를 찾을 수 없습니다. ID: " + jobPositionId);
        }

        return jobPositionSkillRepository.findAllByJobPosition_Id(jobPositionId).stream()
                .map(JobPositionSkillDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 스킬 ID로 연결된 모든 직무 관계 조회
     */
    @Transactional(readOnly = true)
    public List<JobPositionSkillDto> getJobPositionsBySkillId(Long skillId) {
        // 스킬 존재 여부 확인
        if (!skillRepository.existsById(skillId)) {
            throw new IllegalArgumentException("스킬을 찾을 수 없습니다. ID: " + skillId);
        }

        return jobPositionSkillRepository.findAllBySkill_Id(skillId).stream()
                .map(JobPositionSkillDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 직무 ID와 스킬 ID로 관계 조회
     */
    @Transactional(readOnly = true)
    public JobPositionSkillDto getJobPositionSkillByIds(Long jobPositionId, Long skillId) {
        JobPosition jobPosition = jobPositionRepository.findById(jobPositionId)
                .orElseThrow(() -> new IllegalArgumentException("직무를 찾을 수 없습니다. ID: " + jobPositionId));

        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new IllegalArgumentException("스킬을 찾을 수 없습니다. ID: " + skillId));

        return jobPositionSkillRepository.findByJobPositionAndSkill(jobPosition, skill)
                .map(JobPositionSkillDto::fromEntity)
                .orElseThrow(() -> new IllegalArgumentException(
                        "직무-스킬 관계를 찾을 수 없습니다. 직무 ID: " + jobPositionId + ", 스킬 ID: " + skillId));
    }
}