package com.evawova.preview.domain.interview.dto;

import com.evawova.preview.domain.interview.model.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * 인터뷰 설정 정보를 담는 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewSettings {
    private InterviewType type;
    private JobRole jobRole;
    private InterviewerStyle interviewerStyle;
    private InterviewDifficulty difficulty;
    private InterviewDuration duration;
    private InterviewMode interviewMode;
    private ExperienceLevel experienceLevel;
    private List<String> technicalSkills;
    private InterviewLanguage language;

    // 하위 호환성을 위한 필드
    private String position;
    private String interviewStyle;
    private String interviewModeAsString;
    private String languageCode;

    /**
     * 하위 호환성을 위한 변환 메서드
     */
    public static InterviewSettings fromLegacy(String type, String position, String interviewStyle,
                                              String difficulty, String duration, String interviewMode,
                                              String experienceLevel, List<String> technicalSkills,
                                              String language) {
        InterviewSettings settings = new InterviewSettings();
        
        // 값 설정
        if (type != null && !type.isEmpty()) {
            try {
                settings.type = InterviewType.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException ignored) {}
        }
        
        settings.position = position;
        settings.interviewStyle = interviewStyle;
        settings.interviewModeAsString = interviewMode;
        settings.languageCode = language != null ? language : "ko";
        settings.technicalSkills = technicalSkills;
        
        // Enum 값 설정
        if (position != null && !position.isEmpty()) {
            try {
                settings.jobRole = getJobRoleFromString(position);
            } catch (IllegalArgumentException ignored) {}
        }
        
        if (interviewStyle != null && !interviewStyle.isEmpty()) {
            try {
                settings.interviewerStyle = getInterviewerStyleFromString(interviewStyle);
            } catch (IllegalArgumentException ignored) {}
        }
        
        if (difficulty != null && !difficulty.isEmpty()) {
            try {
                settings.difficulty = InterviewDifficulty.valueOf(difficulty.toUpperCase());
            } catch (IllegalArgumentException ignored) {}
        }
        
        if (duration != null && !duration.isEmpty()) {
            try {
                settings.duration = InterviewDuration.valueOf(duration.toUpperCase());
            } catch (IllegalArgumentException ignored) {}
        }
        
        if (interviewMode != null && !interviewMode.isEmpty()) {
            try {
                settings.interviewMode = getInterviewModeFromString(interviewMode);
            } catch (IllegalArgumentException ignored) {}
        }
        
        if (experienceLevel != null && !experienceLevel.isEmpty()) {
            try {
                settings.experienceLevel = ExperienceLevel.valueOf(experienceLevel.toUpperCase());
            } catch (IllegalArgumentException ignored) {}
        }
        
        if (language != null && !language.isEmpty()) {
            try {
                settings.language = getLanguageFromCode(language);
            } catch (IllegalArgumentException ignored) {}
        }
        
        return settings;
    }

    // 직무 역할 변환
    private static JobRole getJobRoleFromString(String position) {
        if (position == null || position.isEmpty()) {
            return null;
        }

        switch (position) {
            case "frontend_developer": return JobRole.FRONTEND_DEVELOPER;
            case "backend_developer": return JobRole.BACKEND_DEVELOPER;
            case "fullstack_developer": return JobRole.FULLSTACK_DEVELOPER;
            case "mobile_developer": return JobRole.MOBILE_DEVELOPER;
            case "devops_developer": return JobRole.DEVOPS_DEVELOPER;
            case "data_scientist": return JobRole.DATA_SCIENTIST;
            case "ai_engineer": return JobRole.AI_ENGINEER;
            case "security_engineer": return JobRole.SECURITY_ENGINEER;
            case "qa_engineer": return JobRole.QA_ENGINEER;
            case "ui_ux_designer": return JobRole.UI_UX_DESIGNER;
            case "graphic_designer": return JobRole.GRAPHIC_DESIGNER;
            case "product_designer": return JobRole.PRODUCT_DESIGNER;
            case "brand_designer": return JobRole.BRAND_DESIGNER;
            case "digital_marketer": return JobRole.DIGITAL_MARKETER;
            case "content_marketer": return JobRole.CONTENT_MARKETER;
            case "brand_marketer": return JobRole.BRAND_MARKETER;
            case "growth_hacker": return JobRole.GROWTH_HACKER;
            case "hr_manager": return JobRole.HR_MANAGER;
            case "finance_manager": return JobRole.FINANCE_MANAGER;
            case "business_development": return JobRole.BUSINESS_DEVELOPMENT;
            case "project_manager": return JobRole.PROJECT_MANAGER;
            default: return null;
        }
    }

    // 면접관 스타일 변환
    private static InterviewerStyle getInterviewerStyleFromString(String style) {
        if (style == null || style.isEmpty()) {
            return null;
        }

        switch (style) {
            case "friendly": return InterviewerStyle.FRIENDLY;
            case "technical": return InterviewerStyle.TECHNICAL;
            case "challenging": return InterviewerStyle.CHALLENGING;
            default: return null;
        }
    }

    // 면접 모드 변환
    private static InterviewMode getInterviewModeFromString(String mode) {
        if (mode == null || mode.isEmpty()) {
            return null;
        }

        switch (mode) {
            case "text": return InterviewMode.TEXT;
            case "voice": return InterviewMode.VOICE;
            default: return null;
        }
    }

    // 언어 변환
    private static InterviewLanguage getLanguageFromCode(String code) {
        if (code == null || code.isEmpty()) {
            return InterviewLanguage.KO;
        }

        switch (code) {
            case "en": return InterviewLanguage.EN;
            case "ko":
            default: return InterviewLanguage.KO;
        }
    }
} 