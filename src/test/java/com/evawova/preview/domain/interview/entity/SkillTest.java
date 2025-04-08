package com.evawova.preview.domain.interview.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class SkillTest {

    @Test
    @DisplayName("스킬 엔티티 생성 테스트")
    void createSkill() {
        // given
        LocalDateTime now = LocalDateTime.now();
        String code = "java";
        String name = "Java";
        String nameEn = "Java";
        String description = "객체지향 프로그래밍 언어";
        String descriptionEn = "Object-oriented programming language";
        String iconUrl = "https://example.com/java.png";
        String primaryFieldType = JobField.DEVELOPMENT;
        boolean active = true;
        int sortOrder = 1;
        boolean isPopular = true;

        // when
        Skill skill = Skill.builder()
                .code(code)
                .name(name)
                .nameEn(nameEn)
                .description(description)
                .descriptionEn(descriptionEn)
                .iconUrl(iconUrl)
                .primaryFieldType(primaryFieldType)
                .active(active)
                .sortOrder(sortOrder)
                .isPopular(isPopular)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // then
        assertThat(skill.getCode()).isEqualTo(code);
        assertThat(skill.getName()).isEqualTo(name);
        assertThat(skill.getNameEn()).isEqualTo(nameEn);
        assertThat(skill.getDescription()).isEqualTo(description);
        assertThat(skill.getDescriptionEn()).isEqualTo(descriptionEn);
        assertThat(skill.getIconUrl()).isEqualTo(iconUrl);
        assertThat(skill.getPrimaryFieldType()).isEqualTo(primaryFieldType);
        assertThat(skill.getActive()).isEqualTo(active);
        assertThat(skill.getSortOrder()).isEqualTo(sortOrder);
        assertThat(skill.getIsPopular()).isEqualTo(isPopular);
        assertThat(skill.getCreatedAt()).isEqualTo(now);
        assertThat(skill.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("스킬 엔티티 기본값 테스트")
    void skillDefaultValues() {
        // given
        Skill skill = Skill.builder()
                .code("java")
                .name("Java")
                .primaryFieldType(JobField.DEVELOPMENT)
                .build();

        // then
        assertThat(skill.getActive()).isTrue();
        assertThat(skill.getSortOrder()).isZero();
        assertThat(skill.getIsPopular()).isFalse();
    }

    @Test
    @DisplayName("스킬 엔티티 연관관계 테스트")
    void skillRelationships() {
        // given
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

        JobField jobField = JobField.builder()
                .code(JobField.DEVELOPMENT)
                .name("개발")
                .build();

        JobPosition jobPosition = JobPosition.builder()
                .code(JobPosition.BACKEND_DEVELOPER)
                .name("백엔드 개발자")
                .icon("Icons.code")
                .jobField(jobField)
                .build();

        // when
        jobPosition.addSkill(javaSkill);
        jobPosition.addSkill(springSkill, 5);

        // then
        assertThat(javaSkill.getJobPositionSkills()).hasSize(1);
        assertThat(springSkill.getJobPositionSkills()).hasSize(1);
        assertThat(javaSkill.getJobPositionSkills().get(0).getJobPosition()).isEqualTo(jobPosition);
        assertThat(springSkill.getJobPositionSkills().get(0).getJobPosition()).isEqualTo(jobPosition);
        assertThat(springSkill.getJobPositionSkills().get(0).getImportance()).isEqualTo(5);
    }
}