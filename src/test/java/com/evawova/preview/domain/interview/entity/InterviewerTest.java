package com.evawova.preview.domain.interview.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class InterviewerTest {

    @Test
    @DisplayName("면접관 엔티티 생성 테스트")
    void createInterviewer() {
        // given
        LocalDateTime now = LocalDateTime.now();
        String code = "friendly";
        String name = "김친절";
        String nameEn = "Kim Friendly";
        String description = "친절한 면접관입니다.";
        String descriptionEn = "A friendly interviewer.";
        Interviewer.InterviewerPersonality personality = Interviewer.InterviewerPersonality.FRIENDLY;
        Interviewer.QuestionStyle questionStyle = Interviewer.QuestionStyle.OPEN_ENDED;
        Interviewer.FeedbackStyle feedbackStyle = Interviewer.FeedbackStyle.ENCOURAGING;
        String profileImageUrl = "/images/interviewers/friendly.png";
        boolean active = true;
        int sortOrder = 1;

        // when
        Interviewer interviewer = Interviewer.builder()
                .code(code)
                .name(name)
                .nameEn(nameEn)
                .description(description)
                .descriptionEn(descriptionEn)
                .personality(personality)
                .questionStyle(questionStyle)
                .feedbackStyle(feedbackStyle)
                .profileImageUrl(profileImageUrl)
                .active(active)
                .sortOrder(sortOrder)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // then
        assertThat(interviewer.getCode()).isEqualTo(code);
        assertThat(interviewer.getName()).isEqualTo(name);
        assertThat(interviewer.getNameEn()).isEqualTo(nameEn);
        assertThat(interviewer.getDescription()).isEqualTo(description);
        assertThat(interviewer.getDescriptionEn()).isEqualTo(descriptionEn);
        assertThat(interviewer.getPersonality()).isEqualTo(personality);
        assertThat(interviewer.getQuestionStyle()).isEqualTo(questionStyle);
        assertThat(interviewer.getFeedbackStyle()).isEqualTo(feedbackStyle);
        assertThat(interviewer.getProfileImageUrl()).isEqualTo(profileImageUrl);
        assertThat(interviewer.getActive()).isEqualTo(active);
        assertThat(interviewer.getSortOrder()).isEqualTo(sortOrder);
        assertThat(interviewer.getCreatedAt()).isEqualTo(now);
        assertThat(interviewer.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("면접관 성향 enum 테스트")
    void interviewerPersonalityEnum() {
        // given
        Interviewer.InterviewerPersonality friendly = Interviewer.InterviewerPersonality.FRIENDLY;
        Interviewer.InterviewerPersonality strict = Interviewer.InterviewerPersonality.STRICT;
        Interviewer.InterviewerPersonality technical = Interviewer.InterviewerPersonality.TECHNICAL;

        // then
        assertThat(friendly.getDisplayName()).isEqualTo("친근한");
        assertThat(strict.getDisplayName()).isEqualTo("엄격한");
        assertThat(technical.getDisplayName()).isEqualTo("기술 중심적");
    }

    @Test
    @DisplayName("질문 스타일 enum 테스트")
    void questionStyleEnum() {
        // given
        Interviewer.QuestionStyle openEnded = Interviewer.QuestionStyle.OPEN_ENDED;
        Interviewer.QuestionStyle direct = Interviewer.QuestionStyle.DIRECT;
        Interviewer.QuestionStyle technical = Interviewer.QuestionStyle.TECHNICAL;

        // then
        assertThat(openEnded.getDisplayName()).isEqualTo("개방형");
        assertThat(direct.getDisplayName()).isEqualTo("직접적");
        assertThat(technical.getDisplayName()).isEqualTo("기술적");
    }

    @Test
    @DisplayName("피드백 스타일 enum 테스트")
    void feedbackStyleEnum() {
        // given
        Interviewer.FeedbackStyle constructive = Interviewer.FeedbackStyle.CONSTRUCTIVE;
        Interviewer.FeedbackStyle critical = Interviewer.FeedbackStyle.CRITICAL;
        Interviewer.FeedbackStyle encouraging = Interviewer.FeedbackStyle.ENCOURAGING;

        // then
        assertThat(constructive.getDisplayName()).isEqualTo("건설적");
        assertThat(critical.getDisplayName()).isEqualTo("비판적");
        assertThat(encouraging.getDisplayName()).isEqualTo("격려하는");
    }

    @Test
    @DisplayName("면접관 엔티티 기본값 테스트")
    void interviewerDefaultValues() {
        // given
        Interviewer interviewer = Interviewer.builder()
                .code("test")
                .name("테스트")
                .personality(Interviewer.InterviewerPersonality.FRIENDLY)
                .questionStyle(Interviewer.QuestionStyle.OPEN_ENDED)
                .feedbackStyle(Interviewer.FeedbackStyle.CONSTRUCTIVE)
                .build();

        // then
        assertThat(interviewer.getActive()).isTrue();
        assertThat(interviewer.getSortOrder()).isZero();
    }
}