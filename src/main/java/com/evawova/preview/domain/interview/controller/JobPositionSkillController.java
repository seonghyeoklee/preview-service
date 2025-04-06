package com.evawova.preview.domain.interview.controller;

import com.evawova.preview.domain.interview.dto.JobPositionSkillDto;
import com.evawova.preview.domain.interview.service.JobPositionSkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/job-position-skills")
@RequiredArgsConstructor
public class JobPositionSkillController {

    private final JobPositionSkillService jobPositionSkillService;

    /**
     * ID로 직무-스킬 관계 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<JobPositionSkillDto> getJobPositionSkillById(@PathVariable("id") Long id) {
        try {
            JobPositionSkillDto dto = jobPositionSkillService.getJobPositionSkillById(id);
            return ResponseEntity.ok(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 직무 ID로 스킬 관계 조회
     */
    @GetMapping("/by-job-position/{jobPositionId}")
    public ResponseEntity<List<JobPositionSkillDto>> getByJobPositionId(@PathVariable("jobPositionId") Long jobPositionId) {
        try {
            List<JobPositionSkillDto> relations = jobPositionSkillService.getSkillsByJobPositionId(jobPositionId);
            return ResponseEntity.ok(relations);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 스킬 ID로 직무 관계 조회
     */
    @GetMapping("/by-skill/{skillId}")
    public ResponseEntity<List<JobPositionSkillDto>> getBySkillId(@PathVariable("skillId") Long skillId) {
        try {
            List<JobPositionSkillDto> relations = jobPositionSkillService.getJobPositionsBySkillId(skillId);
            return ResponseEntity.ok(relations);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}