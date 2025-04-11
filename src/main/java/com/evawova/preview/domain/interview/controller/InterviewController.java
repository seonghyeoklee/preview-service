package com.evawova.preview.domain.interview.controller;

import com.evawova.preview.domain.interview.dto.InterviewStartRequest;
import com.evawova.preview.domain.interview.dto.InterviewStartResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/interviews")
@Tag(name = "Interview", description = "인터뷰 API")
@RequiredArgsConstructor
public class InterviewController {

    // private final InterviewService interviewService;

    // @PostMapping("/start")
    // @Operation(summary = "인터뷰 시작")
    // public ResponseEntity<InterviewStartResponse> startInterview(
    // @Parameter(description = "인터뷰 시작 요청") @Valid @RequestBody
    // InterviewStartRequest request) {
    // return ResponseEntity.ok(interviewService.startInterview(request));
    // }

    // @PostMapping("/{interviewId}/end")
    // @Operation(summary = "인터뷰 종료")
    // public ResponseEntity<Void> endInterview(
    // @Parameter(description = "인터뷰 ID") @PathVariable Long interviewId) {
    // interviewService.endInterview(interviewId);
    // return ResponseEntity.ok().build();
    // }

    // @PostMapping("/{interviewId}/cancel")
    // @Operation(summary = "인터뷰 취소")
    // public ResponseEntity<Void> cancelInterview(
    // @Parameter(description = "인터뷰 ID") @PathVariable Long interviewId) {
    // interviewService.cancelInterview(interviewId);
    // return ResponseEntity.ok().build();
    // }
}