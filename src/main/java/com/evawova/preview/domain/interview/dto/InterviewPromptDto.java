package com.evawova.preview.domain.interview.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

import com.evawova.preview.domain.interview.entity.InterviewPrompt;

@Getter
@Builder
public class InterviewPromptDto {
    private Long id;
    private String name;
    private String category;
    private String content;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static InterviewPromptDto from(InterviewPrompt prompt) {
        return InterviewPromptDto.builder()
                .id(prompt.getId())
                .name(prompt.getName())
                .category(prompt.getCategory().name())
                .content(prompt.getContent())
                .active(prompt.isActive())
                .createdAt(prompt.getCreatedAt())
                .updatedAt(prompt.getUpdatedAt())
                .build();
    }
} 