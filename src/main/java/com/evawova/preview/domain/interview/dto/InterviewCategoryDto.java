package com.evawova.preview.domain.interview.dto;

import com.evawova.preview.domain.interview.entity.InterviewCategory;
import com.evawova.preview.domain.interview.model.InterviewType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class InterviewCategoryDto {

    private Long id;
    private String icon;
    private String title;
    private String description;
    private InterviewType type;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static InterviewCategoryDto from(InterviewCategory entity) {
        return InterviewCategoryDto.builder()
                .id(entity.getId())
                .icon(entity.getIcon())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .type(entity.getType())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}