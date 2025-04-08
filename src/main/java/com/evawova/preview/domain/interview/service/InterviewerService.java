package com.evawova.preview.domain.interview.service;

import com.evawova.preview.domain.interview.dto.InterviewerDto;
import com.evawova.preview.domain.interview.entity.Interviewer;
import com.evawova.preview.domain.interview.entity.Interviewer.InterviewerPersonality;
import com.evawova.preview.domain.interview.entity.Interviewer.QuestionStyle;
import com.evawova.preview.domain.interview.entity.Interviewer.FeedbackStyle;
import com.evawova.preview.domain.interview.repository.InterviewerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 면접관 관련 비즈니스 로직을 처리하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InterviewerService {

    private final InterviewerRepository interviewerRepository;

    /**
     * 모든 면접관 조회
     */
    @Transactional(readOnly = true)
    public List<InterviewerDto> getAllInterviewers() {
        return interviewerRepository.findAll().stream()
                .map(InterviewerDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 정렬 순서대로 모든 면접관 조회
     */
    @Transactional(readOnly = true)
    public List<InterviewerDto> getAllInterviewersSorted() {
        return interviewerRepository.findAllByOrderBySortOrderAsc().stream()
                .map(InterviewerDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 활성화된 면접관만 조회 (정렬 순서대로)
     */
    @Transactional(readOnly = true)
    public List<InterviewerDto> getActiveInterviewers() {
        return interviewerRepository.findByActiveTrueOrderBySortOrderAsc().stream()
                .map(InterviewerDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * ID로 면접관 조회
     */
    @Transactional(readOnly = true)
    public InterviewerDto getInterviewerById(Long id) {
        return interviewerRepository.findById(id)
                .map(InterviewerDto::fromEntity)
                .orElseThrow(() -> new IllegalArgumentException("면접관을 찾을 수 없습니다. ID: " + id));
    }

    /**
     * 코드로 면접관 조회
     */
    @Transactional(readOnly = true)
    public InterviewerDto getInterviewerByCode(String code) {
        return interviewerRepository.findByCode(code)
                .map(InterviewerDto::fromEntity)
                .orElseThrow(() -> new IllegalArgumentException("면접관을 찾을 수 없습니다. 코드: " + code));
    }

    /**
     * 면접관 성향으로 조회
     */
    @Transactional(readOnly = true)
    public List<InterviewerDto> getInterviewersByPersonality(InterviewerPersonality personality) {
        return interviewerRepository.findByPersonality(personality).stream()
                .map(InterviewerDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 질문 스타일로 조회
     */
    @Transactional(readOnly = true)
    public List<InterviewerDto> getInterviewersByQuestionStyle(QuestionStyle questionStyle) {
        return interviewerRepository.findByQuestionStyle(questionStyle).stream()
                .map(InterviewerDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 피드백 스타일로 조회
     */
    @Transactional(readOnly = true)
    public List<InterviewerDto> getInterviewersByFeedbackStyle(FeedbackStyle feedbackStyle) {
        return interviewerRepository.findByFeedbackStyle(feedbackStyle).stream()
                .map(InterviewerDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 면접관 성향과 질문 스타일로 조회
     */
    @Transactional(readOnly = true)
    public List<InterviewerDto> getInterviewersByPersonalityAndQuestionStyle(
            InterviewerPersonality personality, QuestionStyle questionStyle) {
        return interviewerRepository.findByPersonalityAndQuestionStyle(personality, questionStyle).stream()
                .map(InterviewerDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 면접관 성향과 피드백 스타일로 조회
     */
    @Transactional(readOnly = true)
    public List<InterviewerDto> getInterviewersByPersonalityAndFeedbackStyle(
            InterviewerPersonality personality, FeedbackStyle feedbackStyle) {
        return interviewerRepository.findByPersonalityAndFeedbackStyle(personality, feedbackStyle).stream()
                .map(InterviewerDto::fromEntity)
                .collect(Collectors.toList());
    }
}