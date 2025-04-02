package com.evawova.preview.domain.interview.service;

import com.evawova.preview.domain.interview.dto.JobPositionDto;
import com.evawova.preview.domain.interview.repository.JobPositionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JobPositionService {

    private final JobPositionRepository jobPositionRepository;

    public List<JobPositionDto> getAllPositions() {
        // Use the custom query to fetch skills eagerly
        return jobPositionRepository.findAllWithSkills().stream()
                .map(JobPositionDto::from)
                .collect(Collectors.toList());
    }

    // TODO: Add methods for creating, updating, deleting positions if needed.
}