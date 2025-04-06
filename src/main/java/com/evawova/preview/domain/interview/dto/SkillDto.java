package com.evawova.preview.domain.interview.dto;

import com.evawova.preview.domain.interview.entity.Skill;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class SkillDto {
    private Long id;
    private String code;
    private String name;
    private String nameEn;
    private String description;
    private String descriptionEn;
    private String iconUrl;
    private Boolean isPopular;
    private String primaryFieldType;
    private Boolean active;
    private Integer sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 필요한 경우 직무 관계 목록 포함
    private List<JobPositionSkillDto> jobPositions;

    /**
     * Skill 엔티티를 DTO로 변환 (직무 관계 불포함)
     */
    public static SkillDto fromEntity(Skill skill) {
        return SkillDto.builder()
                .id(skill.getId())
                .code(skill.getCode())
                .name(skill.getName())
                .nameEn(skill.getNameEn())
                .description(skill.getDescription())
                .descriptionEn(skill.getDescriptionEn())
                .iconUrl(skill.getIconUrl())
                .isPopular(skill.getIsPopular())
                .primaryFieldType(skill.getPrimaryFieldType())
                .active(skill.getActive())
                .sortOrder(skill.getSortOrder())
                .createdAt(skill.getCreatedAt())
                .updatedAt(skill.getUpdatedAt())
                .build();
    }

    /**
     * Skill 엔티티를 DTO로 변환 (직무 관계 포함)
     */
    public static SkillDto fromEntityWithJobPositions(Skill skill) {
        SkillDto dto = fromEntity(skill);

        return SkillDto.builder()
                .id(dto.getId())
                .code(dto.getCode())
                .name(dto.getName())
                .nameEn(dto.getNameEn())
                .description(dto.getDescription())
                .descriptionEn(dto.getDescriptionEn())
                .iconUrl(dto.getIconUrl())
                .isPopular(dto.getIsPopular())
                .primaryFieldType(dto.getPrimaryFieldType())
                .active(dto.getActive())
                .sortOrder(dto.getSortOrder())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .jobPositions(skill.getJobPositionSkills().stream()
                        .map(JobPositionSkillDto::fromEntity)
                        .collect(Collectors.toList()))
                .build();
    }
}