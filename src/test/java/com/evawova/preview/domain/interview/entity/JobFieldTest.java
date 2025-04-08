package com.evawova.preview.domain.interview.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class JobFieldTest {

    @Test
    @DisplayName("직군 엔티티 생성 테스트")
    void createJobField() {
        // given
        LocalDateTime now = LocalDateTime.now();
        String code = JobField.DEVELOPMENT;
        String name = "개발";
        String nameEn = "Development";
        String description = "소프트웨어 개발 관련 직군";
        String descriptionEn = "Software development related fields";
        String icon = "Icons.developer_mode";
        boolean active = true;
        int sortOrder = 1;

        // when
        JobField jobField = JobField.builder()
                .code(code)
                .name(name)
                .nameEn(nameEn)
                .description(description)
                .descriptionEn(descriptionEn)
                .icon(icon)
                .active(active)
                .sortOrder(sortOrder)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // then
        assertThat(jobField.getCode()).isEqualTo(code);
        assertThat(jobField.getName()).isEqualTo(name);
        assertThat(jobField.getNameEn()).isEqualTo(nameEn);
        assertThat(jobField.getDescription()).isEqualTo(description);
        assertThat(jobField.getDescriptionEn()).isEqualTo(descriptionEn);
        assertThat(jobField.getIcon()).isEqualTo(icon);
        assertThat(jobField.getActive()).isEqualTo(active);
        assertThat(jobField.getSortOrder()).isEqualTo(sortOrder);
        assertThat(jobField.getCreatedAt()).isEqualTo(now);
        assertThat(jobField.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("직군 엔티티 기본값 테스트")
    void jobFieldDefaultValues() {
        // given
        JobField jobField = JobField.builder()
                .code(JobField.DEVELOPMENT)
                .name("개발")
                .build();

        // then
        assertThat(jobField.getActive()).isTrue();
        assertThat(jobField.getSortOrder()).isZero();
    }

    @Test
    @DisplayName("직군 코드 상수 테스트")
    void jobFieldConstants() {
        // then
        assertThat(JobField.DEVELOPMENT).isEqualTo("development");
        assertThat(JobField.DESIGN).isEqualTo("design");
        assertThat(JobField.MARKETING).isEqualTo("marketing");
        assertThat(JobField.BUSINESS).isEqualTo("business");
        assertThat(JobField.SALES).isEqualTo("sales");
        assertThat(JobField.CUSTOMER_SERVICE).isEqualTo("customer_service");
    }
}