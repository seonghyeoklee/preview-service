package com.evawova.preview.domain.interview.controller;

import com.evawova.preview.common.response.ApiResponse;
import com.evawova.preview.domain.interview.dto.SkillResponseDto;
import com.evawova.preview.domain.interview.model.JobRole;
import com.evawova.preview.domain.interview.service.SkillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 기술 스택 API
 */
@RestController
@RequestMapping("/api/v1/interview/skills")
@Tag(name = "Skills", description = "기술 스택 관련 API")
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;

    /**
     * 모든 스킬 목록을 조회합니다.
     */
    @GetMapping
    @Operation(summary = "모든 스킬 조회", description = "등록된 모든 스킬 목록을 조회합니다.")
    public ApiResponse<List<SkillResponseDto>> getAllSkills() {
        return ApiResponse.success(skillService.getAllSkills());
    }

    /**
     * 특정 직무와 관련된 모든 스킬을 조회합니다.
     */
    @GetMapping("/job-role/{jobRole}")
    @Operation(summary = "직무별 스킬 조회", description = "특정 직무와 관련된 모든 스킬을 조회합니다.")
    public ApiResponse<List<SkillResponseDto>> getSkillsByJobRole(
            @PathVariable(name = "jobRole") JobRole jobRole) {
        return ApiResponse.success(skillService.getSkillsByJobRole(jobRole));
    }

    /**
     * 인기 있는 스킬 목록을 조회합니다.
     */
    @GetMapping("/popular")
    @Operation(summary = "인기 스킬 조회", description = "인기 있는 스킬 목록을 조회합니다.")
    public ApiResponse<List<SkillResponseDto>> getPopularSkills() {
        return ApiResponse.success(skillService.getPopularSkills());
    }

    /**
     * 특정 직무와 관련된 인기 스킬 목록을 조회합니다.
     */
    @GetMapping("/job-role/{jobRole}/popular")
    @Operation(summary = "직무별 인기 스킬 조회", description = "특정 직무와 관련된 인기 스킬 목록을 조회합니다.")
    public ApiResponse<List<SkillResponseDto>> getPopularSkillsByJobRole(
            @PathVariable(name = "jobRole") JobRole jobRole) {
        return ApiResponse.success(skillService.getPopularSkillsByJobRole(jobRole));
    }
}