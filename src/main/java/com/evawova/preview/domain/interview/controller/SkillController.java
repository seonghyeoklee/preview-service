package com.evawova.preview.domain.interview.controller;

import com.evawova.preview.domain.interview.dto.SkillDto;
import com.evawova.preview.domain.interview.service.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/skills")
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;

    /**
     * 모든 스킬 조회
     */
    @GetMapping
    public ResponseEntity<List<SkillDto>> getAllSkills() {
        return ResponseEntity.ok(skillService.getAllSkills());
    }

    /**
     * ID로 스킬 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<SkillDto> getSkillById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(skillService.getSkillById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * ID로 스킬 조회 (직무 관계 포함)
     */
    @GetMapping("/{id}/with-job-positions")
    public ResponseEntity<SkillDto> getSkillWithJobPositionsById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(skillService.getSkillWithJobPositionsById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 코드로 스킬 조회
     */
    @GetMapping("/by-code/{code}")
    public ResponseEntity<SkillDto> getSkillByCode(@PathVariable String code) {
        try {
            return ResponseEntity.ok(skillService.getSkillByCode(code));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 직군 유형으로 스킬 목록 조회
     */
    @GetMapping("/by-field-type/{fieldType}")
    public ResponseEntity<List<SkillDto>> getSkillsByFieldType(@PathVariable String fieldType) {
        return ResponseEntity.ok(skillService.getSkillsByFieldType(fieldType));
    }

    /**
     * 인기 스킬 목록 조회
     */
    @GetMapping("/popular")
    public ResponseEntity<List<SkillDto>> getPopularSkills() {
        return ResponseEntity.ok(skillService.getPopularSkills());
    }
}