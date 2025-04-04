package com.evawova.preview.domain.interview.controller;

import com.evawova.preview.common.response.ApiResponse;
import com.evawova.preview.domain.interview.dto.PromptRequest;
import com.evawova.preview.domain.interview.dto.PromptResponse;
import com.evawova.preview.domain.interview.model.PromptCategory;
import com.evawova.preview.domain.interview.service.InterviewPromptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/interview/prompts")
@Tag(name = "Interview Prompts", description = "면접 프롬프트 관련 API")
public class InterviewPromptController {

    private final InterviewPromptService promptService;

    public InterviewPromptController(InterviewPromptService promptService) {
        this.promptService = promptService;
    }

    @GetMapping
    @Operation(summary = "모든 프롬프트 조회", description = "활성화된 모든 프롬프트를 조회합니다.")
    public ApiResponse<List<PromptResponse>> getAllPrompts() {
        return ApiResponse.success(promptService.getAllPrompts());
    }

    @GetMapping("/main")
    @Operation(summary = "대분류 프롬프트 조회", description = "최상위 프롬프트(대분류)를 조회합니다.")
    public ApiResponse<List<PromptResponse>> getMainPrompts() {
        return ApiResponse.success(promptService.getMainPrompts());
    }

    @GetMapping("/sub/{parentId}")
    @Operation(summary = "하위 프롬프트 조회", description = "특정 프롬프트의 하위 프롬프트를 조회합니다.")
    public ApiResponse<List<PromptResponse>> getSubPrompts(@PathVariable Long parentId) {
        return ApiResponse.success(promptService.getSubPrompts(parentId));
    }

    @GetMapping("/{id}/children")
    @Operation(summary = "프롬프트와 하위 프롬프트 조회", description = "특정 프롬프트와 그 하위 프롬프트를 함께 조회합니다.")
    public ApiResponse<PromptResponse> getPromptWithChildren(@PathVariable Long id) {
        return ApiResponse.success(promptService.getPromptWithChildren(id));
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "카테고리별 프롬프트 조회", description = "특정 카테고리의 프롬프트를 조회합니다.")
    public ApiResponse<List<PromptResponse>> getPromptsByCategory(@PathVariable PromptCategory category) {
        return ApiResponse.success(promptService.getPromptsByCategory(category));
    }

    @GetMapping("/metadata")
    @Operation(summary = "프롬프트 메타데이터 조회", description = "면접 설정에 필요한 메타데이터를 조회합니다.")
    public ApiResponse<Map<String, Object>> getMetadata() {
        return ApiResponse.success(promptService.getMetadata());
    }

    @PostMapping
    @Operation(summary = "프롬프트 생성", description = "새로운 프롬프트를 생성합니다.")
    public ApiResponse<PromptResponse> createPrompt(@RequestBody PromptRequest request) {
        return ApiResponse.success(promptService.createPrompt(request.toDto()));
    }

    @PostMapping("/{parentId}/children")
    @Operation(summary = "하위 프롬프트 추가", description = "특정 프롬프트에 하위 프롬프트를 추가합니다.")
    public ApiResponse<PromptResponse> addChildPrompt(
            @PathVariable Long parentId,
            @RequestBody PromptRequest childRequest) {
        return ApiResponse.success(promptService.addChildPrompt(parentId, childRequest.toDto()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "프롬프트 수정", description = "특정 프롬프트를 수정합니다.")
    public ApiResponse<PromptResponse> updatePrompt(
            @PathVariable Long id,
            @RequestBody PromptRequest request) {
        return ApiResponse.success(promptService.updatePrompt(id, request.toDto()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "프롬프트 비활성화", description = "특정 프롬프트를 비활성화합니다.")
    public ApiResponse<Void> deactivatePrompt(@PathVariable Long id) {
        promptService.deactivatePrompt(id);
        return ApiResponse.success(null, "프롬프트가 성공적으로 비활성화되었습니다.");
    }
}