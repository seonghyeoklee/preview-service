package com.evawova.preview.domain.interview.model;

import com.evawova.preview.domain.common.model.AggregateRoot;
import com.evawova.preview.domain.interview.dto.InterviewSettings;
import com.evawova.preview.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
@NoArgsConstructor
public class InterviewSession extends AggregateRoot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String sessionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private LocalDateTime startedAt;

    private LocalDateTime endedAt;

    @Enumerated(EnumType.STRING)
    private InterviewType type;

    @Enumerated(EnumType.STRING)
    private JobRole jobRole;

    @Enumerated(EnumType.STRING)
    private InterviewerStyle interviewerStyle;

    @Enumerated(EnumType.STRING)
    private InterviewDifficulty difficulty;

    @Enumerated(EnumType.STRING)
    private InterviewDuration duration;

    @Enumerated(EnumType.STRING)
    private InterviewMode interviewMode;

    @Enumerated(EnumType.STRING)
    private ExperienceLevel experienceLevel;

    @Enumerated(EnumType.STRING)
    private InterviewLanguage language;

    @ElementCollection
    @CollectionTable(name = "interview_session_skills", joinColumns = @JoinColumn(name = "interview_session_id"))
    @Column(name = "skill")
    private List<String> technicalSkills = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String prompt;

    /**
     * 인터뷰 세션 생성을 위한 팩토리 메서드
     */
    public static InterviewSession create(User user, InterviewSettings settings, String prompt) {
        InterviewSession session = new InterviewSession();
        session.sessionId = UUID.randomUUID().toString();
        session.user = user;
        session.startedAt = LocalDateTime.now();
        
        // 설정 값 복사
        session.type = settings.getType();
        session.jobRole = settings.getJobRole();
        session.interviewerStyle = settings.getInterviewerStyle();
        session.difficulty = settings.getDifficulty();
        session.duration = settings.getDuration();
        session.interviewMode = settings.getInterviewMode();
        session.experienceLevel = settings.getExperienceLevel();
        session.language = settings.getLanguage();
        
        if (settings.getTechnicalSkills() != null) {
            session.technicalSkills.addAll(settings.getTechnicalSkills());
        }
        
        session.prompt = prompt;
        
        return session;
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