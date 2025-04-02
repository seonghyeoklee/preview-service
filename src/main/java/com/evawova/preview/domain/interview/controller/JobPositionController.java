package com.evawova.preview.domain.interview.controller;

import com.evawova.preview.common.response.ApiResponse;
import com.evawova.preview.domain.interview.dto.JobPositionDto;
import com.evawova.preview.domain.interview.service.JobPositionService;
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
@RequestMapping("/api/v1/interview/positions")
@Tag(name = "Job Position", description = "직무 포지션 API")
@RequiredArgsConstructor
public class JobPositionController {

    private final JobPositionService jobPositionService;

    @GetMapping
    @Operation(summary = "모든 직무 포지션 조회")
    @PreAuthorize("hasAnyRole('USER_FREE', 'USER_STANDARD', 'USER_PRO', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<JobPositionDto>>> getAllPositions() {
        List<JobPositionDto> positions = jobPositionService.getAllPositions();
        return ResponseEntity.ok(ApiResponse.success(positions));
    }
}