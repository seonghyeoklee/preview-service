package com.evawova.preview.domain.interview.dto;

import com.evawova.preview.domain.interview.entity.JobPositionSkill;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class JobPositionSkillDto {
    private Long id;
    private Long jobPositionId;
    private String jobPositionName;
    private String jobPositionCode;
    private Long skillId;
    private String skillName;
    private String skillCode;
    private Integer importance;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * JobPositionSkill 엔티티를 DTO로 변환
     */
    public static JobPositionSkillDto fromEntity(JobPositionSkill jobPositionSkill) {
        return JobPositionSkillDto.builder()
                .id(jobPositionSkill.getId())
                .jobPositionId(jobPositionSkill.getJobPosition().getId())
                .jobPositionName(jobPositionSkill.getJobPosition().getName())
                .jobPositionCode(jobPositionSkill.getJobPosition().getCode())
                .skillId(jobPositionSkill.getSkill().getId())
                .skillName(jobPositionSkill.getSkill().getName())
                .skillCode(jobPositionSkill.getSkill().getCode())
                .importance(jobPositionSkill.getImportance())
                .createdAt(jobPositionSkill.getCreatedAt())
                .updatedAt(jobPositionSkill.getUpdatedAt())
                .build();
    }
}