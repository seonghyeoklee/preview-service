package com.evawova.preview.domain.interview.controller;

import com.evawova.preview.common.response.ApiResponse;
import com.evawova.preview.domain.interview.dto.InterviewCategoryDto;
import com.evawova.preview.domain.interview.service.InterviewCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/interview/categories")
@Tag(name = "Interview Category", description = "인터뷰 카테고리 API")
@RequiredArgsConstructor
public class InterviewCategoryController {

    private final InterviewCategoryService interviewCategoryService;

    @GetMapping
    @Operation(summary = "모든 인터뷰 카테고리 조회")
    @PreAuthorize("hasAnyRole('USER_FREE', 'USER_STANDARD', 'USER_PRO', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<InterviewCategoryDto>>> getAllCategories() {
        List<InterviewCategoryDto> categories = interviewCategoryService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.success(categories));
    }
}