package com.evawova.preview.domain.interview.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InterviewerStyle {
    FRIENDLY("친절한", "Friendly", "편안한 분위기에서 대화하듯 면접을 진행합니다"),
    TECHNICAL("기술 중심적인", "Technical", "기술적 지식과 역량을 중점적으로 평가합니다"),
    CHALLENGING("도전적인", "Challenging", "예상치 못한 질문으로 문제 해결 능력을 테스트합니다"),
    BEHAVIORAL("행동 중심적인", "Behavioral", "과거 경험과 행동 패턴을 통해 역량을 평가합니다"),
    PROFESSIONAL("전문적인", "Professional", "업계 표준과 전문성을 중심으로 평가합니다");

    private final String korDescription;
    private final String engDescription;
    private final String detailDescription;
}