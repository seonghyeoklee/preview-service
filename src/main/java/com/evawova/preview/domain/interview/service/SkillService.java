package com.evawova.preview.domain.interview.service;

import com.evawova.preview.domain.interview.dto.SkillDto;
import com.evawova.preview.domain.interview.entity.Skill;
import com.evawova.preview.domain.interview.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 스킬 관련 비즈니스 로직을 처리하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;

    /**
     * 모든 스킬 조회
     */
    @Transactional(readOnly = true)
    public List<SkillDto> getAllSkills() {
        return skillRepository.findAll().stream()
                .map(SkillDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * ID로 스킬 조회
     */
    @Transactional(readOnly = true)
    public SkillDto getSkillById(Long id) {
        return skillRepository.findById(id)
                .map(SkillDto::fromEntity)
                .orElseThrow(() -> new IllegalArgumentException("스킬을 찾을 수 없습니다. ID: " + id));
    }

    /**
     * ID로 스킬 조회 (직무 관계 포함)
     */
    @Transactional(readOnly = true)
    public SkillDto getSkillWithJobPositionsById(Long id) {
        return skillRepository.findById(id)
                .map(SkillDto::fromEntityWithJobPositions)
                .orElseThrow(() -> new IllegalArgumentException("스킬을 찾을 수 없습니다. ID: " + id));
    }

    /**
     * 코드로 스킬 조회
     */
    @Transactional(readOnly = true)
    public SkillDto getSkillByCode(String code) {
        return skillRepository.findByCode(code)
                .map(SkillDto::fromEntity)
                .orElseThrow(() -> new IllegalArgumentException("스킬을 찾을 수 없습니다. 코드: " + code));
    }

    /**
     * 직군 유형으로 스킬 목록 조회
     */
    @Transactional(readOnly = true)
    public List<SkillDto> getSkillsByFieldType(String fieldType) {
        return skillRepository.findByPrimaryFieldType(fieldType).stream()
                .map(SkillDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 인기 스킬 목록 조회
     */
    @Transactional(readOnly = true)
    public List<SkillDto> getPopularSkills() {
        return skillRepository.findByIsPopularTrue().stream()
                .map(SkillDto::fromEntity)
                .collect(Collectors.toList());
    }
}