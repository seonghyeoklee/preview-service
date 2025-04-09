package com.evawova.preview.domain.interview.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InterviewDifficulty {
    BEGINNER("초급", "Beginner", "기본적인 질문 위주로 구성된 난이도입니다"),
    INTERMEDIATE("중급", "Intermediate", "실무 경험을 확인하는 난이도입니다"),
    ADVANCED("고급", "Advanced", "심층적인 지식과 경험을 확인하는 난이도입니다"),
    EXPERT("전문가", "Expert", "최고 수준의 전문성을 요구하는 난이도입니다");

    private final String korDescription;
    private final String engDescription;
    private final String detailDescription;
}