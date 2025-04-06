package com.evawova.preview.domain.interview.service;

import com.evawova.preview.domain.interview.dto.ExperienceLevelDto;
import com.evawova.preview.domain.interview.entity.ExperienceLevel;
import com.evawova.preview.domain.interview.repository.ExperienceLevelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 경력 수준 관련 비즈니스 로직을 처리하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExperienceLevelService {

    private final ExperienceLevelRepository experienceLevelRepository;

    /**
     * 모든 경력 수준 조회
     */
    @Transactional(readOnly = true)
    public List<ExperienceLevelDto> getAllExperienceLevels() {
        return experienceLevelRepository.findAll().stream()
                .map(ExperienceLevelDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 정렬 순서대로 모든 경력 수준 조회
     */
    @Transactional(readOnly = true)
    public List<ExperienceLevelDto> getAllExperienceLevelsSorted() {
        return experienceLevelRepository.findAllByOrderBySortOrderAsc().stream()
                .map(ExperienceLevelDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 활성화된 경력 수준만 조회 (정렬 순서대로)
     */
    @Transactional(readOnly = true)
    public List<ExperienceLevelDto> getActiveExperienceLevels() {
        return experienceLevelRepository.findByActiveTrueOrderBySortOrderAsc().stream()
                .map(ExperienceLevelDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * ID로 경력 수준 조회
     */
    @Transactional(readOnly = true)
    public ExperienceLevelDto getExperienceLevelById(Long id) {
        return experienceLevelRepository.findById(id)
                .map(ExperienceLevelDto::fromEntity)
                .orElseThrow(() -> new IllegalArgumentException("경력 수준을 찾을 수 없습니다. ID: " + id));
    }

    /**
     * 코드로 경력 수준 조회
     */
    @Transactional(readOnly = true)
    public ExperienceLevelDto getExperienceLevelByCode(String code) {
        return experienceLevelRepository.findByCode(code)
                .map(ExperienceLevelDto::fromEntity)
                .orElseThrow(() -> new IllegalArgumentException("경력 수준을 찾을 수 없습니다. 코드: " + code));
    }

    /**
     * 경력 연차에 해당하는 경력 수준 조회
     */
    @Transactional(readOnly = true)
    public List<ExperienceLevelDto> getExperienceLevelsByYears(Integer years) {
        return experienceLevelRepository
                .findByMinYearsLessThanEqualAndMaxYearsGreaterThanEqualOrderBySortOrderAsc(years, years).stream()
                .map(ExperienceLevelDto::fromEntity)
                .collect(Collectors.toList());
    }
}