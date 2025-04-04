package com.evawova.preview.domain.interview.dto;

import com.evawova.preview.domain.interview.entity.InterviewPrompt;
import com.evawova.preview.domain.interview.model.PromptCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@Schema(description = "면접 프롬프트 응답")
public class PromptResponse {

    @Schema(description = "프롬프트 고유 식별자")
    private Long id;

    @Schema(description = "프롬프트 이름")
    private String name;

    @Schema(description = "프롬프트 카테고리")
    private PromptCategory category;

    @Schema(description = "카테고리 표시 이름")
    private String categoryName;

    @Schema(description = "프롬프트 내용")
    private String content;

    @Schema(description = "프롬프트 활성화 여부")
    private boolean active;

    @Schema(description = "상위 프롬프트 ID")
    private Long parentId;

    @Schema(description = "상위 프롬프트 이름")
    private String parentName;

    @Schema(description = "프롬프트 계층 (1: 대분류, 2: 중분류, 3: 소분류)")
    private Integer level;

    @Schema(description = "하위 프롬프트 목록")
    private List<PromptResponse> children;

    @Schema(description = "생성일시")
    private LocalDateTime createdAt;

    @Schema(description = "수정일시")
    private LocalDateTime updatedAt;

    /**
     * InterviewPrompt 엔티티를 PromptResponse DTO로 변환 (자식 프롬프트 포함)
     */
    public static PromptResponse from(InterviewPrompt prompt) {
        return PromptResponse.builder()
                .id(prompt.getId())
                .name(prompt.getName())
                .category(prompt.getCategory())
                .categoryName(prompt.getCategory().getDisplayName())
                .content(prompt.getContent())
                .active(prompt.isActive())
                .parentId(prompt.getParent() != null ? prompt.getParent().getId() : null)
                .parentName(prompt.getParent() != null ? prompt.getParent().getName() : null)
                .level(prompt.getLevel())
                .children(prompt.getChildren() != null && !prompt.getChildren().isEmpty()
                        ? prompt.getChildren().stream()
                                .map(PromptResponse::from)
                                .collect(Collectors.toList())
                        : null)
                .createdAt(prompt.getCreatedAt())
                .updatedAt(prompt.getUpdatedAt())
                .build();
    }

    /**
     * InterviewPrompt 엔티티를 PromptResponse DTO로 변환 (자식 프롬프트 제외)
     */
    public static PromptResponse fromWithoutChildren(InterviewPrompt prompt) {
        return PromptResponse.builder()
                .id(prompt.getId())
                .name(prompt.getName())
                .category(prompt.getCategory())
                .categoryName(prompt.getCategory().getDisplayName())
                .content(prompt.getContent())
                .active(prompt.isActive())
                .parentId(prompt.getParent() != null ? prompt.getParent().getId() : null)
                .parentName(prompt.getParent() != null ? prompt.getParent().getName() : null)
                .level(prompt.getLevel())
                .createdAt(prompt.getCreatedAt())
                .updatedAt(prompt.getUpdatedAt())
                .build();
    }

    /**
     * InterviewPromptDto를 PromptResponse DTO로 변환
     */
    public static PromptResponse fromDto(InterviewPromptDto dto) {
        return PromptResponse.builder()
                .id(dto.getId())
                .name(dto.getName())
                .category(dto.getCategory())
                .categoryName(dto.getCategory() != null ? dto.getCategory().getDisplayName() : null)
                .content(dto.getContent())
                .active(dto.isActive())
                .parentId(dto.getParentId())
                .parentName(dto.getParentName())
                .level(dto.getLevel())
                .children(dto.getChildren() != null
                        ? dto.getChildren().stream()
                                .map(PromptResponse::fromDto)
                                .collect(Collectors.toList())
                        : null)
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .build();
    }
}