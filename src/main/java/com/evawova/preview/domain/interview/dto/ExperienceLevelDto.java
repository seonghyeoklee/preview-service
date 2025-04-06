package com.evawova.preview.domain.interview.dto;

import com.evawova.preview.domain.interview.entity.ExperienceLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ExperienceLevelDto {
    private Long id;
    private String code;
    private String displayName;
    private String displayNameEn;
    private String description;
    private String descriptionEn;
    private Integer minYears;
    private Integer maxYears;
    private Boolean active;
    private Integer sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * ExperienceLevel 엔티티를 DTO로 변환
     */
    public static ExperienceLevelDto fromEntity(ExperienceLevel experienceLevel) {
        return ExperienceLevelDto.builder()
                .id(experienceLevel.getId())
                .code(experienceLevel.getCode())
                .displayName(experienceLevel.getDisplayName())
                .displayNameEn(experienceLevel.getDisplayNameEn())
                .description(experienceLevel.getDescription())
                .descriptionEn(experienceLevel.getDescriptionEn())
                .minYears(experienceLevel.getMinYears())
                .maxYears(experienceLevel.getMaxYears())
                .active(experienceLevel.getActive())
                .sortOrder(experienceLevel.getSortOrder())
                .createdAt(experienceLevel.getCreatedAt())
                .updatedAt(experienceLevel.getUpdatedAt())
                .build();
    }
}