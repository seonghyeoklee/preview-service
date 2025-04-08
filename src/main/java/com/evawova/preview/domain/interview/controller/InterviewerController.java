package com.evawova.preview.domain.interview.controller;

import com.evawova.preview.domain.interview.dto.InterviewerDto;
import com.evawova.preview.domain.interview.entity.Interviewer.InterviewerPersonality;
import com.evawova.preview.domain.interview.entity.Interviewer.QuestionStyle;
import com.evawova.preview.domain.interview.entity.Interviewer.FeedbackStyle;
import com.evawova.preview.domain.interview.service.InterviewerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 면접관 관련 API를 제공하는 컨트롤러
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/interviewers")
public class InterviewerController {

    private final InterviewerService interviewerService;

    /**
     * 모든 면접관 목록 조회
     */
    @GetMapping
    public ResponseEntity<List<InterviewerDto>> getAllInterviewers() {
        return ResponseEntity.ok(interviewerService.getAllInterviewersSorted());
    }

    /**
     * 활성화된 면접관 목록 조회
     */
    @GetMapping("/active")
    public ResponseEntity<List<InterviewerDto>> getActiveInterviewers() {
        return ResponseEntity.ok(interviewerService.getActiveInterviewers());
    }

    /**
     * ID로 면접관 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<InterviewerDto> getInterviewerById(@PathVariable Long id) {
        return ResponseEntity.ok(interviewerService.getInterviewerById(id));
    }

    /**
     * 코드로 면접관 조회
     */
    @GetMapping("/by-code/{code}")
    public ResponseEntity<InterviewerDto> getInterviewerByCode(@PathVariable String code) {
        return ResponseEntity.ok(interviewerService.getInterviewerByCode(code));
    }

    /**
     * 면접관 성향으로 면접관 목록 조회
     */
    @GetMapping("/by-personality/{personality}")
    public ResponseEntity<List<InterviewerDto>> getInterviewersByPersonality(
            @PathVariable InterviewerPersonality personality) {
        return ResponseEntity.ok(interviewerService.getInterviewersByPersonality(personality));
    }

    /**
     * 질문 스타일로 면접관 목록 조회
     */
    @GetMapping("/by-question-style/{questionStyle}")
    public ResponseEntity<List<InterviewerDto>> getInterviewersByQuestionStyle(
            @PathVariable QuestionStyle questionStyle) {
        return ResponseEntity.ok(interviewerService.getInterviewersByQuestionStyle(questionStyle));
    }

    /**
     * 피드백 스타일로 면접관 목록 조회
     */
    @GetMapping("/by-feedback-style/{feedbackStyle}")
    public ResponseEntity<List<InterviewerDto>> getInterviewersByFeedbackStyle(
            @PathVariable FeedbackStyle feedbackStyle) {
        return ResponseEntity.ok(interviewerService.getInterviewersByFeedbackStyle(feedbackStyle));
    }

    /**
     * 면접관 성향과 질문 스타일로 면접관 목록 조회
     */
    @GetMapping("/by-personality-and-question-style")
    public ResponseEntity<List<InterviewerDto>> getInterviewersByPersonalityAndQuestionStyle(
            @RequestParam InterviewerPersonality personality,
            @RequestParam QuestionStyle questionStyle) {
        return ResponseEntity.ok(
                interviewerService.getInterviewersByPersonalityAndQuestionStyle(personality, questionStyle));
    }

    /**
     * 면접관 성향과 피드백 스타일로 면접관 목록 조회
     */
    @GetMapping("/by-personality-and-feedback-style")
    public ResponseEntity<List<InterviewerDto>> getInterviewersByPersonalityAndFeedbackStyle(
            @RequestParam InterviewerPersonality personality,
            @RequestParam FeedbackStyle feedbackStyle) {
        return ResponseEntity.ok(
                interviewerService.getInterviewersByPersonalityAndFeedbackStyle(personality, feedbackStyle));
    }
}