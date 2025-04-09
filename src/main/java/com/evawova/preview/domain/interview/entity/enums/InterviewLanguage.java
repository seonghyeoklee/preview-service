package com.evawova.preview.domain.interview.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InterviewLanguage {
    KO("한국어", "Korean", "ko"),
    EN("영어", "English", "en");

    private final String korDescription;
    private final String engDescription;
    private final String languageCode;
}