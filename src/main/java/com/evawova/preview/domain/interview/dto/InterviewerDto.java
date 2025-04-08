package com.evawova.preview.domain.interview.dto;

import com.evawova.preview.domain.interview.entity.Interviewer;
import com.evawova.preview.domain.interview.entity.Interviewer.InterviewerPersonality;
import com.evawova.preview.domain.interview.entity.Interviewer.QuestionStyle;
import com.evawova.preview.domain.interview.entity.Interviewer.FeedbackStyle;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class InterviewerDto {
    private Long id;
    private String code;
    private String name;
    private String nameEn;
    private String description;
    private String descriptionEn;
    private InterviewerPersonality personality;
    private String personalityDisplayName;
    private QuestionStyle questionStyle;
    private String questionStyleDisplayName;
    private FeedbackStyle feedbackStyle;
    private String feedbackStyleDisplayName;
    private String profileImageUrl;
    private Boolean active;
    private Integer sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Interviewer 엔티티를 DTO로 변환
     */
    public static InterviewerDto fromEntity(Interviewer interviewer) {
        return InterviewerDto.builder()
                .id(interviewer.getId())
                .code(interviewer.getCode())
                .name(interviewer.getName())
                .nameEn(interviewer.getNameEn())
                .description(interviewer.getDescription())
                .descriptionEn(interviewer.getDescriptionEn())
                .personality(interviewer.getPersonality())
                .personalityDisplayName(interviewer.getPersonality().getDisplayName())
                .questionStyle(interviewer.getQuestionStyle())
                .questionStyleDisplayName(interviewer.getQuestionStyle().getDisplayName())
                .feedbackStyle(interviewer.getFeedbackStyle())
                .feedbackStyleDisplayName(interviewer.getFeedbackStyle().getDisplayName())
                .profileImageUrl(interviewer.getProfileImageUrl())
                .active(interviewer.getActive())
                .sortOrder(interviewer.getSortOrder())
                .createdAt(interviewer.getCreatedAt())
                .updatedAt(interviewer.getUpdatedAt())
                .build();
    }
}