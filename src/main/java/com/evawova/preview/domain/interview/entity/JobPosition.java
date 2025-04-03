package com.evawova.preview.domain.interview.entity;

import com.evawova.preview.domain.interview.model.JobRole;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "job_positions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString(exclude = { "skills", "category" })
public class JobPosition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Comment("고유 식별자 ID")
    private Long id;

    @Column(nullable = false, unique = true)
    @Comment("직무 포지션 고유 식별자 (Frontend 등)")
    private String positionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Comment("직무 Enum")
    private JobRole role;

    @Column(nullable = false)
    @Comment("직무 제목 (한글)")
    private String title;

    @Column(nullable = false)
    @Comment("직무 제목 (영문)")
    private String titleEn;

    @Column(columnDefinition = "TEXT")
    @Comment("직무 설명 (한글)")
    private String description;

    @Column(columnDefinition = "TEXT")
    @Comment("직무 설명 (영문)")
    private String descriptionEn;

    @Column(nullable = false)
    @Comment("직무 아이콘 (FontAwesome)")
    private String icon;

    @CreatedDate
    @Column(nullable = false)
    @Comment("등록 일시")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    @Comment("수정 일시")
    private LocalDateTime updatedAt;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "job_position_skills", joinColumns = @JoinColumn(name = "job_position_id"), inverseJoinColumns = @JoinColumn(name = "skill_id"))
    @Comment("직무 관련 기술")
    private Set<Skill> skills = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private InterviewCategory category;

    @Builder
    public JobPosition(String positionId, JobRole role, String title, String titleEn, String description,
            String descriptionEn, String icon, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.positionId = positionId;
        this.role = role;
        this.title = title;
        this.titleEn = titleEn;
        this.description = description;
        this.descriptionEn = descriptionEn;
        this.icon = icon;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void setCategory(InterviewCategory category) {
        this.category = category;
    }
}