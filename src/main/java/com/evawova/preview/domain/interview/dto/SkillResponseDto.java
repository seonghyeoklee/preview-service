package com.evawova.preview.domain.interview.dto;

import com.evawova.preview.domain.interview.entity.Skill;
import com.evawova.preview.domain.interview.model.JobRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 스킬 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillResponseDto {

    private Long id;
    private String name;
    private String nameEn;
    private String icon;
    private JobRole primaryJobRole;
    private Boolean isPopular;

    /**
     * Skill 엔티티로부터 DTO를 생성합니다.
     */
    public static SkillResponseDto from(Skill skill) {
        return SkillResponseDto.builder()
                .id(skill.getId())
                .name(skill.getName())
                .nameEn(skill.getNameEn())
                .icon(skill.getIcon())
                .primaryJobRole(skill.getPrimaryJobRole())
                .isPopular(skill.getIsPopular())
                .build();
    }
}