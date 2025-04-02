package com.evawova.preview.domain.interview.dto;

import com.evawova.preview.domain.interview.entity.JobPosition;
import com.evawova.preview.domain.interview.entity.Skill;
import com.evawova.preview.domain.interview.model.InterviewType;
import com.evawova.preview.domain.interview.model.JobRole;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class JobPositionDto {

    private String positionId;
    private JobRole role;
    private String title;
    private String titleEn;
    private String description;
    private String descriptionEn;
    private String icon;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ī�װ� ���� �߰�
    private Long categoryId;
    private String categoryTitle;
    private InterviewType categoryType;

    public static JobPositionDto fromEntity(JobPosition entity) {
        return JobPositionDto.builder()
                .positionId(entity.getPositionId())
                .role(entity.getRole())
                .title(entity.getTitle())
                .titleEn(entity.getTitleEn())
                .description(entity.getDescription())
                .descriptionEn(entity.getDescriptionEn())
                .icon(entity.getIcon())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .categoryId(entity.getCategory() != null ? entity.getCategory().getId() : null)
                .categoryTitle(entity.getCategory() != null ? entity.getCategory().getTitle() : null)
                .categoryType(entity.getCategory() != null ? entity.getCategory().getType() : null)
                .build();
    }
}