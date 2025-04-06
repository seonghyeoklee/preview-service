package com.evawova.preview.domain.interview.service;

import com.evawova.preview.domain.interview.dto.JobFieldDto;
import com.evawova.preview.domain.interview.repository.JobFieldRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 직군 관련 비즈니스 로직을 처리하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JobFieldService {

    private final JobFieldRepository jobFieldRepository;

    /**
     * 모든 직군 조회
     */
    @Transactional(readOnly = true)
    public List<JobFieldDto> getAllJobFields() {
        return jobFieldRepository.findAll().stream()
                .map(JobFieldDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * ID로 직군 조회
     */
    @Transactional(readOnly = true)
    public JobFieldDto getJobFieldById(Long id) {
        return jobFieldRepository.findById(id)
                .map(JobFieldDto::fromEntity)
                .orElseThrow(() -> new IllegalArgumentException("직군을 찾을 수 없습니다. ID: " + id));
    }

    /**
     * ID로 직군 조회 (직무 포함)
     */
    @Transactional(readOnly = true)
    public JobFieldDto getJobFieldWithPositionsById(Long id) {
        return jobFieldRepository.findById(id)
                .map(JobFieldDto::fromEntityWithPositions)
                .orElseThrow(() -> new IllegalArgumentException("직군을 찾을 수 없습니다. ID: " + id));
    }

    /**
     * 코드로 직군 조회
     */
    @Transactional(readOnly = true)
    public JobFieldDto getJobFieldByCode(String code) {
        return jobFieldRepository.findByCode(code)
                .map(JobFieldDto::fromEntity)
                .orElseThrow(() -> new IllegalArgumentException("직군을 찾을 수 없습니다. 코드: " + code));
    }
}