package com.evawova.preview.domain.interview.dto;

import com.evawova.preview.domain.interview.entity.enums.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class InterviewStartRequest {

    @NotNull
    @Schema(description = "인터뷰 유형")
    private InterviewType type;

    @NotNull
    @Schema(description = "직무")
    private JobRole jobRole;

    @NotNull
    @Schema(description = "면접관 스타일")
    private InterviewerStyle interviewerStyle;

    @NotNull
    @Schema(description = "난이도")
    private InterviewDifficulty difficulty;

    @NotNull
    @Schema(description = "면접 시간")
    private InterviewDuration duration;

    @NotNull
    @Schema(description = "면접 모드")
    private InterviewMode interviewMode;

    @NotNull
    @Schema(description = "경력 수준")
    private ExperienceLevel experienceLevel;

    @Schema(description = "기술 스킬 목록")
    private List<String> technicalSkills;

    @NotNull
    @Schema(description = "인터뷰 언어")
    private InterviewLanguage language;
}