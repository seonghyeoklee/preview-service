package com.evawova.preview.domain.interview.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * 면접 엔티티 - 사용자가 선택한 직무와 스킬로 면접을 진행하는 정보
 */
@Entity
@Table(name = "interviews")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Interview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("면접 고유 ID")
    private Long id;

    @Column(nullable = false, unique = true)
    @Comment("면접 세션 고유 코드")
    private String sessionCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_field_id")
    @Comment("선택한 직군")
    private JobField jobField;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_position_id")
    @Comment("선택한 직무")
    private JobPosition jobPosition;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "interview_skills", joinColumns = @JoinColumn(name = "interview_id"), inverseJoinColumns = @JoinColumn(name = "skill_id"))
    @Builder.Default
    @Comment("선택한 스킬 목록")
    private Set<Skill> selectedSkills = new HashSet<>();

    @Column(nullable = false)
    @Comment("면접 난이도 (1: 쉬움, 2: 보통, 3: 어려움)")
    private Integer difficulty;

    @Column(nullable = false)
    @Comment("면접 진행 언어 (ko: 한국어, en: 영어)")
    private String language;

    @Column(nullable = false)
    @Comment("면접관 스타일 (friendly: 친절함, neutral: 중립적, challenging: 도전적)")
    private String interviewerStyle;

    @Column(nullable = false)
    @Comment("면접 진행 시간 (분 단위)")
    private Integer durationMinutes;

    @Column(nullable = false)
    @Comment("경력 수준 (entry, junior, mid_level, senior, executive)")
    private String experienceLevel;

    @Column(nullable = false)
    @Comment("시작 시간")
    private LocalDateTime startedAt;

    @Column
    @Comment("종료 시간")
    private LocalDateTime endedAt;

    @Column(nullable = false)
    @Comment("면접 완료 여부")
    private Boolean isCompleted = false;

    @Column
    @Comment("면접 점수 (0-100)")
    private Integer score;

    @Column
    @Comment("면접 피드백")
    private String feedback;

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
     * 면접 종료 처리
     */
    public void complete(Integer score, String feedback) {
        this.isCompleted = true;
        this.endedAt = LocalDateTime.now();
        this.score = score;
        this.feedback = feedback;
    }

    /**
     * 스킬 추가
     */
    public void addSkill(Skill skill) {
        this.selectedSkills.add(skill);
    }

    /**
     * 세션 코드 생성
     */
    public static String generateSessionCode() {
        return "INT-" + System.currentTimeMillis();
    }
}