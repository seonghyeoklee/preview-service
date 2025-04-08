package com.evawova.preview.domain.interview.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

/**
 * 면접관 엔티티
 */
@Entity
@Table(name = "interviewers")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Interviewer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("면접관 고유 ID")
    private Long id;

    @Column(nullable = false, unique = true)
    @Comment("면접관 코드 (예: friendly, strict)")
    private String code;

    @Column(nullable = false)
    @Comment("면접관 이름 (한글)")
    private String name;

    @Column
    @Comment("면접관 이름 (영문)")
    private String nameEn;

    @Column
    @Comment("면접관 설명 (한글)")
    private String description;

    @Column
    @Comment("면접관 설명 (영문)")
    private String descriptionEn;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Comment("면접관 성향")
    private InterviewerPersonality personality;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Comment("질문 스타일")
    private QuestionStyle questionStyle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Comment("피드백 스타일")
    private FeedbackStyle feedbackStyle;

    @Column
    @Comment("면접관 프로필 이미지 URL")
    private String profileImageUrl;

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
     * 면접관 성향
     */
    public enum InterviewerPersonality {
        FRIENDLY("친근한"),
        STRICT("엄격한"),
        CASUAL("편안한"),
        FORMAL("격식있는"),
        TECHNICAL("기술 중심적"),
        CONVERSATIONAL("대화 중심적"),
        BALANCED("균형잡힌"),
        PRAGMATIC("실용적인"),
        CREATIVE("창의적인");

        private final String displayName;

        InterviewerPersonality(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * 질문 스타일
     */
    public enum QuestionStyle {
        OPEN_ENDED("개방형"),
        DIRECT("직접적"),
        SITUATIONAL("상황 기반"),
        BEHAVIORAL("행동 기반"),
        TECHNICAL("기술적"),
        PROBLEM_SOLVING("문제 해결 중심"),
        STANDARD("일반적"),
        MIXED("복합적"),
        HYPOTHETICAL("가상 상황 기반");

        private final String displayName;

        QuestionStyle(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * 피드백 스타일
     */
    public enum FeedbackStyle {
        CONSTRUCTIVE("건설적"),
        CRITICAL("비판적"),
        ENCOURAGING("격려하는"),
        DETAILED("상세한"),
        CONCISE("간결한"),
        BALANCED("균형잡힌"),
        PRACTICAL("실용적"),
        INSIGHTFUL("통찰력 있는");

        private final String displayName;

        FeedbackStyle(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * 기존 enum 값을 위한 정적 상수
     */
    public static final String FRIENDLY = "friendly";
    public static final String STRICT = "strict";
    public static final String TECHNICAL = "technical";
    public static final String BALANCED = "balanced";
    public static final String MENTOR = "mentor";
}