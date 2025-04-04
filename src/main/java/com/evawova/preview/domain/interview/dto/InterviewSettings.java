package com.evawova.preview.domain.interview.dto;

import com.evawova.preview.domain.interview.model.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 면접 설정 정보를 담는 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewSettings {

    // 면접 유형
    private InterviewType type;

    // 직무 정보
    private JobRole jobRole;

    // 면접관 스타일
    private InterviewerStyle interviewerStyle;

    // 면접 난이도
    private InterviewDifficulty difficulty;

    // 경력 수준
    private ExperienceLevel experienceLevel;

    // 면접 시간
    private InterviewDuration duration;

    // 면접 모드
    private InterviewMode interviewMode;

    // 기술 스택
    private List<String> technicalSkills;

    // 면접 언어
    private InterviewLanguage language;

    // 사용할 OpenAI 모델
    private String model;

    /**
     * 기본값이 적용된 면접 설정 생성
     */
    public static InterviewSettings createDefault() {
        return InterviewSettings.builder()
                .type(InterviewType.DEVELOPMENT)
                .jobRole(JobRole.BACKEND_DEVELOPER)
                .interviewerStyle(InterviewerStyle.FRIENDLY)
                .difficulty(InterviewDifficulty.INTERMEDIATE)
                .experienceLevel(ExperienceLevel.MID_LEVEL)
                .duration(InterviewDuration.MEDIUM)
                .interviewMode(InterviewMode.TEXT)
                .language(InterviewLanguage.KO)
                .model("gpt-3.5-turbo")
                .build();
    }

    /**
     * 면접 설정이 유효한지 검사하고 빈 필드는 기본값으로 채움
     */
    public void validateAndFillDefaults() {
        if (type == null) {
            type = InterviewType.DEVELOPMENT;
        }

        if (jobRole == null) {
            jobRole = JobRole.BACKEND_DEVELOPER;
        }

        if (interviewerStyle == null) {
            interviewerStyle = InterviewerStyle.FRIENDLY;
        }

        if (difficulty == null) {
            difficulty = InterviewDifficulty.INTERMEDIATE;
        }

        if (experienceLevel == null) {
            experienceLevel = ExperienceLevel.MID_LEVEL;
        }

        if (duration == null) {
            duration = InterviewDuration.MEDIUM;
        }

        if (interviewMode == null) {
            interviewMode = InterviewMode.TEXT;
        }

        if (language == null) {
            language = InterviewLanguage.KO;
        }

        if (model == null || model.isBlank()) {
            model = "gpt-3.5-turbo";
        }
    }
}