package com.evawova.preview.domain.interview.controller;

import com.evawova.preview.common.response.ApiResponse;
import com.evawova.preview.domain.interview.dto.JobPositionDto;
import com.evawova.preview.domain.interview.model.InterviewType;
import com.evawova.preview.domain.interview.service.JobPositionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/interview/positions")
@Tag(name = "Job Position", description = "직무 포지션 API")
@RequiredArgsConstructor
public class JobPositionController {

    private final JobPositionService jobPositionService;

    @GetMapping
    @Operation(summary = "직무 포지션 조회", description = "모든 직무 포지션 또는 카테고리별 직무 포지션 정보를 조회합니다.")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<JobPositionDto>>> getPositions(
            @RequestParam(name = "categoryId", required = false) String categoryTypeStr) {

        List<JobPositionDto> positions;
        if (categoryTypeStr != null && !categoryTypeStr.isEmpty()) {
            try {
                // 문자열을 Enum으로 변환
                InterviewType categoryType = InterviewType.valueOf(categoryTypeStr);
                positions = jobPositionService.getPositionsByCategoryType(categoryType);
            } catch (IllegalArgumentException e) {
                // 잘못된 Enum 값이 제공된 경우 모든 포지션 반환
                positions = jobPositionService.getAllPositions();
            }
        } else {
            // 모든 포지션 조회
            positions = jobPositionService.getAllPositions();
        }

        return ResponseEntity.ok(ApiResponse.success(positions));
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "카테고리별 직무 포지션 조회", description = "특정 카테고리에 속한 직무 포지션 정보를 조회합니다.")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<JobPositionDto>>> getPositionsByCategory(
            @PathVariable("categoryId") Long categoryId) {
        List<JobPositionDto> positions = jobPositionService.getPositionsByCategory(categoryId);
        return ResponseEntity.ok(ApiResponse.success(positions));
    }

    @GetMapping("/type/{categoryType}")
    @Operation(summary = "직군별 직무 포지션 조회", description = "특정 직군(TECHNICAL, DESIGN, MARKETING, BUSINESS 등)에 속한 직무 포지션 정보를 조회합니다.")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<JobPositionDto>>> getPositionsByCategoryType(
            @PathVariable("categoryType") InterviewType categoryType) {
        List<JobPositionDto> positions = jobPositionService.getPositionsByCategoryType(categoryType);
        return ResponseEntity.ok(ApiResponse.success(positions));
    }
}