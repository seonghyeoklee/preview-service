package com.evawova.preview.domain.ai.controller;

import com.evawova.preview.domain.ai.service.AIInterviewPromptService;
import com.evawova.preview.domain.ai.service.OpenAIService;
import com.evawova.preview.domain.interview.dto.InterviewSettings;
import com.evawova.preview.global.api.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
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

    public OpenAIController(
            OpenAIService openAIService,
            AIInterviewPromptService interviewPromptService,
            ObjectMapper objectMapper) {
        this.openAIService = openAIService;
        this.interviewPromptService = interviewPromptService;
        this.objectMapper = objectMapper;
    }

    /**
     * 면접 시작 - 첫 질문 생성
     */
    @PostMapping("/interview/start")
    public ApiResponse<?> startInterview(@RequestBody Map<String, Object> request) {
        // InterviewSettings 객체로 변환
        InterviewSettings settings;
        if (request.containsKey("settings")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> settingsMap = (Map<String, Object>) request.get("settings");
            // 여기서는 간단히 ObjectMapper를 사용하여 변환한다고 가정
            // 실제 구현에서는 적절한 변환 로직을 추가해야 함
            settings = objectMapper.convertValue(settingsMap, InterviewSettings.class);
        } else {
            settings = objectMapper.convertValue(request, InterviewSettings.class);
        }

        log.info("면접 시작 요청 - 직무: {}, 난이도: {}", settings.getJobRole(), settings.getDifficulty());

        // 설정 유효성 검사 및 기본값 설정
        settings.validateAndFillDefaults();

        // 커스텀 프롬프트 확인
        String customPrompt = null;
        if (request.containsKey("customPrompt")) {
            customPrompt = (String) request.get("customPrompt");
            log.info("커스텀 프롬프트가 제공되었습니다. 길이: {}", customPrompt.length());
        }

        Map<String, Object> result = openAIService.startInterview(settings, customPrompt);
        return ApiResponse.success(result);
    }

    /**
     * 메시지 전송 - 대화 이력을 바탕으로 응답 생성
     */
    @PostMapping("/interview/message")
    public ApiResponse<?> sendMessage(@RequestBody Map<String, Object> request) {
        String message = (String) request.get("message");
        @SuppressWarnings("unchecked")
        List<Map<String, String>> conversationHistory = (List<Map<String, String>>) request.get("conversation");

        log.info("메시지 전송 요청 - 대화기록 길이: {}", conversationHistory.size());

        Map<String, Object> result = openAIService.sendMessage(message, conversationHistory);
        return ApiResponse.success(result);
    }

    /**
     * 면접 결과 요약 - 대화 이력을 바탕으로 면접 결과 생성
     */
    @PostMapping("/interview/summary")
    public ApiResponse<?> generateInterviewSummary(@RequestBody Map<String, Object> request) {
        @SuppressWarnings("unchecked")
        List<Map<String, String>> messages = (List<Map<String, String>>) request.get("messages");
        InterviewSettings settings = InterviewSettings.createDefault();

        // 설정값이 있으면 적용
        if (request.containsKey("settings")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> settingsMap = (Map<String, Object>) request.get("settings");

            // TODO: Map을 InterviewSettings로 변환하는 로직 (더 좋은 방법이 있다면 개선 필요)
        }

        log.info("면접 요약 생성 요청 - 메시지 수: {}", messages.size());

        String summary = openAIService.generateInterviewSummary(messages, settings);
        return ApiResponse.success(Map.of("summary", summary));
    }

    /**
     * 사용 가능한 기술 스택 목록 조회
     */
    @GetMapping("/skills")
    public ApiResponse<?> getAvailableSkills() {
        List<String> skills = interviewPromptService.getAvailableSkills();
        return ApiResponse.success(Map.of("skills", skills));
    }

    /**
     * 면접 프롬프트 조회 - 면접 설정에 따른 프롬프트 생성
     */
    @PostMapping("/interview/prompt")
    public ApiResponse<?> generateInterviewPrompt(@RequestBody InterviewSettings settings) {
        log.info("면접 프롬프트 조회 요청 - 직무: {}, 난이도: {}", settings.getJobRole(), settings.getDifficulty());

        // 설정 유효성 검사 및 기본값 설정
        settings.validateAndFillDefaults();

        String prompt = interviewPromptService.generateInterviewPrompt(settings);
        return ApiResponse.success(Map.of("prompt", prompt));
    }

    /**
     * 테스트용 API - 입력한 텍스트를 그대로 에코
     */
    @PostMapping("/test/echo")
    public ApiResponse<?> testEcho(@RequestBody Map<String, Object> request) {
        String message = (String) request.getOrDefault("message", "");
        log.info("테스트 에코 요청 - 메시지: {}", message);

        Map<String, Object> response = new HashMap<>();
        response.put("response", message);
        response.put("estimatedResponseTime", 1);

        return ApiResponse.success(response);
    }

    /**
     * 테스트용 API - 면접 시작 (에코)
     */
    @PostMapping("/test/interview/start")
    public ApiResponse<?> testStartInterview(@RequestBody InterviewSettings settings) {
        log.info("테스트 면접 시작 요청 - 직무: {}, 난이도: {}", settings.getJobRole(), settings.getDifficulty());

        // 모델 값이 "string"인 경우 기본값으로 대체
        if ("string".equals(settings.getModel())) {
            settings.setModel("gpt-3.5-turbo");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("response", "안녕하세요, 저는 면접관입니다. " +
                settings.getJobRole() + " 직무에 지원하셨군요. 간단한 자기소개 부탁드립니다.");
        response.put("estimatedResponseTime", 1);

        return ApiResponse.success(response);
    }

    /**
     * 테스트용 API - 메시지 전송 (에코)
     */
    @PostMapping("/test/interview/message")
    public ApiResponse<?> testSendMessage(@RequestBody Map<String, Object> request) {
        String message = (String) request.get("message");

        log.info("테스트 메시지 전송 요청 - 메시지: {}", message);

        Map<String, Object> response = new HashMap<>();
        response.put("response", "당신이 보낸 메시지: " + message);
        response.put("estimatedResponseTime", 1);

        return ApiResponse.success(response);
    }

    /**
     * 테스트용 API - 면접 요약 생성 (에코)
     */
    @PostMapping("/test/interview/summary")
    public ApiResponse<?> testGenerateInterviewSummary(@RequestBody Map<String, Object> request) {
        @SuppressWarnings("unchecked")
        List<Map<String, String>> messages = (List<Map<String, String>>) request.get("messages");

        log.info("테스트 면접 요약 생성 요청 - 메시지 수: {}", messages.size());

        // 모든 메시지의 content를 연결하여 요약 생성
        StringBuilder summaryBuilder = new StringBuilder("면접 요약:\n\n");
        for (Map<String, String> message : messages) {
            if (message.containsKey("role") && message.containsKey("content")) {
                String role = message.get("role");
                String content = message.get("content");

                if (!"system".equals(role)) { // 시스템 메시지는 제외
                    String roleName = "user".equals(role) ? "지원자" : "면접관";
                    summaryBuilder.append(roleName).append(": ").append(content).append("\n\n");
                }
            }
        }

        return ApiResponse.success(Map.of("summary", summaryBuilder.toString()));
    }

    /**
     * 면접 평가 생성 - 대화 이력을 바탕으로 상세 평가 결과 생성
     */
    @PostMapping("/interview/evaluate")
    public ApiResponse<?> evaluateInterview(@RequestBody Map<String, Object> request) {
        @SuppressWarnings("unchecked")
        List<Map<String, String>> messages = (List<Map<String, String>>) request.get("messages");
        InterviewSettings settings = InterviewSettings.createDefault();

        // 설정값이 있으면 적용
        if (request.containsKey("settings")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> settingsMap = (Map<String, Object>) request.get("settings");
            settings = objectMapper.convertValue(settingsMap, InterviewSettings.class);
        }

        log.info("면접 평가 생성 요청 - 메시지 수: {}, 직무: {}", messages.size(),
                settings.getJobRole() != null ? settings.getJobRole() : "미지정");

        Map<String, Object> evaluation = openAIService.generateInterviewEvaluation(messages, settings);
        return ApiResponse.success(evaluation);
    }
}