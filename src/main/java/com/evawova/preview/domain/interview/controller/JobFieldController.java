package com.evawova.preview.domain.interview.controller;

import com.evawova.preview.domain.interview.dto.JobFieldDto;
import com.evawova.preview.domain.interview.service.JobFieldService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/job-fields")
@RequiredArgsConstructor
public class JobFieldController {

    private final JobFieldService jobFieldService;

    /**
     * 직군 전체 조회
     */
    @GetMapping
    public ResponseEntity<List<JobFieldDto>> getAllJobFields() {
        return ResponseEntity.ok(jobFieldService.getAllJobFields());
    }

    /**
     * ID로 직군 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<JobFieldDto> getJobFieldById(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.ok(jobFieldService.getJobFieldById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * ID로 직군 조회 (직무 포함)
     */
    @GetMapping("/{id}/with-positions")
    public ResponseEntity<JobFieldDto> getJobFieldWithPositionsById(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.ok(jobFieldService.getJobFieldWithPositionsById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 코드로 직군 조회
     */
    @GetMapping("/by-code/{code}")
    public ResponseEntity<JobFieldDto> getJobFieldByCode(@PathVariable("code") String code) {
        try {
            return ResponseEntity.ok(jobFieldService.getJobFieldByCode(code));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}