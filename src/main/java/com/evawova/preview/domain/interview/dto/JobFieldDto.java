package com.evawova.preview.domain.interview.dto;

import com.evawova.preview.domain.interview.entity.JobField;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class JobFieldDto {
    private Long id;
    private String code;
    private String name;
    private String nameEn;
    private String description;
    private String descriptionEn;
    private String icon;
    private Boolean active;
    private Integer sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 필요한 경우 하위 직무 목록 포함
    private List<JobPositionDto> positions;

    /**
     * JobField 엔티티를 DTO로 변환 (직무 목록 불포함)
     */
    public static JobFieldDto fromEntity(JobField jobField) {
        return JobFieldDto.builder()
                .id(jobField.getId())
                .code(jobField.getCode())
                .name(jobField.getName())
                .nameEn(jobField.getNameEn())
                .description(jobField.getDescription())
                .descriptionEn(jobField.getDescriptionEn())
                .icon(jobField.getIcon())
                .active(jobField.getActive())
                .sortOrder(jobField.getSortOrder())
                .createdAt(jobField.getCreatedAt())
                .updatedAt(jobField.getUpdatedAt())
                .build();
    }

    /**
     * JobField 엔티티를 DTO로 변환 (직무 목록 포함)
     */
    public static JobFieldDto fromEntityWithPositions(JobField jobField) {
        return JobFieldDto.builder()
                .id(jobField.getId())
                .code(jobField.getCode())
                .name(jobField.getName())
                .nameEn(jobField.getNameEn())
                .description(jobField.getDescription())
                .descriptionEn(jobField.getDescriptionEn())
                .icon(jobField.getIcon())
                .active(jobField.getActive())
                .sortOrder(jobField.getSortOrder())
                .createdAt(jobField.getCreatedAt())
                .updatedAt(jobField.getUpdatedAt())
                .positions(jobField.getPositions().stream()
                        .map(JobPositionDto::fromEntity)
                        .collect(Collectors.toList()))
                .build();
    }
}