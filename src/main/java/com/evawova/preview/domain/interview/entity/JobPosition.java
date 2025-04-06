package com.evawova.preview.domain.interview.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 직무 엔티티 - 백엔드 개발자, 프론트엔드 개발자 등
 */
@Entity
@Table(name = "job_positions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class JobPosition {

    // 직무 코드 상수
    public static final String BACKEND_DEVELOPER = "backend_developer";
    public static final String FRONTEND_DEVELOPER = "frontend_developer";
    public static final String FULLSTACK_DEVELOPER = "fullstack_developer";
    public static final String MOBILE_DEVELOPER = "mobile_developer";
    public static final String UI_UX_DESIGNER = "ui_ux_designer";
    public static final String PRODUCT_DESIGNER = "product_designer";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Comment("직무 고유 ID")
    private Long id;

    @Column(nullable = false, unique = true)
    @Comment("직무 코드 (예: backend_developer)")
    private String code;

    @Column(nullable = false)
    @Comment("직무 이름 (한글)")
    private String name;

    @Column
    @Comment("직무 이름 (영문)")
    private String nameEn;

    @Column(columnDefinition = "TEXT")
    @Comment("직무 설명 (한글)")
    private String description;

    @Column(columnDefinition = "TEXT")
    @Comment("직무 설명 (영문)")
    private String descriptionEn;

    @Column(nullable = false)
    @Comment("직무 아이콘")
    private String icon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_field_id")
    @Comment("소속 직군")
    private JobField jobField;

    @OneToMany(mappedBy = "jobPosition", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @Comment("직무 관련 스킬 연결")
    private List<JobPositionSkill> jobPositionSkills = new ArrayList<>();

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
     * 직군 설정
     */
    public void setJobField(JobField jobField) {
        this.jobField = jobField;
    }

    /**
     * 스킬 추가
     */
    public void addSkill(Skill skill) {
        JobPositionSkill jobPositionSkill = new JobPositionSkill(this, skill);
        this.jobPositionSkills.add(jobPositionSkill);
    }

    /**
     * 스킬 추가 (중요도 설정)
     */
    public void addSkill(Skill skill, Integer importance) {
        JobPositionSkill jobPositionSkill = new JobPositionSkill(this, skill, importance);
        this.jobPositionSkills.add(jobPositionSkill);
    }

    /**
     * 스킬 제거
     */
    public void removeSkill(Skill skill) {
        jobPositionSkills.removeIf(link -> link.getSkill().equals(skill));
    }
}