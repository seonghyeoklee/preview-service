package com.evawova.preview.domain.ai.service;

import com.evawova.preview.domain.interview.dto.InterviewSettings;
import com.evawova.preview.domain.interview.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class OpenAIServiceTest {

    @Mock
    private AIInterviewPromptService interviewPromptService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private OpenAIService openAIService;

    private InterviewSettings testSettings;

    @BeforeEach
    void setUp() {
        // API 키와 모델 설정
        ReflectionTestUtils.setField(openAIService, "apiKey", "test-api-key");
        ReflectionTestUtils.setField(openAIService, "defaultModel", "gpt-3.5-turbo");

        // 테스트용 면접 설정 초기화
        testSettings = new InterviewSettings();
        testSettings.setJobRole(JobRole.BACKEND_DEVELOPER);
        testSettings.setType(InterviewType.DEVELOPMENT);
        testSettings.setInterviewerStyle(InterviewerStyle.TECHNICAL);
        testSettings.setDifficulty(InterviewDifficulty.INTERMEDIATE);
        testSettings.setExperienceLevel(ExperienceLevel.MID_LEVEL);
        testSettings.setTechnicalSkills(List.of("Java", "Spring", "SQL"));
        testSettings.setModel("gpt-3.5-turbo");
    }

    @Test
    @DisplayName("응답 시간 추정 테스트")
    void estimateResponseTime_Test() {
        // when & then
        assertThat(openAIService.estimateResponseTime("gpt-3.5-turbo", 300)).isEqualTo(5);
        assertThat(openAIService.estimateResponseTime("gpt-3.5-turbo", 600)).isEqualTo(7);
        assertThat(openAIService.estimateResponseTime("gpt-4", 300)).isEqualTo(8);
        assertThat(openAIService.estimateResponseTime("unknown-model", 300)).isEqualTo(5);
    }

    @Test
    @DisplayName("면접 시작 - 스파이 테스트")
    void startInterview_WithSpy_Test() {
        // given
        OpenAIService spyService = spy(openAIService);

        // 실제 메서드 호출을 피하기 위해 doAnswer 사용
        doAnswer(invocation -> {
            return Map.of(
                    "response", "이것은 모의 응답입니다.",
                    "estimatedResponseTime", 5);
        }).when(spyService).startInterview(any(InterviewSettings.class));

        // when
        Map<String, Object> response = spyService.startInterview(testSettings);

        // then
        assertThat(response).isNotNull();
        assertThat(response.get("response")).isEqualTo("이것은 모의 응답입니다.");
        assertThat(response.get("estimatedResponseTime")).isEqualTo(5);
    }

    @Test
    @DisplayName("메시지 전송 - 스파이 테스트")
    void sendMessage_WithSpy_Test() {
        // given
        OpenAIService spyService = spy(openAIService);

        List<Map<String, String>> conversationHistory = new ArrayList<>();
        conversationHistory.add(Map.of("role", "system", "content", "시스템 메시지"));

        // 실제 메서드 호출을 피하기 위해 doAnswer 사용
        doAnswer(invocation -> {
            return Map.of(
                    "response", "이것은 모의 응답입니다.",
                    "estimatedResponseTime", 5);
        }).when(spyService).sendMessage(anyString(), anyList());

        // when
        Map<String, Object> response = spyService.sendMessage("테스트 메시지", conversationHistory);

        // then
        assertThat(response).isNotNull();
        assertThat(response.get("response")).isEqualTo("이것은 모의 응답입니다.");
        assertThat(response.get("estimatedResponseTime")).isEqualTo(5);
    }

    @Test
    @DisplayName("면접 요약 생성 - 스파이 테스트")
    void generateInterviewSummary_WithSpy_Test() {
        // given
        OpenAIService spyService = spy(openAIService);

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "user", "content", "사용자 질문"));

        // 실제 메서드 호출을 피하기 위해 doAnswer 사용
        doAnswer(invocation -> "이것은 모의 요약입니다.").when(spyService)
                .generateInterviewSummary(anyList(), any(InterviewSettings.class));

        // when
        String summary = spyService.generateInterviewSummary(messages, testSettings);

        // then
        assertThat(summary).isEqualTo("이것은 모의 요약입니다.");
    }

    @Test
    @DisplayName("API 호출 실패 시 예외 처리 테스트")
    void apiCallFailure_Test() {
        // given
        OpenAIService spyService = spy(openAIService);
        doThrow(new RuntimeException("API 오류")).when(spyService).startInterview(any(InterviewSettings.class));

        // when & then
        assertThatThrownBy(() -> spyService.startInterview(testSettings))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("API 오류");
    }
}