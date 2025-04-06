package com.evawova.preview.domain.interview.controller;

import com.evawova.preview.domain.interview.dto.ExperienceLevelDto;
import com.evawova.preview.domain.interview.service.ExperienceLevelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/experience-levels")
@RequiredArgsConstructor
public class ExperienceLevelController {

    private final ExperienceLevelService experienceLevelService;

    /**
     * 모든 경력 수준 조회
     */
    @GetMapping
    public ResponseEntity<List<ExperienceLevelDto>> getAllExperienceLevels() {
        return ResponseEntity.ok(experienceLevelService.getAllExperienceLevelsSorted());
    }

    /**
     * 활성화된 경력 수준만 조회
     */
    @GetMapping("/active")
    public ResponseEntity<List<ExperienceLevelDto>> getActiveExperienceLevels() {
        return ResponseEntity.ok(experienceLevelService.getActiveExperienceLevels());
    }

    /**
     * ID로 경력 수준 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<ExperienceLevelDto> getExperienceLevelById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(experienceLevelService.getExperienceLevelById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 코드로 경력 수준 조회
     */
    @GetMapping("/by-code/{code}")
    public ResponseEntity<ExperienceLevelDto> getExperienceLevelByCode(@PathVariable String code) {
        try {
            return ResponseEntity.ok(experienceLevelService.getExperienceLevelByCode(code));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 경력 연차에 해당하는 경력 수준 조회
     */
    @GetMapping("/by-years/{years}")
    public ResponseEntity<List<ExperienceLevelDto>> getExperienceLevelsByYears(@PathVariable Integer years) {
        return ResponseEntity.ok(experienceLevelService.getExperienceLevelsByYears(years));
    }
}