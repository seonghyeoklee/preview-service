package com.evawova.preview.domain.interview.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InterviewMode {
    TEXT("텍스트 기반", "Text-based", "텍스트 채팅을 통해 면접을 진행합니다"),
    VOICE("음성 기반", "Voice-based", "음성 대화를 통해 면접을 진행합니다");

    private final String korDescription;
    private final String engDescription;
    private final String detailDescription;
}