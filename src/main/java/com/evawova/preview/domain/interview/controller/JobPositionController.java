package com.evawova.preview.domain.interview.controller;

import com.evawova.preview.domain.interview.dto.JobPositionDto;
import com.evawova.preview.domain.interview.service.JobPositionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/job-positions")
@RequiredArgsConstructor
public class JobPositionController {

    private final JobPositionService jobPositionService;

    /**
     * 모든 직무 조회
     */
    @GetMapping
    public ResponseEntity<List<JobPositionDto>> getAllJobPositions() {
        return ResponseEntity.ok(jobPositionService.getAllJobPositions());
    }

    /**
     * ID로 직무 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<JobPositionDto> getJobPositionById(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.ok(jobPositionService.getJobPositionById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * ID로 직무 조회 (스킬 포함)
     */
    @GetMapping("/{id}/with-skills")
    public ResponseEntity<JobPositionDto> getJobPositionWithSkillsById(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.ok(jobPositionService.getJobPositionWithSkillsById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 직군 ID로 직무 목록 조회
     */
    @GetMapping("/by-job-field/{jobFieldId}")
    public ResponseEntity<List<JobPositionDto>> getJobPositionsByJobFieldId(@PathVariable("jobFieldId") Long jobFieldId) {
        try {
            return ResponseEntity.ok(jobPositionService.getJobPositionsByJobFieldId(jobFieldId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 코드로 직무 조회
     */
    @GetMapping("/by-code/{code}")
    public ResponseEntity<JobPositionDto> getJobPositionByCode(@PathVariable("code") String code) {
        try {
            return ResponseEntity.ok(jobPositionService.getJobPositionByCode(code));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}