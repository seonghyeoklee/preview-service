package com.evawova.preview.domain.interview.service;

import com.evawova.preview.domain.interview.dto.InterviewPromptDto;
import com.evawova.preview.domain.interview.dto.InterviewSettings;
import com.evawova.preview.domain.interview.entity.InterviewPrompt;
import com.evawova.preview.domain.interview.model.*;
import com.evawova.preview.domain.interview.repository.InterviewPromptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 면접 프롬프트 생성을 담당하는 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InterviewPromptService {

    private final InterviewPromptRepository promptRepository;

    /**
     * 면접 설정 정보를 기반으로 AI 면접관을 위한 프롬프트를 생성합니다.
     * 
     * @param settings 면접 설정 정보
     * @return 생성된 프롬프트
     */
    public String generateInterviewPrompt(InterviewSettings settings) {
        List<InterviewPrompt> prompts = promptRepository.findByActive(true);
        return prompts.stream()
                .map(prompt -> replacePlaceholders(prompt.getContent(), settings))
                .collect(Collectors.joining("\n\n"));
    }

    /**
     * 프롬프트의 플레이스홀더를 실제 값으로 대체합니다.
     */
    private String replacePlaceholders(String content, InterviewSettings settings) {
        return content
                .replace("${type}", settings.getType() != null ? settings.getType().getDisplayName() : "")
                .replace("${jobRole}", settings.getJobRole() != null ? settings.getJobRole().getDisplayName() : "")
                .replace("${interviewerStyle}", settings.getInterviewerStyle() != null ? settings.getInterviewerStyle().getDisplayName() : "")
                .replace("${difficulty}", settings.getDifficulty() != null ? settings.getDifficulty().getDisplayName() : "")
                .replace("${duration}", settings.getDuration() != null ? settings.getDuration().getDisplayName() : "")
                .replace("${interviewMode}", settings.getInterviewMode() != null ? settings.getInterviewMode().getDisplayName() : "")
                .replace("${experienceLevel}", settings.getExperienceLevel() != null ? settings.getExperienceLevel().getDisplayName() : "")
                .replace("${language}", settings.getLanguage() != null ? settings.getLanguage().getDisplayName() : "")
                .replace("${technicalSkills}", settings.getTechnicalSkills() != null ? String.join(", ", settings.getTechnicalSkills()) : "");
    }

    /**
     * 면접 설정에 필요한 메타데이터를 조회합니다.
     */
    public Map<String, Object> getMetadata() {
        return Map.of(
                "interviewTypes", InterviewType.values(),
                "jobRoles", JobRole.values(),
                "interviewerStyles", InterviewerStyle.values(),
                "difficulties", InterviewDifficulty.values(),
                "durations", InterviewDuration.values(),
                "modes", InterviewMode.values(),
                "experienceLevels", ExperienceLevel.values(),
                "languages", InterviewLanguage.values()
        );
    }

    /**
     * 모든 프롬프트를 조회합니다.
     */
    public List<InterviewPromptDto> getAllPrompts() {
        return promptRepository.findAll().stream()
                .map(InterviewPromptDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 특정 카테고리의 프롬프트를 조회합니다.
     */
    public List<InterviewPromptDto> getPromptsByCategory(String category) {
        return promptRepository.findByCategoryAndActive(category, true).stream()
                .map(InterviewPromptDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 새로운 프롬프트를 생성합니다.
     */
    @Transactional
    public InterviewPromptDto createPrompt(InterviewPromptDto promptDto) {
        InterviewPrompt prompt = InterviewPrompt.builder()
                .name(promptDto.getName())
                .category(PromptCategory.valueOf(promptDto.getCategory()))
                .content(promptDto.getContent())
                .active(promptDto.isActive())
                .build();

        return InterviewPromptDto.from(promptRepository.save(prompt));
    }

    /**
     * 프롬프트를 수정합니다.
     */
    @Transactional
    public InterviewPromptDto updatePrompt(Long id, InterviewPromptDto promptDto) {
        InterviewPrompt prompt = promptRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("프롬프트를 찾을 수 없습니다: " + id));

        prompt.update(
                promptDto.getName(),
                promptDto.getContent(),
                promptDto.isActive()
        );

        return InterviewPromptDto.from(promptRepository.save(prompt));
    }

    /**
     * 프롬프트를 비활성화합니다.
     */
    @Transactional
    public void deactivatePrompt(Long id) {
        InterviewPrompt prompt = promptRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("프롬프트를 찾을 수 없습니다: " + id));
        prompt.deactivate();
        promptRepository.save(prompt);
    }
} 