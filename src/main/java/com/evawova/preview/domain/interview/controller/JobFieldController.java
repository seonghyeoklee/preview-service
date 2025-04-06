package com.evawova.preview.domain.interview.controller;

import com.evawova.preview.domain.interview.entity.JobField;
import com.evawova.preview.domain.interview.repository.JobFieldRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/job-fields")
@RequiredArgsConstructor
public class JobFieldController {

    private final JobFieldRepository jobFieldRepository;

    /**
     * ID로 직군 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<JobField> getJobFieldById(@PathVariable Long id) {
        return jobFieldRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}