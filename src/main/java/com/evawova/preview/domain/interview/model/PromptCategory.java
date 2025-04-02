package com.evawova.preview.domain.interview.model;

public enum PromptCategory {
    BASIC("기본 프롬프트"),
    INTERVIEWER_STYLE("면접관 스타일"),
    JOB_INFO("직무 정보"),
    EXPERIENCE_SKILLS("경험 및 스킬"),
    DIFFICULTY_STYLE("난이도 및 스타일"),
    TIME_QUESTIONS("시간 및 질문"),
    INTERVIEW_PROCESS("인터뷰 프로세스"),
    LANGUAGE("언어 설정"),
    CLOSING("마무리 지침");

    private final String displayName;

    PromptCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}