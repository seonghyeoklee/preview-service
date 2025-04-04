package com.evawova.preview.domain.interview.controller;

import com.evawova.preview.common.response.ApiResponse;
import com.evawova.preview.domain.interview.model.JobRole;
import com.evawova.preview.domain.interview.service.JobRoleSkillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 직무별 기술 스택 API
 */
@RestController
@RequestMapping("/api/v1/interview/job-skills")
@Tag(name = "Job Role Skills", description = "직무별 기술 스택 관련 API")
@RequiredArgsConstructor
public class JobRoleSkillController {

    private final JobRoleSkillService jobRoleSkillService;

    /**
     * 특정 직무에 해당하는 기술 스택 목록을 조회합니다.
     */
    @GetMapping("/job-role/{jobRole}")
    @Operation(summary = "직무별 기술 스택 조회", description = "특정 직무에 해당하는 기술 스택 목록을 조회합니다.")
    public ApiResponse<List<String>> getSkillsByJobRole(@PathVariable(name = "jobRole") JobRole jobRole) {
        return ApiResponse.success(jobRoleSkillService.getSkillsByJobRole(jobRole));
    }

    /**
     * 특정 직무에 해당하는 기술 스택 목록을 영문명으로 조회합니다.
     */
    @GetMapping("/job-role/{jobRole}/en")
    @Operation(summary = "직무별 기술 스택 영문명 조회", description = "특정 직무에 해당하는 기술 스택 목록을 영문명으로 조회합니다.")
    public ApiResponse<List<String>> getSkillsEnByJobRole(@PathVariable(name = "jobRole") JobRole jobRole) {
        return ApiResponse.success(jobRoleSkillService.getSkillsEnByJobRole(jobRole));
    }

    /**
     * 모든 직무에 대한 기술 스택 목록을 조회합니다.
     */
    @GetMapping
    @Operation(summary = "모든 직무별 기술 스택 조회", description = "모든 직무에 대한 기술 스택 목록을 조회합니다.")
    public ApiResponse<Map<String, Object>> getAllJobRoleSkills() {
        return ApiResponse.success(jobRoleSkillService.getAllJobRoleSkills());
    }

    /**
     * 모든 기술 스택 목록을 조회합니다.
     */
    @GetMapping("/all")
    @Operation(summary = "모든 기술 스택 조회", description = "등록된 모든 기술 스택 목록을 조회합니다.")
    public ApiResponse<List<String>> getAllSkills() {
        return ApiResponse.success(jobRoleSkillService.getAllSkills());
    }
}