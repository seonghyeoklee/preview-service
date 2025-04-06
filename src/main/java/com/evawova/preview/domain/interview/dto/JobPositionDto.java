package com.evawova.preview.domain.interview.dto;

import com.evawova.preview.domain.interview.entity.JobPosition;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class JobPositionDto {
    private Long id;
    private String code;
    private String name;
    private String nameEn;
    private String description;
    private String descriptionEn;
    private String icon;
    private Long jobFieldId;
    private String jobFieldName;
    private Boolean active;
    private Integer sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 필요한 경우 스킬 관계 목록 포함
    private List<JobPositionSkillDto> skills;

    /**
     * JobPosition 엔티티를 DTO로 변환 (스킬 관계 불포함)
     */
    public static JobPositionDto fromEntity(JobPosition jobPosition) {
        return JobPositionDto.builder()
                .id(jobPosition.getId())
                .code(jobPosition.getCode())
                .name(jobPosition.getName())
                .nameEn(jobPosition.getNameEn())
                .description(jobPosition.getDescription())
                .descriptionEn(jobPosition.getDescriptionEn())
                .icon(jobPosition.getIcon())
                .jobFieldId(jobPosition.getJobField() != null ? jobPosition.getJobField().getId() : null)
                .jobFieldName(jobPosition.getJobField() != null ? jobPosition.getJobField().getName() : null)
                .active(jobPosition.getActive())
                .sortOrder(jobPosition.getSortOrder())
                .createdAt(jobPosition.getCreatedAt())
                .updatedAt(jobPosition.getUpdatedAt())
                .build();
    }

    /**
     * JobPosition 엔티티를 DTO로 변환 (스킬 관계 포함)
     */
    public static JobPositionDto fromEntityWithSkills(JobPosition jobPosition) {
        JobPositionDto dto = fromEntity(jobPosition);

        return JobPositionDto.builder()
                .id(dto.getId())
                .code(dto.getCode())
                .name(dto.getName())
                .nameEn(dto.getNameEn())
                .description(dto.getDescription())
                .descriptionEn(dto.getDescriptionEn())
                .icon(dto.getIcon())
                .jobFieldId(dto.getJobFieldId())
                .jobFieldName(dto.getJobFieldName())
                .active(dto.getActive())
                .sortOrder(dto.getSortOrder())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .skills(jobPosition.getJobPositionSkills().stream()
                        .map(JobPositionSkillDto::fromEntity)
                        .collect(Collectors.toList()))
                .build();
    }
}