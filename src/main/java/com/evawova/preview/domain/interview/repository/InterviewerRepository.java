package com.evawova.preview.domain.interview.repository;

import com.evawova.preview.domain.interview.entity.Interviewer;
import com.evawova.preview.domain.interview.entity.Interviewer.InterviewerPersonality;
import com.evawova.preview.domain.interview.entity.Interviewer.QuestionStyle;
import com.evawova.preview.domain.interview.entity.Interviewer.FeedbackStyle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterviewerRepository extends JpaRepository<Interviewer, Long> {

    /**
     * 코드로 면접관 조회
     */
    Optional<Interviewer> findByCode(String code);

    /**
     * 활성화된 면접관만 조회
     */
    List<Interviewer> findByActiveTrue();

    /**
     * 정렬 순서대로 면접관 조회
     */
    List<Interviewer> findAllByOrderBySortOrderAsc();

    /**
     * 활성화된 면접관을 정렬 순서대로 조회
     */
    List<Interviewer> findByActiveTrueOrderBySortOrderAsc();

    /**
     * 면접관 성향으로 조회
     */
    List<Interviewer> findByPersonality(InterviewerPersonality personality);

    /**
     * 질문 스타일로 조회
     */
    List<Interviewer> findByQuestionStyle(QuestionStyle questionStyle);

    /**
     * 피드백 스타일로 조회
     */
    List<Interviewer> findByFeedbackStyle(FeedbackStyle feedbackStyle);

    /**
     * 면접관 성향과 질문 스타일로 조회
     */
    List<Interviewer> findByPersonalityAndQuestionStyle(
            InterviewerPersonality personality, QuestionStyle questionStyle);

    /**
     * 면접관 성향과 피드백 스타일로 조회
     */
    List<Interviewer> findByPersonalityAndFeedbackStyle(
            InterviewerPersonality personality, FeedbackStyle feedbackStyle);
}