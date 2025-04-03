package com.evawova.preview.domain.interview.service;

import com.evawova.preview.domain.interview.dto.JobPositionDto;
import com.evawova.preview.domain.interview.model.InterviewType;
import com.evawova.preview.domain.interview.repository.JobPositionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobPositionService {

    private final JobPositionRepository jobPositionRepository;

    @Transactional(readOnly = true)
    public List<JobPositionDto> getAllPositions() {
        return jobPositionRepository.findAllWithSkills().stream()
                .map(JobPositionDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<JobPositionDto> getPositionsByCategory(Long categoryId) {
        return jobPositionRepository.findAllByCategoryIdWithSkills(categoryId).stream()
                .map(JobPositionDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<JobPositionDto> getPositionsByCategoryType(InterviewType categoryType) {
        return jobPositionRepository.findAllByCategoryTypeWithSkills(categoryType).stream()
                .map(JobPositionDto::fromEntity)
                .collect(Collectors.toList());
    }
}