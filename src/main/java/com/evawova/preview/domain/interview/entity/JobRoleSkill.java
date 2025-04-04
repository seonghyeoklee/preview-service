package com.evawova.preview.domain.interview.entity;

import com.evawova.preview.domain.interview.model.JobRole;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

/**
 * 직무별 기술 스택 매핑
 */
@Entity
@Table(name = "job_role_skills")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class JobRoleSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("직무별 기술 ID")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Comment("직무 역할")
    private JobRole jobRole;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id", nullable = false)
    @Comment("기술 스택")
    private Skill skill;

    @Column(nullable = false)
    @Comment("중요도 (1: 필수, 2: 권장, 3: 선택)")
    private Integer importance;

    @Column(nullable = false)
    @Comment("생성 시간")
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @Comment("수정 시간")
    private LocalDateTime updatedAt;

    // Ensure timestamps are set during creation via builder
    public static class JobRoleSkillBuilder {
        private LocalDateTime createdAt = LocalDateTime.now();
        private LocalDateTime updatedAt = LocalDateTime.now();
    }
}