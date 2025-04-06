package com.evawova.preview.domain.interview.controller;

import com.evawova.preview.domain.interview.entity.JobPositionSkill;
import com.evawova.preview.domain.interview.repository.JobPositionSkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/job-position-skills")
@RequiredArgsConstructor
public class JobPositionSkillController {

    private final JobPositionSkillRepository jobPositionSkillRepository;

    /**
     * ID로 직무-스킬 관계 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<JobPositionSkill> getJobPositionSkillById(@PathVariable Long id) {
        return jobPositionSkillRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 직무 ID로 스킬 관계 조회
     */
    @GetMapping("/by-job-position/{jobPositionId}")
    public ResponseEntity<List<JobPositionSkill>> getByJobPositionId(@PathVariable Long jobPositionId) {
        List<JobPositionSkill> relations = jobPositionSkillRepository.findAllByJobPosition_Id(jobPositionId);
        return ResponseEntity.ok(relations);
    }

    /**
     * 스킬 ID로 직무 관계 조회
     */
    @GetMapping("/by-skill/{skillId}")
    public ResponseEntity<List<JobPositionSkill>> getBySkillId(@PathVariable Long skillId) {
        List<JobPositionSkill> relations = jobPositionSkillRepository.findAllBySkill_Id(skillId);
        return ResponseEntity.ok(relations);
    }
}