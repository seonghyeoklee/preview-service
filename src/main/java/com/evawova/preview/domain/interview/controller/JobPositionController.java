package com.evawova.preview.domain.interview.controller;

import com.evawova.preview.domain.interview.entity.JobPosition;
import com.evawova.preview.domain.interview.repository.JobPositionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/job-positions")
@RequiredArgsConstructor
public class JobPositionController {

    private final JobPositionRepository jobPositionRepository;

    /**
     * ID로 직무 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<JobPosition> getJobPositionById(@PathVariable Long id) {
        return jobPositionRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}