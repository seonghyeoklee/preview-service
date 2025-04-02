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
    @Column(nullable = false, unique = true)
    @Comment("���� ������ ���� �ĺ��� (Frontend ��)")
    private String positionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Comment("���� ���� Enum")
    private JobRole role;

    @Column(nullable = false)
    @Comment("���� ���� (�ѱ�)")
    private String title;

    @Column(nullable = false)
    @Comment("���� ���� (����)")
    private String titleEn;

    @Column(columnDefinition = "TEXT")
    @Comment("���� ���� (�ѱ�)")
    private String description;

    @Column(columnDefinition = "TEXT")
    @Comment("���� ���� (����)")
    private String descriptionEn;

    @Column(nullable = false)
    @Comment("������ �̸� (FontAwesome)")
    private String icon;

    @CreatedDate
    @Column(nullable = false)
    @Comment("���� �ð�")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    @Comment("���� �ð�")
    private LocalDateTime updatedAt;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "job_position_skills", joinColumns = @JoinColumn(name = "position_id"), inverseJoinColumns = @JoinColumn(name = "skill_id"))
    @Comment("���� ���� ��� ����")
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