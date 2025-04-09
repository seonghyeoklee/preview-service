package com.evawova.preview.domain.interview.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExperienceLevel {
    ENTRY("신입", "Entry Level", 0, 0),
    JUNIOR("주니어", "Junior Level", 1, 3),
    MID_LEVEL("미드레벨", "Mid Level", 4, 7),
    SENIOR("시니어", "Senior Level", 8, 100);

    private final String korDescription;
    private final String engDescription;
    private final int minYears;
    private final int maxYears;
}