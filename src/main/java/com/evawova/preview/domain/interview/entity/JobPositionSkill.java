package com.evawova.preview.domain.interview.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 직무와 스킬 간의 관계를 나타내는 엔티티
 */
@Entity
@Table(name = "job_position_skills")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class JobPositionSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("관계 ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_position_id", nullable = false)
    @Comment("직무")
    private JobPosition jobPosition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id", nullable = false)
    @Comment("스킬")
    private Skill skill;

    @Column(nullable = false)
    @Comment("중요도 (0: 낮음, 10: 높음)")
    private Integer importance = 5;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    @Comment("생성 시간")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    @Comment("수정 시간")
    private LocalDateTime updatedAt;

    /**
     * 직무와 스킬을 연결하는 생성자
     */
    public JobPositionSkill(JobPosition jobPosition, Skill skill) {
        this.jobPosition = jobPosition;
        this.skill = skill;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 직무와 스킬을 중요도와 함께 연결하는 생성자
     */
    public JobPositionSkill(JobPosition jobPosition, Skill skill, Integer importance) {
        this(jobPosition, skill);
        this.importance = importance;
    }

    /**
     * 중요도 설정
     */
    public void setImportance(Integer importance) {
        this.importance = importance;
        this.updatedAt = LocalDateTime.now();
    }
}