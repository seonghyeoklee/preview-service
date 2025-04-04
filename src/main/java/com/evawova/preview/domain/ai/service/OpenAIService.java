package com.evawova.preview.domain.ai.service;

import com.evawova.preview.domain.interview.dto.InterviewSettings;
import com.evawova.preview.domain.interview.service.InterviewPromptService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;

/**
 * OpenAI API를 사용하여 면접 시나리오를 생성하고 대화를 주고받는 서비스
 */
@Slf4j
@Service
public class OpenAIService {

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.model:gpt-3.5-turbo}")
    private String defaultModel;

    private final InterviewPromptService interviewPromptService;
    private final ObjectMapper objectMapper;

    // 모델별 응답 시간 추정 (초)
    private static final Map<String, Integer> responseTimeEstimates = Map.of(
            "gpt-3.5-turbo", 5,
            "gpt-4", 8,
            "gpt-4-turbo", 6);

    public OpenAIService(
            InterviewPromptService interviewPromptService,
            ObjectMapper objectMapper,
            @Value("${openai.api.key}") String apiKey,
            @Value("${openai.model:gpt-3.5-turbo}") String defaultModel) {
        this.interviewPromptService = interviewPromptService;
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
        this.defaultModel = defaultModel;
    }

    /**
     * 응답 시간 추정 (초)
     */
    public int estimateResponseTime(String model, int messageLength) {
        int baseTime = responseTimeEstimates.getOrDefault(model, 5);
        // 메시지 길이에 따라 응답 시간 조정 (긴 메시지는 응답 시간이 더 길어짐)
        if (messageLength > 500) {
            return baseTime + 2;
        } else if (messageLength > 1000) {
            return baseTime + 4;
        }
        return baseTime;
    }

    /**
     * 면접 시작 - 첫 질문 생성
     */
    public Map<String, Object> startInterview(InterviewSettings settings) {
        return startInterview(settings, null);
    }

    /**
     * 면접 시작 - 커스텀 프롬프트 지원
     * 
     * @param settings     면접 설정
     * @param customPrompt 커스텀 프롬프트 (null이 아니면 이를 사용)
     * @return 응답 결과
     */
    public Map<String, Object> startInterview(InterviewSettings settings, String customPrompt) {
        try {
            OpenAiService service = new OpenAiService(apiKey, Duration.ofSeconds(60));
            String model = settings.getModel() != null ? settings.getModel() : defaultModel;

            // "string" 값인 경우 기본값으로 대체
            if ("string".equals(model)) {
                model = defaultModel;
                log.info("모델 값이 'string'으로 설정되어 기본값({})으로 대체합니다.", defaultModel);
            }

            // 시스템 프롬프트 생성 또는 커스텀 프롬프트 사용
            String systemPrompt;
            if (customPrompt != null && !customPrompt.isEmpty()) {
                systemPrompt = customPrompt;
                log.info("커스텀 프롬프트 사용 (길이: {})", customPrompt.length());
            } else {
                systemPrompt = interviewPromptService.generateInterviewPrompt(settings);
                log.info("자동 생성된 프롬프트 사용 (길이: {})", systemPrompt.length());
            }

            List<ChatMessage> messages = new ArrayList<>();

            // 시스템 메시지 추가
            messages.add(new ChatMessage("system", systemPrompt));

            // 첫 메시지 추가 - 면접 시작 요청
            String userPrompt = "면접을 시작해주세요. 첫 질문은 간단한 자기소개와 함께 지원자의 경력이나 관심사에 대해 물어봐주세요.";
            messages.add(new ChatMessage("user", userPrompt));

            // API 요청 생성
            ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                    .model(model)
                    .messages(messages)
                    .temperature(0.7)
                    .build();

            // API 호출
            ChatCompletionResult result = service.createChatCompletion(chatCompletionRequest);
            String response = result.getChoices().get(0).getMessage().getContent();

            // 응답 시간 추정
            int estimatedResponseTime = estimateResponseTime(model, userPrompt.length());

            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("response", response);
            responseMap.put("estimatedResponseTime", estimatedResponseTime);

            return responseMap;
        } catch (Exception e) {
            log.error("OpenAI API 호출 중 오류 발생", e);
            throw new RuntimeException("면접 시작 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 메시지 전송 및 응답 생성
     */
    public Map<String, Object> sendMessage(String message, List<Map<String, String>> conversationHistory) {
        try {
            OpenAiService service = new OpenAiService(apiKey, Duration.ofSeconds(60));
            String model = defaultModel;

            // 대화 기록 변환
            List<ChatMessage> messages = new ArrayList<>();

            // 대화 기록에서 모델 정보 확인 (첫 번째 시스템 메시지에 포함되어 있을 수 있음)
            if (!conversationHistory.isEmpty() && "system".equals(conversationHistory.get(0).get("role"))) {
                // 대화 기록에서 모델 정보 추출 로직이 있다면 여기에 구현
                // 예: model =
                // extractModelFromSystemMessage(conversationHistory.get(0).get("content"));
            }

            // "string" 값인 경우 기본값으로 대체
            if ("string".equals(model)) {
                model = defaultModel;
                log.info("모델 값이 'string'으로 설정되어 기본값({})으로 대체합니다.", defaultModel);
            }

            // 대화 기록 변환
            for (Map<String, String> entry : conversationHistory) {
                String role = entry.get("role");
                String content = entry.get("content");
                messages.add(new ChatMessage(role, content));
            }

            // 사용자 메시지 추가
            messages.add(new ChatMessage("user", message));

            // API 요청 생성
            ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                    .model(model)
                    .messages(messages)
                    .temperature(0.7)
                    .build();

            // API 호출
            ChatCompletionResult result = service.createChatCompletion(chatCompletionRequest);
            String response = result.getChoices().get(0).getMessage().getContent();

            // 응답 시간 추정
            int estimatedResponseTime = estimateResponseTime(model, message.length());

            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("response", response);
            responseMap.put("estimatedResponseTime", estimatedResponseTime);

            return responseMap;
        } catch (Exception e) {
            log.error("OpenAI API 호출 중 오류 발생", e);
            throw new RuntimeException("메시지 전송 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 면접 요약 생성
     */
    public String generateInterviewSummary(List<Map<String, String>> messages, InterviewSettings settings) {
        try {
            OpenAiService service = new OpenAiService(apiKey, Duration.ofSeconds(120));
            String model = settings.getModel() != null ? settings.getModel() : defaultModel;

            // "string" 값인 경우 기본값으로 대체
            if ("string".equals(model)) {
                model = defaultModel;
                log.info("모델 값이 'string'으로 설정되어 기본값({})으로 대체합니다.", defaultModel);
            }

            List<ChatMessage> chatMessages = new ArrayList<>();

            // 시스템 메시지 추가 - 요약 생성 지침
            String systemPrompt = createSummaryPrompt(settings);
            chatMessages.add(new ChatMessage("system", systemPrompt));

            // 대화 내용을 하나의 문자열로 변환
            StringBuilder conversationText = new StringBuilder();
            for (Map<String, String> message : messages) {
                String role = message.get("role");
                String content = message.get("content");

                // 시스템 메시지는 제외
                if ("system".equals(role))
                    continue;

                String roleName = "user".equals(role) ? "지원자" : "면접관";
                conversationText.append(roleName).append(": ").append(content).append("\n\n");
            }

            // 사용자 메시지 추가 - 대화 내용
            chatMessages.add(new ChatMessage("user", "다음은 면접 대화 내용입니다. 요약해주세요:\n\n" + conversationText));

            // API 요청 생성
            ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                    .model(model)
                    .messages(chatMessages)
                    .temperature(0.3) // 요약은 더 결정적인 응답이 필요하므로 낮은 temperature 사용
                    .build();

            // API 호출
            ChatCompletionResult result = service.createChatCompletion(chatCompletionRequest);
            return result.getChoices().get(0).getMessage().getContent();

        } catch (Exception e) {
            log.error("면접 요약 생성 중 오류 발생", e);
            throw new RuntimeException("면접 요약 생성 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 요약 생성을 위한 프롬프트 생성
     */
    private String createSummaryPrompt(InterviewSettings settings) {
        StringBuilder promptBuilder = new StringBuilder();

        promptBuilder.append("당신은 프로페셔널한 면접 평가자입니다. ");
        promptBuilder.append("다음 면접 대화를 분석하고 지원자의 역량을 평가하여 종합적인 요약을 제공해주세요.\n\n");

        promptBuilder.append("## 평가 항목\n");
        promptBuilder.append("1. 기술적 역량: 지원자가 보여준 기술적 지식과 이해도를 평가해주세요.\n");
        promptBuilder.append("2. 문제 해결 능력: 지원자의 문제 접근 방식과 해결 능력을 평가해주세요.\n");
        promptBuilder.append("3. 커뮤니케이션 능력: 지원자의 의사소통 명확성과 효율성을 평가해주세요.\n");
        promptBuilder.append("4. 경험 및 프로젝트: 지원자의 과거 경험과 프로젝트에 대한 설명을 평가해주세요.\n");
        promptBuilder.append("5. 강점 및 개선점: 지원자의 주요 강점과 개선이 필요한 영역을 식별해주세요.\n\n");

        promptBuilder.append("## 응답 형식\n");
        promptBuilder.append("- 객관적이고 공정한 평가를 제공해주세요.\n");
        promptBuilder.append("- 구체적인 예시와 함께 피드백을 제공해주세요.\n");
        promptBuilder.append("- 건설적인 피드백과 개선을 위한 제안을 포함해주세요.\n\n");

        // 직무 정보가 있다면 추가
        if (settings.getJobRole() != null) {
            promptBuilder.append("## 직무 정보\n");
            promptBuilder.append("해당 면접은 다음 직무에 대한 것입니다: ").append(settings.getJobRole()).append("\n");
            promptBuilder.append("이 직무에 필요한 핵심 역량을 중심으로 평가해주세요.\n\n");
        }

        return promptBuilder.toString();
    }

    /**
     * 면접 평가 생성
     */
    public Map<String, Object> generateInterviewEvaluation(List<Map<String, String>> messages,
            InterviewSettings settings) {
        try {
            OpenAiService service = new OpenAiService(apiKey, Duration.ofSeconds(120));
            String model = settings.getModel() != null ? settings.getModel() : defaultModel;

            // "string" 값인 경우 기본값으로 대체
            if ("string".equals(model)) {
                model = defaultModel;
                log.info("모델 값이 'string'으로 설정되어 기본값({})으로 대체합니다.", defaultModel);
            }

            List<ChatMessage> chatMessages = new ArrayList<>();

            // 시스템 메시지 추가 - 평가 생성 지침
            String systemPrompt = createEvaluationPrompt(settings);
            chatMessages.add(new ChatMessage("system", systemPrompt));

            // 대화 내용을 하나의 문자열로 변환
            StringBuilder conversationText = new StringBuilder();
            for (Map<String, String> message : messages) {
                String role = message.get("role");
                String content = message.get("content");

                // 시스템 메시지는 제외
                if ("system".equals(role))
                    continue;

                String roleName = "user".equals(role) ? "지원자" : "면접관";
                conversationText.append(roleName).append(": ").append(content).append("\n\n");
            }

            // 사용자 메시지 추가 - 대화 내용
            chatMessages.add(new ChatMessage("user", "다음은 면접 대화 내용입니다. 평가해주세요:\n\n" + conversationText));

            // API 요청 생성
            ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                    .model(model)
                    .messages(chatMessages)
                    .temperature(0.3) // 평가는 더 결정적인 응답이 필요하므로 낮은 temperature 사용
                    .build();

            // API 호출
            ChatCompletionResult result = service.createChatCompletion(chatCompletionRequest);
            String evaluationText = result.getChoices().get(0).getMessage().getContent();

            // 이미 JSON 형식으로 응답했을 경우를 대비해 파싱 시도
            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> evaluationMap = objectMapper.readValue(evaluationText, Map.class);
                return evaluationMap;
            } catch (Exception e) {
                // JSON 파싱 실패 시 텍스트 응답을 그대로 반환
                log.info("구조화된 JSON 응답이 아닙니다. 텍스트 응답을 반환합니다.");
                return Map.of(
                        "evaluationText", evaluationText,
                        "isStructured", false);
            }

        } catch (Exception e) {
            log.error("면접 평가 생성 중 오류 발생", e);
            throw new RuntimeException("면접 평가 생성 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 평가 생성을 위한 프롬프트 생성
     */
    private String createEvaluationPrompt(InterviewSettings settings) {
        StringBuilder promptBuilder = new StringBuilder();

        promptBuilder.append("당신은 전문적인 면접 평가자입니다. ");
        promptBuilder.append("다음 면접 대화를 분석하고 지원자의 역량을 평가하여 구조화된 평가 결과를 제공해주세요.\n\n");

        promptBuilder.append("## 평가 항목\n");
        promptBuilder.append("1. 기술적 역량: 지원자가 보여준 기술적 지식과 이해도를 평가해주세요.\n");
        promptBuilder.append("2. 문제 해결 능력: 지원자의 문제 접근 방식과 해결 능력을 평가해주세요.\n");
        promptBuilder.append("3. 커뮤니케이션 능력: 지원자의 의사소통 명확성과 효율성을 평가해주세요.\n");
        promptBuilder.append("4. 경험 및 프로젝트: 지원자의 과거 경험과 프로젝트에 대한 설명을 평가해주세요.\n");
        promptBuilder.append("5. 문화적 적합성: 지원자의 가치관과 태도가 조직 문화에 적합한지 평가해주세요.\n");
        promptBuilder.append("6. 자기 개발: 자기계발 의지와 학습 능력을 평가해주세요.\n\n");

        // 직무 정보가 있다면 추가
        if (settings.getJobRole() != null) {
            promptBuilder.append("## 직무 정보\n");
            promptBuilder.append("평가할 직무: ").append(settings.getJobRole()).append("\n");
            promptBuilder.append("이 직무에 필요한 핵심 역량을 중심으로 평가해주세요.\n\n");
        }

        promptBuilder.append("## 응답 형식\n");
        promptBuilder.append("다음 JSON 형식으로 평가 결과를 제공해주세요:\n\n");
        promptBuilder.append("```json\n");
        promptBuilder.append("{\n");
        promptBuilder.append("  \"overallScore\": 85,  // 총점 (0-100점)\n");
        promptBuilder.append("  \"technicalSkill\": {\n");
        promptBuilder.append("    \"score\": 85,  // 점수 (0-100점)\n");
        promptBuilder.append("    \"strengths\": [\"강점1\", \"강점2\"],\n");
        promptBuilder.append("    \"weaknesses\": [\"약점1\", \"약점2\"],\n");
        promptBuilder.append("    \"comments\": \"기술적 역량에 대한 종합 의견\"\n");
        promptBuilder.append("  },\n");
        promptBuilder.append("  \"problemSolving\": {\n");
        promptBuilder.append("    \"score\": 80,\n");
        promptBuilder.append("    \"strengths\": [\"강점1\", \"강점2\"],\n");
        promptBuilder.append("    \"weaknesses\": [\"약점1\", \"약점2\"],\n");
        promptBuilder.append("    \"comments\": \"문제 해결 능력에 대한 종합 의견\"\n");
        promptBuilder.append("  },\n");
        promptBuilder.append("  \"communication\": {\n");
        promptBuilder.append("    \"score\": 90,\n");
        promptBuilder.append("    \"strengths\": [\"강점1\", \"강점2\"],\n");
        promptBuilder.append("    \"weaknesses\": [\"약점1\", \"약점2\"],\n");
        promptBuilder.append("    \"comments\": \"커뮤니케이션 능력에 대한 종합 의견\"\n");
        promptBuilder.append("  },\n");
        promptBuilder.append("  \"experience\": {\n");
        promptBuilder.append("    \"score\": 85,\n");
        promptBuilder.append("    \"relevance\": 80,  // 해당 직무 관련성 (0-100점)\n");
        promptBuilder.append("    \"comments\": \"경험에 대한 종합 의견\"\n");
        promptBuilder.append("  },\n");
        promptBuilder.append("  \"culturalFit\": {\n");
        promptBuilder.append("    \"score\": 90,\n");
        promptBuilder.append("    \"comments\": \"문화적 적합성에 대한 의견\"\n");
        promptBuilder.append("  },\n");
        promptBuilder.append("  \"overallEvaluation\": \"종합적인 평가와 채용 추천 여부\",\n");
        promptBuilder.append("  \"hiringRecommendation\": \"Strong Hire\",  // Strong Hire, Hire, Maybe, No Hire\n");
        promptBuilder.append("  \"developmentAreas\": [\"성장 필요 영역1\", \"성장 필요 영역2\"]\n");
        promptBuilder.append("}\n");
        promptBuilder.append("```\n\n");

        promptBuilder.append("평가는 면접에서 나온 구체적인 답변과 행동에 근거해야 합니다. 구체적인 예시를 통해 점수와 의견을 뒷받침해주세요.\n");
        promptBuilder.append("'hiringRecommendation'은 'Strong Hire', 'Hire', 'Maybe', 'No Hire' 중 하나로 제공해주세요.\n");

        return promptBuilder.toString();
    }
}