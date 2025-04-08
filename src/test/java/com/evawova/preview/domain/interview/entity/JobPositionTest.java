package com.evawova.preview.domain.interview.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class JobPositionTest {

    @Test
    @DisplayName("직무 엔티티 생성 테스트")
    void createJobPosition() {
        // given
        LocalDateTime now = LocalDateTime.now();
        String code = JobPosition.BACKEND_DEVELOPER;
        String name = "백엔드 개발자";
        String nameEn = "Backend Developer";
        String description = "서버 사이드 개발을 담당하는 직무";
        String descriptionEn = "Responsible for server-side development";
        String icon = "Icons.code";
        boolean active = true;
        int sortOrder = 1;

        JobField jobField = JobField.builder()
                .code(JobField.DEVELOPMENT)
                .name("개발")
                .build();

        // when
        JobPosition jobPosition = JobPosition.builder()
                .code(code)
                .name(name)
                .nameEn(nameEn)
                .description(description)
                .descriptionEn(descriptionEn)
                .icon(icon)
                .jobField(jobField)
                .active(active)
                .sortOrder(sortOrder)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // then
        assertThat(jobPosition.getCode()).isEqualTo(code);
        assertThat(jobPosition.getName()).isEqualTo(name);
        assertThat(jobPosition.getNameEn()).isEqualTo(nameEn);
        assertThat(jobPosition.getDescription()).isEqualTo(description);
        assertThat(jobPosition.getDescriptionEn()).isEqualTo(descriptionEn);
        assertThat(jobPosition.getIcon()).isEqualTo(icon);
        assertThat(jobPosition.getJobField()).isEqualTo(jobField);
        assertThat(jobPosition.getActive()).isEqualTo(active);
        assertThat(jobPosition.getSortOrder()).isEqualTo(sortOrder);
        assertThat(jobPosition.getCreatedAt()).isEqualTo(now);
        assertThat(jobPosition.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("직무 엔티티 기본값 테스트")
    void jobPositionDefaultValues() {
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

        // then
        assertThat(jobPosition.getActive()).isTrue();
        assertThat(jobPosition.getSortOrder()).isZero();
    }

    @Test
    @DisplayName("직무 엔티티 연관관계 테스트")
    void jobPositionRelationships() {
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

        Skill javaSkill = Skill.builder()
                .code("java")
                .name("Java")
                .primaryFieldType(JobField.DEVELOPMENT)
                .build();

        Skill springSkill = Skill.builder()
                .code("spring")
                .name("Spring")
                .primaryFieldType(JobField.DEVELOPMENT)
                .build();

        // when
        jobPosition.addSkill(javaSkill);
        jobPosition.addSkill(springSkill, 5);

        // then
        assertThat(jobPosition.getJobPositionSkills()).hasSize(2);
        assertThat(jobPosition.getJobPositionSkills().get(0).getSkill()).isEqualTo(javaSkill);
        assertThat(jobPosition.getJobPositionSkills().get(1).getSkill()).isEqualTo(springSkill);
        assertThat(jobPosition.getJobPositionSkills().get(1).getImportance()).isEqualTo(5);
    }
}