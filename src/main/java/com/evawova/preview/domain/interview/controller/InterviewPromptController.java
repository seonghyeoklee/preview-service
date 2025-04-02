package com.evawova.preview.domain.interview.controller;

import com.evawova.preview.domain.interview.dto.InterviewPromptDto;
import com.evawova.preview.domain.interview.service.InterviewPromptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Interview Prompt", description = "면접 프롬프트 관련 API")
@RestController
@RequestMapping("/api/v1/interview/prompts")
@RequiredArgsConstructor
public class InterviewPromptController {

    private final InterviewPromptService promptService;

    @Operation(summary = "면접 설정 메타데이터 조회", description = "면접 설정에 필요한 모든 메타데이터를 조회합니다.")
    @GetMapping("/metadata")
    public ResponseEntity<Map<String, Object>> getMetadata() {
        return ResponseEntity.ok(promptService.getMetadata());
    }

    @Operation(summary = "모든 프롬프트 조회", description = "시스템에 등록된 모든 프롬프트를 조회합니다.")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<InterviewPromptDto>> getAllPrompts() {
        return ResponseEntity.ok(promptService.getAllPrompts());
    }

    @Operation(summary = "카테고리별 프롬프트 조회", description = "특정 카테고리의 프롬프트를 조회합니다.")
    @GetMapping("/category/{category}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<InterviewPromptDto>> getPromptsByCategory(
            @Parameter(description = "프롬프트 카테고리") @PathVariable String category) {
        return ResponseEntity.ok(promptService.getPromptsByCategory(category));
    }

    @Operation(summary = "프롬프트 생성", description = "새로운 프롬프트를 생성합니다.")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<InterviewPromptDto> createPrompt(
            @Parameter(description = "생성할 프롬프트 정보") @RequestBody InterviewPromptDto promptDto) {
        return ResponseEntity.ok(promptService.createPrompt(promptDto));
    }

    @Operation(summary = "프롬프트 수정", description = "기존 프롬프트를 수정합니다.")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<InterviewPromptDto> updatePrompt(
            @Parameter(description = "프롬프트 ID") @PathVariable Long id,
            @Parameter(description = "수정할 프롬프트 정보") @RequestBody InterviewPromptDto promptDto) {
        return ResponseEntity.ok(promptService.updatePrompt(id, promptDto));
    }

    @Operation(summary = "프롬프트 비활성화", description = "프롬프트를 비활성화합니다.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> deactivatePrompt(
            @Parameter(description = "프롬프트 ID") @PathVariable Long id) {
        promptService.deactivatePrompt(id);
        return ResponseEntity.ok().build();
    }
} 