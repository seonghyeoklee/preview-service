package com.evawova.preview.domain.interview.service;

import com.evawova.preview.domain.interview.dto.InterviewPromptDto;
import com.evawova.preview.domain.interview.dto.InterviewSettings;
import com.evawova.preview.domain.interview.dto.PromptResponse;
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
        // 영문 플레이스홀더 처리
        String replaced = content
                .replace("${type}", settings.getType() != null ? settings.getType().getDisplayName() : "")
                .replace("${jobRole}", settings.getJobRole() != null ? settings.getJobRole().getDisplayName() : "")
                .replace("${interviewerStyle}",
                        settings.getInterviewerStyle() != null ? settings.getInterviewerStyle().getDisplayName() : "")
                .replace("${difficulty}",
                        settings.getDifficulty() != null ? settings.getDifficulty().getDisplayName() : "")
                .replace("${duration}", settings.getDuration() != null ? settings.getDuration().getDisplayName() : "")
                .replace("${interviewMode}",
                        settings.getInterviewMode() != null ? settings.getInterviewMode().getDisplayName() : "")
                .replace("${experienceLevel}",
                        settings.getExperienceLevel() != null ? settings.getExperienceLevel().getDisplayName() : "")
                .replace("${language}", settings.getLanguage() != null ? settings.getLanguage().getDisplayName() : "")
                .replace("${technicalSkills}",
                        settings.getTechnicalSkills() != null ? String.join(", ", settings.getTechnicalSkills()) : "");

        // 한글 플레이스홀더 처리 추가
        replaced = replaced
                .replace("{{면접_유형}}", settings.getType() != null ? settings.getType().getDisplayName() : "")
                .replace("{{직무}}", settings.getJobRole() != null ? settings.getJobRole().getDisplayName() : "")
                .replace("{{면접관_스타일}}",
                        settings.getInterviewerStyle() != null ? settings.getInterviewerStyle().getDisplayName() : "")
                .replace("{{난이도}}",
                        settings.getDifficulty() != null ? settings.getDifficulty().getDisplayName() : "")
                .replace("{{면접_시간}}", settings.getDuration() != null ? settings.getDuration().getDisplayName() : "")
                .replace("{{면접_모드}}",
                        settings.getInterviewMode() != null ? settings.getInterviewMode().getDisplayName() : "")
                .replace("{{경력_수준}}",
                        settings.getExperienceLevel() != null ? settings.getExperienceLevel().getDisplayName() : "")
                .replace("{{언어}}", settings.getLanguage() != null ? settings.getLanguage().getDisplayName() : "")
                .replace("{{기술_스택}}",
                        settings.getTechnicalSkills() != null ? String.join(", ", settings.getTechnicalSkills()) : "");

        return replaced;
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
                "languages", InterviewLanguage.values());
    }

    /**
     * 모든 프롬프트를 조회합니다.
     */
    public List<PromptResponse> getAllPrompts() {
        return promptRepository.findAll().stream()
                .map(PromptResponse::fromWithoutChildren)
                .collect(Collectors.toList());
    }

    /**
     * 대분류 프롬프트만 조회합니다.
     */
    public List<PromptResponse> getMainPrompts() {
        return promptRepository.findMainPrompts().stream()
                .map(PromptResponse::fromWithoutChildren)
                .collect(Collectors.toList());
    }

    /**
     * 특정 부모 프롬프트의 하위 프롬프트를 조회합니다.
     */
    public List<PromptResponse> getSubPrompts(Long parentId) {
        return promptRepository.findSubPromptsByParentId(parentId).stream()
                .map(PromptResponse::fromWithoutChildren)
                .collect(Collectors.toList());
    }

    /**
     * 프롬프트와 그 하위 프롬프트를 함께 조회합니다.
     */
    public PromptResponse getPromptWithChildren(Long id) {
        InterviewPrompt prompt = promptRepository.findByIdWithChildren(id)
                .orElseThrow(() -> new IllegalArgumentException("프롬프트를 찾을 수 없습니다: " + id));
        return PromptResponse.from(prompt);
    }

    /**
     * 특정 카테고리의 프롬프트를 조회합니다.
     */
    public List<PromptResponse> getPromptsByCategory(PromptCategory category) {
        return promptRepository.findByCategoryAndActive(category, true).stream()
                .map(PromptResponse::fromWithoutChildren)
                .collect(Collectors.toList());
    }

    /**
     * 새로운 프롬프트를 생성합니다.
     */
    @Transactional
    public PromptResponse createPrompt(InterviewPromptDto promptDto) {
        // Builder 타입을 사용하지 않고 필요한 모든 속성을 직접 설정
        InterviewPrompt prompt = InterviewPrompt.builder()
                .name(promptDto.getName())
                .category(promptDto.getCategory())
                .content(promptDto.getContent())
                .active(promptDto.isActive())
                .level(promptDto.getLevel() != null ? promptDto.getLevel() : 1)
                .build();

        // 부모 프롬프트가 있는 경우 연결
        if (promptDto.getParentId() != null) {
            InterviewPrompt parent = promptRepository.findById(promptDto.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("부모 프롬프트를 찾을 수 없습니다: " + promptDto.getParentId()));

            // 부모에 자식 추가 (양방향 관계 설정)
            parent.addChild(prompt);

            // 저장
            promptRepository.save(parent);
        } else {
            // 부모가 없는 경우 바로 저장
            promptRepository.save(prompt);
        }

        return PromptResponse.from(prompt);
    }

    /**
     * 프롬프트를 수정합니다.
     */
    @Transactional
    public PromptResponse updatePrompt(Long id, InterviewPromptDto promptDto) {
        InterviewPrompt prompt = promptRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("프롬프트를 찾을 수 없습니다: " + id));

        prompt.update(
                promptDto.getName(),
                promptDto.getContent(),
                promptDto.isActive());

        return PromptResponse.from(promptRepository.save(prompt));
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

    /**
     * 자식 프롬프트를 추가합니다.
     */
    @Transactional
    public PromptResponse addChildPrompt(Long parentId, InterviewPromptDto childDto) {
        InterviewPrompt parent = promptRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("부모 프롬프트를 찾을 수 없습니다: " + parentId));

        InterviewPrompt child = InterviewPrompt.builder()
                .name(childDto.getName())
                .category(childDto.getCategory())
                .content(childDto.getContent())
                .active(childDto.isActive())
                .parent(parent)
                .level(parent.getLevel() + 1)
                .build();

        parent.addChild(child);
        promptRepository.save(parent);

        return PromptResponse.from(child);
    }
}