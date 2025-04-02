package com.evawova.preview.domain.interview.entity;

import com.evawova.preview.domain.interview.dto.InterviewSettings;
import com.evawova.preview.domain.interview.model.ExperienceLevel;
import com.evawova.preview.domain.interview.model.InterviewDifficulty;
import com.evawova.preview.domain.interview.model.InterviewDuration;
import com.evawova.preview.domain.interview.model.InterviewLanguage;
import com.evawova.preview.domain.interview.model.InterviewMode;
import com.evawova.preview.domain.interview.model.InterviewType;
import com.evawova.preview.domain.interview.model.InterviewerStyle;
import com.evawova.preview.domain.interview.model.JobRole;
import com.evawova.preview.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 인터뷰 세션 정보를 관리하는 엔티티
 */
@Entity
@Table(name = "interview_sessions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class InterviewSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("인터뷰 세션의 고유 식별자")
    private Long id;

    @Column(nullable = false, unique = true)
    @Comment("인터뷰 세션의 UUID (외부 노출용)")
    private String sessionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @Comment("인터뷰를 진행하는 사용자")
    private User user;

    @Column(nullable = false)
    @Comment("인터뷰 시작 시간")
    private LocalDateTime startedAt;

    @Comment("인터뷰 종료 시간")
    private LocalDateTime endedAt;

    @Enumerated(EnumType.STRING)
    @Comment("인터뷰 유형 (기술, 디자인, 마케팅, 비즈니스)")
    private InterviewType type;

    @Enumerated(EnumType.STRING)
    @Comment("직무 역할 (프론트엔드, 백엔드, 풀스택 등)")
    private JobRole jobRole;

    @Enumerated(EnumType.STRING)
    @Comment("면접관 스타일 (친근한, 전문적인, 도전적인)")
    private InterviewerStyle interviewerStyle;

    @Enumerated(EnumType.STRING)
    @Comment("인터뷰 난이도 (초급, 중급, 고급)")
    private InterviewDifficulty difficulty;

    @Enumerated(EnumType.STRING)
    @Comment("인터뷰 진행 시간 (15분, 30분, 45분, 60분)")
    private InterviewDuration duration;

    @Enumerated(EnumType.STRING)
    @Comment("인터뷰 모드 (실시간, 비동기)")
    private InterviewMode interviewMode;

    @Enumerated(EnumType.STRING)
    @Comment("경력 수준 (신입, 주임, 대리, 과장, 차장, 부장)")
    private ExperienceLevel experienceLevel;

    @Enumerated(EnumType.STRING)
    @Comment("인터뷰 진행 언어 (한국어, 영어)")
    private InterviewLanguage language;

    @ElementCollection
    @CollectionTable(name = "interview_session_skills", joinColumns = @JoinColumn(name = "interview_session_id"))
    @Column(name = "skill")
    @Comment("기술 스택 목록")
    @Builder.Default
    private List<String> technicalSkills = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    @Comment("AI 면접관 프롬프트")
    private String prompt;

    /**
     * 인터뷰 세션 생성을 위한 팩토리 메서드
     */
    public static InterviewSession create(User user, InterviewSettings settings, String prompt) {
        return InterviewSession.builder()
                .sessionId(UUID.randomUUID().toString())
                .user(user)
                .startedAt(LocalDateTime.now())
                .type(settings.getType())
                .jobRole(settings.getJobRole())
                .interviewerStyle(settings.getInterviewerStyle())
                .difficulty(settings.getDifficulty())
                .duration(settings.getDuration())
                .interviewMode(settings.getInterviewMode())
                .experienceLevel(settings.getExperienceLevel())
                .language(settings.getLanguage())
                .technicalSkills(settings.getTechnicalSkills() != null ? 
                        new ArrayList<>(settings.getTechnicalSkills()) : new ArrayList<>())
                .prompt(prompt)
                .build();
    }

    /**
     * 인터뷰 종료
     */
    public void end() {
        if (this.endedAt == null) {
            this.endedAt = LocalDateTime.now();
        }
    }

    /**
     * 인터뷰 시간(분) 계산
     */
    public long getDurationInMinutes() {
        LocalDateTime end = this.endedAt != null ? this.endedAt : LocalDateTime.now();
        return java.time.Duration.between(this.startedAt, end).toMinutes();
    }
} 