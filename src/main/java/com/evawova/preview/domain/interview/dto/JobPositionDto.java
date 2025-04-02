package com.evawova.preview.domain.interview.dto;

import com.evawova.preview.domain.interview.entity.JobPosition;
import com.evawova.preview.domain.interview.model.JobRole;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class JobPositionDto {

    private Long id;
    private String positionId;
    private JobRole role;
    private String title;
    private String description;
    private String icon;
    private List<String> skills;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static JobPositionDto from(JobPosition entity) {
        return JobPositionDto.builder()
                .id(entity.getId())
                .positionId(entity.getPositionId())
                .role(entity.getRole())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .icon(entity.getIcon())
                .skills(entity.getSkills()) // Note: This might trigger lazy loading
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}