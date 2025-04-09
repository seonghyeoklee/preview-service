package com.evawova.preview.domain.interview.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InterviewDuration {
    SHORT("짧은 면접", "Short Interview", 15),
    LONG("긴 면접", "Long Interview", 45);

    private final String korDescription;
    private final String engDescription;
    private final int minutes;
}