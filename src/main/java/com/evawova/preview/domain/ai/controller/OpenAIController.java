package com.evawova.preview.domain.ai.controller;

import com.evawova.preview.common.response.ApiResponse;
import com.evawova.preview.domain.ai.service.AIInterviewPromptService;
import com.evawova.preview.domain.ai.service.OpenAIService;
import com.evawova.preview.domain.interview.dto.InterviewSettings;
import com.evawova.preview.domain.interview.service.InterviewEligibilityService;
import com.evawova.preview.domain.user.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * OpenAI API 통신 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/ai")
public class OpenAIController {

    private final OpenAIService openAIService;
    private final AIInterviewPromptService interviewPromptService;
    private final ObjectMapper objectMapper;
    private final InterviewEligibilityService interviewEligibilityService;

    public OpenAIController(
            OpenAIService openAIService,
            AIInterviewPromptService interviewPromptService,
            ObjectMapper objectMapper,
            InterviewEligibilityService interviewEligibilityService) {
        this.openAIService = openAIService;
        this.interviewPromptService = interviewPromptService;
        this.objectMapper = objectMapper;
        this.interviewEligibilityService = interviewEligibilityService;
    }

    /**
     * 면접 시작 - 첫 질문 생성
     */
    @PostMapping("/interview/start")
    public ApiResponse<Map<String, Object>> startInterview(@RequestBody Map<String, Object> request,
            @AuthenticationPrincipal User user) {
        // 사용자 면접 자격 검증
        if (user != null) {
            InterviewEligibilityService.InterviewEligibilityResult eligibilityResult = interviewEligibilityService
                    .checkEligibility(user);

            if (!eligibilityResult.isEligible()) {
                log.warn("사용자 {} 면접 시작 거부: {}", user.getId(), eligibilityResult.getMessage());
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "면접 자격 검증 실패");
                errorResponse.put("reason", eligibilityResult.getMessage());
                errorResponse.put("timestamp", eligibilityResult.getTimestamp());
                return ApiResponse.error(eligibilityResult.getMessage(), HttpStatus.FORBIDDEN);
            }

            log.info("사용자 {} 면접 시작 자격 검증 통과", user.getId());
        } else {
            log.warn("인증되지 않은 사용자의 면접 시작 시도");
        }

        try {
            // InterviewSettings 객체로 변환
            InterviewSettings settings;
            if (request.containsKey("settings")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> settingsMap = (Map<String, Object>) request.get("settings");
                settings = objectMapper.convertValue(settingsMap, InterviewSettings.class);
            } else {
                settings = objectMapper.convertValue(request, InterviewSettings.class);
            }

            // 시스템 프롬프트 생성
            String customPrompt = null;
            if (request.containsKey("prompt") && request.get("prompt") != null) {
                customPrompt = request.get("prompt").toString();
            }

            // OpenAI API 호출
            Map<String, Object> result = openAIService.startInterview(settings, customPrompt);
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("면접 시작 중 오류 발생", e);
            return ApiResponse.error("면접 시작 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 면접 계속 - 다음 질문 생성
     */
    @PostMapping("/interview/continue")
    public ApiResponse<Map<String, Object>> continueInterview(@RequestBody Map<String, Object> request) {
        try {
            // 대화 기록
            @SuppressWarnings("unchecked")
            List<Map<String, String>> messages = (List<Map<String, String>>) request.get("messages");
            if (messages == null) {
                messages = new ArrayList<>();
            }

            // InterviewSettings 객체로 변환
            InterviewSettings settings;
            if (request.containsKey("settings")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> settingsMap = (Map<String, Object>) request.get("settings");
                settings = objectMapper.convertValue(settingsMap, InterviewSettings.class);
            } else {
                settings = objectMapper.convertValue(request, InterviewSettings.class);
            }

            // OpenAI API 호출
            Map<String, Object> result = openAIService.sendMessage(messages.get(messages.size() - 1).get("content"),
                    messages);
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("면접 계속 중 오류 발생", e);
            throw new RuntimeException("메시지 전송 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 면접 요약 생성
     */
    @PostMapping("/interview/summarize")
    public ApiResponse<String> summarizeInterview(@RequestBody Map<String, Object> request) {
        try {
            // 대화 기록
            @SuppressWarnings("unchecked")
            List<Map<String, String>> messages = (List<Map<String, String>>) request.get("messages");
            if (messages == null || messages.isEmpty()) {
                return ApiResponse.error("요약할 면접 기록이 없습니다.");
            }

            // InterviewSettings 객체로 변환
            InterviewSettings settings;
            if (request.containsKey("settings")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> settingsMap = (Map<String, Object>) request.get("settings");
                settings = objectMapper.convertValue(settingsMap, InterviewSettings.class);
            } else {
                settings = InterviewSettings.createDefault();
            }

            // OpenAI API 호출
            String summary = openAIService.generateInterviewSummary(messages, settings);
            return ApiResponse.success(summary);
        } catch (Exception e) {
            log.error("면접 요약 생성 중 오류 발생", e);
            throw new RuntimeException("요약 생성 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 면접 평가 생성
     */
    @PostMapping("/interview/evaluate")
    public ApiResponse<Map<String, Object>> evaluateInterview(@RequestBody Map<String, Object> request) {
        try {
            // 대화 기록
            @SuppressWarnings("unchecked")
            List<Map<String, String>> messages = (List<Map<String, String>>) request.get("messages");
            if (messages == null || messages.isEmpty()) {
                return ApiResponse.error("평가할 면접 기록이 없습니다.");
            }

            // InterviewSettings 객체로 변환
            InterviewSettings settings;
            if (request.containsKey("settings")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> settingsMap = (Map<String, Object>) request.get("settings");
                settings = objectMapper.convertValue(settingsMap, InterviewSettings.class);
            } else {
                settings = InterviewSettings.createDefault();
            }

            // OpenAI API 호출
            Map<String, Object> evaluation = openAIService.generateInterviewEvaluation(messages, settings);
            return ApiResponse.success(evaluation);
        } catch (Exception e) {
            log.error("면접 평가 생성 중 오류 발생", e);
            throw new RuntimeException("평가 생성 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}