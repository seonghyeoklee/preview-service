package com.evawova.preview.domain.interview.service;

import com.evawova.preview.domain.interview.dto.JobPositionDto;
import com.evawova.preview.domain.interview.entity.JobPosition;
import com.evawova.preview.domain.interview.repository.JobPositionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 직무 관련 비즈니스 로직을 처리하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JobPositionService {

    private final JobPositionRepository jobPositionRepository;

    /**
     * 모든 직무 조회
     */
    @Transactional(readOnly = true)
    public List<JobPositionDto> getAllJobPositions() {
        return jobPositionRepository.findAll().stream()
                .map(JobPositionDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * ID로 직무 조회
     */
    @Transactional(readOnly = true)
    public JobPositionDto getJobPositionById(Long id) {
        return jobPositionRepository.findById(id)
                .map(JobPositionDto::fromEntity)
                .orElseThrow(() -> new IllegalArgumentException("직무를 찾을 수 없습니다. ID: " + id));
    }

    /**
     * ID로 직무 조회 (스킬 포함)
     */
    @Transactional(readOnly = true)
    public JobPositionDto getJobPositionWithSkillsById(Long id) {
        return jobPositionRepository.findById(id)
                .map(JobPositionDto::fromEntityWithSkills)
                .orElseThrow(() -> new IllegalArgumentException("직무를 찾을 수 없습니다. ID: " + id));
    }

    /**
     * 직군 ID로 직무 목록 조회
     */
    @Transactional(readOnly = true)
    public List<JobPositionDto> getJobPositionsByJobFieldId(Long jobFieldId) {
        return jobPositionRepository.findByJobField_Id(jobFieldId).stream()
                .map(JobPositionDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 코드로 직무 조회
     */
    @Transactional(readOnly = true)
    public JobPositionDto getJobPositionByCode(String code) {
        return jobPositionRepository.findByCode(code)
                .map(JobPositionDto::fromEntity)
                .orElseThrow(() -> new IllegalArgumentException("직무를 찾을 수 없습니다. 코드: " + code));
    }
}