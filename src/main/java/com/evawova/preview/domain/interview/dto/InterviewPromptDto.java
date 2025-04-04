package com.evawova.preview.domain.interview.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.evawova.preview.domain.interview.entity.InterviewPrompt;
import com.evawova.preview.domain.interview.model.PromptCategory;

@Getter
@Builder
public class InterviewPromptDto {
    private Long id;
    private String name;
    private PromptCategory category;
    private String content;
    private boolean active;
    private Long parentId;
    private String parentName;
    private Integer level;
    private List<InterviewPromptDto> children;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static InterviewPromptDto from(InterviewPrompt prompt) {
        return InterviewPromptDto.builder()
                .id(prompt.getId())
                .name(prompt.getName())
                .category(prompt.getCategory())
                .content(prompt.getContent())
                .active(prompt.isActive())
                .parentId(prompt.getParent() != null ? prompt.getParent().getId() : null)
                .parentName(prompt.getParent() != null ? prompt.getParent().getName() : null)
                .level(prompt.getLevel())
                .children(prompt.getChildren() != null && !prompt.getChildren().isEmpty()
                        ? prompt.getChildren().stream()
                                .map(InterviewPromptDto::from)
                                .collect(Collectors.toList())
                        : null)
                .createdAt(prompt.getCreatedAt())
                .updatedAt(prompt.getUpdatedAt())
                .build();
    }

    /**
     * 자식 프롬프트 정보 제외한 간단한 DTO 변환
     */
    public static InterviewPromptDto fromWithoutChildren(InterviewPrompt prompt) {
        return InterviewPromptDto.builder()
                .id(prompt.getId())
                .name(prompt.getName())
                .category(prompt.getCategory())
                .content(prompt.getContent())
                .active(prompt.isActive())
                .parentId(prompt.getParent() != null ? prompt.getParent().getId() : null)
                .parentName(prompt.getParent() != null ? prompt.getParent().getName() : null)
                .level(prompt.getLevel())
                .createdAt(prompt.getCreatedAt())
                .updatedAt(prompt.getUpdatedAt())
                .build();
    }
}