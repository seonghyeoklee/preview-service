package com.evawova.preview.domain.interview.controller;

import com.evawova.preview.common.response.ApiResponse;
import com.evawova.preview.domain.interview.dto.InterviewCategoryDto;
import com.evawova.preview.domain.interview.dto.SubCategoryWithSkillsDto;
import com.evawova.preview.domain.interview.service.InterviewCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/interview/categories")
@RequiredArgsConstructor
@Tag(name = "Interview Categories", description = "면접 카테고리 관련 API")
public class InterviewCategoryController {

    private final InterviewCategoryService interviewCategoryService;

    @GetMapping
    @Operation(summary = "카테고리 계층 구조 조회", description = "모든 인터뷰 카테고리를 계층형 구조로 조회합니다. 대분류와 그 하위의 모든 카테고리가 트리 구조로 반환됩니다.")
    public ApiResponse<List<InterviewCategoryDto>> getCategories() {
        return ApiResponse.success(interviewCategoryService.getAllCategories());
    }

    @GetMapping("/main")
    @Operation(summary = "대분류 카테고리 조회", description = "최상위 카테고리(대분류)를 조회합니다.")
    public ApiResponse<List<InterviewCategoryDto>> getMainCategories() {
        return ApiResponse.success(interviewCategoryService.getMainCategories());
    }

    @GetMapping("/sub/{parentId}")
    @Operation(summary = "중분류 카테고리 조회", description = "특정 대분류 카테고리의 하위 카테고리(중분류)를 조회합니다.")
    public ApiResponse<List<InterviewCategoryDto>> getSubCategories(@PathVariable Long parentId) {
        return ApiResponse.success(interviewCategoryService.getSubCategories(parentId));
    }

    @GetMapping("/{id}/skills")
    @Operation(summary = "카테고리 스킬 조회", description = "특정 카테고리와 연관된 스킬 목록을 조회합니다.")
    public ApiResponse<SubCategoryWithSkillsDto> getCategoryWithSkills(@PathVariable Long id) {
        return ApiResponse.success(interviewCategoryService.getCategoryWithSkills(id));
    }

    @GetMapping("/hierarchical")
    @Operation(summary = "계층형 카테고리 조회", description = "모든 카테고리를 계층형 구조로 조회합니다.")
    public ApiResponse<List<InterviewCategoryDto>> getHierarchicalCategories() {
        return ApiResponse.success(interviewCategoryService.getHierarchicalCategories());
    }

    @GetMapping("/hierarchical/{id}")
    @Operation(summary = "특정 카테고리의 계층형 구조 조회", description = "특정 카테고리와 그 하위 카테고리를 계층형 구조로 조회합니다.")
    public ApiResponse<InterviewCategoryDto> getCategoryHierarchy(@PathVariable Long id) {
        return ApiResponse.success(interviewCategoryService.getCategoryHierarchy(id));
    }
}