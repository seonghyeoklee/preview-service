package com.evawova.preview.domain.interview.dto;

import com.evawova.preview.domain.interview.entity.InterviewCategory;
import com.evawova.preview.domain.interview.entity.Skill;
import com.evawova.preview.domain.interview.model.InterviewType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class SubCategoryWithSkillsDto {
    private Long id;
    private String icon;
    private String title;
    private String titleEn;
    private String description;
    private String descriptionEn;
    private InterviewType type;
    private Long parentId;
    private String parentTitle;
    private List<SkillDto> skills;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static SubCategoryWithSkillsDto from(InterviewCategory entity) {
        return SubCategoryWithSkillsDto.builder()
                .id(entity.getId())
                .icon(entity.getIcon())
                .title(entity.getTitle())
                .titleEn(entity.getTitleEn())
                .description(entity.getDescription())
                .descriptionEn(entity.getDescriptionEn())
                .type(entity.getType())
                .parentId(entity.getParent() != null ? entity.getParent().getId() : null)
                .parentTitle(entity.getParent() != null ? entity.getParent().getTitle() : null)
                .skills(entity.getSkills() != null ? entity.getSkills().stream()
                        .map(SkillDto::from)
                        .collect(Collectors.toList()) : List.of())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    @Getter
    @Builder
    public static class SkillDto {
        private Long id;
        private String name;
        private String nameEn;

        public static SkillDto from(Skill skill) {
            return SkillDto.builder()
                    .id(skill.getId())
                    .name(skill.getName())
                    .nameEn(skill.getNameEn())
                    .build();
        }
    }
}