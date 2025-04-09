package com.evawova.preview.domain.interview.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InterviewType {
    TECHNICAL("기술 면접", "Technical Interview"),
    DESIGN("디자인 면접", "Design Interview"),
    MARKETING("마케팅 면접", "Marketing Interview"),
    BUSINESS("경영 면접", "Business Interview");

    private final String korDescription;
    private final String engDescription;
}