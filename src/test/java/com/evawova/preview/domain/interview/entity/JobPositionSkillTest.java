package com.evawova.preview.domain.interview.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class JobPositionSkillTest {

    @Test
    @DisplayName("직무-스킬 매핑 엔티티 생성 테스트")
    void createJobPositionSkill() {
        // given
        LocalDateTime now = LocalDateTime.now();
        int importance = 5;

        JobField jobField = JobField.builder()
                .code(JobField.DEVELOPMENT)
                .name("개발")
                .build();

        JobPosition jobPosition = JobPosition.builder()
                .code(JobPosition.BACKEND_DEVELOPER)
                .name("백엔드 개발자")
                .jobField(jobField)
                .build();

        Skill skill = Skill.builder()
                .code("java")
                .name("Java")
                .primaryFieldType(JobField.DEVELOPMENT)
                .build();

        // when
        JobPositionSkill jobPositionSkill = JobPositionSkill.builder()
                .jobPosition(jobPosition)
                .skill(skill)
                .importance(importance)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // then
        assertThat(jobPositionSkill.getJobPosition()).isEqualTo(jobPosition);
        assertThat(jobPositionSkill.getSkill()).isEqualTo(skill);
        assertThat(jobPositionSkill.getImportance()).isEqualTo(importance);
        assertThat(jobPositionSkill.getCreatedAt()).isEqualTo(now);
        assertThat(jobPositionSkill.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("직무-스킬 매핑 엔티티 기본값 테스트")
    void jobPositionSkillDefaultValues() {
        // given
        JobField jobField = JobField.builder()
                .code(JobField.DEVELOPMENT)
                .name("개발")
                .build();

        JobPosition jobPosition = JobPosition.builder()
                .code(JobPosition.BACKEND_DEVELOPER)
                .name("백엔드 개발자")
                .jobField(jobField)
                .build();

        Skill skill = Skill.builder()
                .code("java")
                .name("Java")
                .primaryFieldType(JobField.DEVELOPMENT)
                .build();

        // when
        JobPositionSkill jobPositionSkill = JobPositionSkill.builder()
                .jobPosition(jobPosition)
                .skill(skill)
                .build();

        // then
        assertThat(jobPositionSkill.getImportance()).isZero();
    }

    @Test
    @DisplayName("직무-스킬 매핑 엔티티 양방향 연관관계 테스트")
    void jobPositionSkillBidirectionalRelationship() {
        // given
        JobField jobField = JobField.builder()
                .code(JobField.DEVELOPMENT)
                .name("개발")
                .build();

        JobPosition jobPosition = JobPosition.builder()
                .code(JobPosition.BACKEND_DEVELOPER)
                .name("백엔드 개발자")
                .jobField(jobField)
                .build();

        Skill skill = Skill.builder()
                .code("java")
                .name("Java")
                .primaryFieldType(JobField.DEVELOPMENT)
                .build();

        // when
        JobPositionSkill jobPositionSkill = JobPositionSkill.builder()
                .jobPosition(jobPosition)
                .skill(skill)
                .importance(5)
                .build();

        // then
        assertThat(jobPosition.getJobPositionSkills()).contains(jobPositionSkill);
        assertThat(skill.getJobPositionSkills()).contains(jobPositionSkill);
    }
}