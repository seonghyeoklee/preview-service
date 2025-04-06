package com.evawova.preview.domain.interview.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 직군 엔티티 - 개발, 디자인, 마케팅 등의 큰 분류
 */
@Entity
@Table(name = "job_fields")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class JobField {

    // 직군 코드 상수
    public static final String DEVELOPMENT = "development";
    public static final String DESIGN = "design";
    public static final String MARKETING = "marketing";
    public static final String BUSINESS = "business";
    public static final String SALES = "sales";
    public static final String CUSTOMER_SERVICE = "customer_service";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("직군 고유 ID")
    private Long id;

    @Column(nullable = false, unique = true)
    @Comment("직군 코드 (예: development, design)")
    private String code;

    @Column(nullable = false)
    @Comment("직군 이름 (한글)")
    private String name;

    @Column
    @Comment("직군 이름 (영문)")
    private String nameEn;

    @Column
    @Comment("직군 설명 (한글)")
    private String description;

    @Column
    @Comment("직군 설명 (영문)")
    private String descriptionEn;

    @Column(nullable = false)
    @Comment("직군 아이콘")
    private String icon;

    @OneToMany(mappedBy = "jobField", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @Comment("직군에 속한 직무 목록")
    private List<JobPosition> positions = new ArrayList<>();

    @Column(nullable = false)
    @Comment("활성 여부")
    private Boolean active = true;

    @Column(nullable = false)
    @Comment("정렬 순서")
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
     * 직무 추가
     */
    public void addPosition(JobPosition position) {
        this.positions.add(position);
        position.setJobField(this);
    }
}