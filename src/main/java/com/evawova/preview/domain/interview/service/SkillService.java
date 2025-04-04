package com.evawova.preview.domain.interview.service;

import com.evawova.preview.domain.interview.dto.SkillResponseDto;
import com.evawova.preview.domain.interview.entity.Skill;
import com.evawova.preview.domain.interview.model.JobRole;
import com.evawova.preview.domain.interview.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 기술 스택 관리 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SkillService {

    private final SkillRepository skillRepository;

    /**
     * 모든 스킬 목록을 조회합니다.
     */
    public List<SkillResponseDto> getAllSkills() {
        return skillRepository.findAll().stream()
                .map(SkillResponseDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 특정 직무와 관련된 모든 스킬을 조회합니다.
     */
    public List<SkillResponseDto> getSkillsByJobRole(JobRole jobRole) {
        return skillRepository.findByPrimaryJobRole(jobRole).stream()
                .map(SkillResponseDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 인기 있는 스킬 목록을 조회합니다.
     */
    public List<SkillResponseDto> getPopularSkills() {
        return skillRepository.findByIsPopularTrue().stream()
                .map(SkillResponseDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 특정 직무와 관련된 인기 스킬 목록을 조회합니다.
     */
    public List<SkillResponseDto> getPopularSkillsByJobRole(JobRole jobRole) {
        return skillRepository.findByPrimaryJobRoleAndIsPopularTrue(jobRole).stream()
                .map(SkillResponseDto::from)
                .collect(Collectors.toList());
    }
}