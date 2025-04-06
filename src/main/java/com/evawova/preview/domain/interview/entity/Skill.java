package com.evawova.preview.domain.interview.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 스킬 엔티티 - Java, Python, React, Figma 등의 기술 스킬
 */
@Entity
@Table(name = "skills")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Skill {

    // 스킬 코드 상수
    public static final String JAVA = "java";
    public static final String PYTHON = "python";
    public static final String SPRING = "spring";
    public static final String DJANGO = "django";
    public static final String JAVASCRIPT = "javascript";
    public static final String REACT = "react";
    public static final String ANGULAR = "angular";
    public static final String VUE = "vue";
    public static final String FIGMA = "figma";
    public static final String SKETCH = "sketch";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("스킬 고유 ID")
    private Long id;

    @Column(nullable = false, unique = true)
    @Comment("스킬 코드 (예: java, python)")
    private String code;

    @Column(nullable = false)
    @Comment("스킬 이름 (한글)")
    private String name;

    @Column(nullable = false)
    @Comment("스킬 이름 (영문)")
    private String nameEn;

    @Column
    @Comment("스킬 설명 (한글)")
    private String description;

    @Column
    @Comment("스킬 설명 (영문)")
    private String descriptionEn;

    @Column
    @Comment("스킬 로고/아이콘 URL")
    private String iconUrl;

    @Column(nullable = false)
    @Comment("인기 스킬 여부")
    @Builder.Default
    private Boolean isPopular = false;

    @OneToMany(mappedBy = "skill", cascade = CascadeType.ALL)
    @Builder.Default
    @Comment("이 스킬이 연결된 직무 관계")
    private List<JobPositionSkill> jobPositionSkills = new ArrayList<>();

    @Column(nullable = false)
    @Comment("스킬의 주요 직군 유형")
    private String primaryFieldType;

    @Column(nullable = false)
    @Comment("활성 여부")
    @Builder.Default
    private Boolean active = true;

    @Column(nullable = false)
    @Comment("정렬 순서")
    @Builder.Default
    private Integer sortOrder = 0;

    @Column(nullable = false)
    @Comment("생성 시간")
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @Comment("수정 시간")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * 직무 연결 추가
     */
    public void addJobPosition(JobPosition jobPosition) {
        JobPositionSkill jobPositionSkill = new JobPositionSkill(jobPosition, this);
        this.jobPositionSkills.add(jobPositionSkill);
        jobPosition.getJobPositionSkills().add(jobPositionSkill);
    }

    /**
     * 직무 연결 제거
     */
    public void removeJobPosition(JobPosition jobPosition) {
        jobPositionSkills.removeIf(link -> link.getJobPosition().equals(jobPosition));
    }
}