package com.evawova.preview.domain.interview.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

/**
 * 경력 수준 엔티티
 */
@Entity
@Table(name = "experience_levels")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ExperienceLevel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("경력 수준 고유 ID")
    private Long id;

    @Column(nullable = false, unique = true)
    @Comment("경력 수준 고유 코드 (예: entry, junior)")
    private String code;

    @Column(nullable = false)
    @Comment("경력 수준 표시 이름 (한글)")
    private String displayName;

    @Column
    @Comment("경력 수준 표시 이름 (영문)")
    private String displayNameEn;

    @Column
    @Comment("경력 수준 설명 (한글)")
    private String description;

    @Column
    @Comment("경력 수준 설명 (영문)")
    private String descriptionEn;

    @Column(nullable = false)
    @Comment("최소 경력 년수")
    private Integer minYears;

    @Column
    @Comment("최대 경력 년수")
    private Integer maxYears;

    @Column(nullable = false)
    @Comment("활성 여부")
    private Boolean active;

    @Column(nullable = false)
    @Comment("정렬 순서")
    private Integer sortOrder;

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
     * 기존 enum 값을 위한 정적 상수
     */
    public static final String ENTRY = "entry";
    public static final String JUNIOR = "junior";
    public static final String MID_LEVEL = "mid_level";
    public static final String SENIOR = "senior";
    public static final String EXECUTIVE = "executive";
}