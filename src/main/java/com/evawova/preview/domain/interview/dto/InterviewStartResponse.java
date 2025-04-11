package com.evawova.preview.domain.interview.dto;

import com.evawova.preview.domain.interview.entity.enums.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class InterviewStartResponse {

    @Schema(description = "인터뷰 세션 ID")
    private Long sessionId;

    @Schema(description = "인터뷰 유형")
    private InterviewType type;

    @Schema(description = "직무")
    private JobRole jobRole;

    @Schema(description = "면접관 스타일")
    private InterviewerStyle interviewerStyle;

    @Schema(description = "난이도")
    private InterviewDifficulty difficulty;

    @Schema(description = "면접 시간")
    private InterviewDuration duration;

    @Schema(description = "면접 모드")
    private InterviewMode interviewMode;

    @Schema(description = "경력 수준")
    private ExperienceLevel experienceLevel;

    @Schema(description = "기술 스킬 목록")
    private List<String> technicalSkills;

    @Schema(description = "인터뷰 언어")
    private InterviewLanguage language;

    @Schema(description = "시작 시간")
    private LocalDateTime startTime;

    @Schema(description = "예상 종료 시간")
    private LocalDateTime expectedEndTime;
}