package com.evawova.preview.domain.interview.dto;

import com.evawova.preview.domain.interview.entity.InterviewSession;
import com.evawova.preview.domain.interview.model.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 인터뷰 세션 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewSessionDto {
    private Long id;
    private String sessionId;
    private Long userId;
    private String userName;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private long durationInMinutes;
    private boolean completed;
    
    // 인터뷰 설정 정보
    private InterviewType type;
    private String typeName;
    private JobRole jobRole;
    private String jobRoleName;
    private InterviewerStyle interviewerStyle;
    private String interviewerStyleName;
    private InterviewDifficulty difficulty;
    private String difficultyName;
    private InterviewDuration duration;
    private String durationName;
    private InterviewMode interviewMode;
    private String interviewModeName;
    private ExperienceLevel experienceLevel;
    private String experienceLevelName;
    private InterviewLanguage language;
    private String languageName;
    private List<String> technicalSkills;
    
    // API 응답용
    private String prompt;
    
    /**
     * 엔티티를 DTO로 변환
     */
    public static InterviewSessionDto fromEntity(InterviewSession session) {
        return InterviewSessionDto.builder()
                .id(session.getId())
                .sessionId(session.getSessionId())
                .userId(session.getUser() != null ? session.getUser().getId() : null)
                .userName(session.getUser() != null ? session.getUser().getEmail() : "")
                .startedAt(session.getStartedAt())
                .endedAt(session.getEndedAt())
                .durationInMinutes(session.getDurationInMinutes())
                .completed(session.getEndedAt() != null)
                .type(session.getType())
                .typeName(session.getType() != null ? session.getType().getDisplayName() : "")
                .jobRole(session.getJobRole())
                .jobRoleName(session.getJobRole() != null ? session.getJobRole().getDisplayName() : "")
                .interviewerStyle(session.getInterviewerStyle())
                .interviewerStyleName(session.getInterviewerStyle() != null ? session.getInterviewerStyle().getDisplayName() : "")
                .difficulty(session.getDifficulty())
                .difficultyName(session.getDifficulty() != null ? session.getDifficulty().getDisplayName() : "")
                .duration(session.getDuration())
                .durationName(session.getDuration() != null ? session.getDuration().getDisplayName() : "")
                .interviewMode(session.getInterviewMode())
                .interviewModeName(session.getInterviewMode() != null ? session.getInterviewMode().getDisplayName() : "")
                .experienceLevel(session.getExperienceLevel())
                .experienceLevelName(session.getExperienceLevel() != null ? session.getExperienceLevel().getDisplayName() : "")
                .language(session.getLanguage())
                .languageName(session.getLanguage() != null ? session.getLanguage().getDisplayName() : "")
                .technicalSkills(session.getTechnicalSkills())
                .prompt(session.getPrompt())
                .build();
    }
} 