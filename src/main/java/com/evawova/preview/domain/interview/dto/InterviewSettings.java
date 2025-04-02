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
} 