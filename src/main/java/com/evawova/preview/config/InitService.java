package com.evawova.preview.config;

import com.evawova.preview.domain.interview.entity.InterviewPrompt;
import com.evawova.preview.domain.interview.model.*;
import com.evawova.preview.domain.interview.repository.InterviewPromptRepository;
import com.evawova.preview.domain.user.service.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InitService {

    private final UserService userService;
    private final InterviewPromptRepository interviewPromptRepository;

    @PostConstruct
    @Transactional
    public void init() {
        log.info("초기 데이터 설정 시작...");
        
        // 사용자 역할 마이그레이션
        log.info("사용자 역할 마이그레이션 시작...");
        userService.migrateUserRoles();
        log.info("사용자 역할 마이그레이션 완료");

        // 프롬프트 데이터 초기화
        log.info("프롬프트 데이터 초기화 시작...");
        initializePrompts();
        log.info("프롬프트 데이터 초기화 완료");
    }

    private void initializePrompts() {
        if (interviewPromptRepository.count() > 0) {
            log.info("프롬프트 데이터가 이미 존재합니다. 초기화를 건너뜁니다.");
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        List<InterviewPrompt> prompts = new ArrayList<>();

        // 기본 프롬프트
        prompts.add(InterviewPrompt.builder()
            .name("기본 프롬프트")
            .category(PromptCategory.BASIC)
            .content("당신은 전문적인 면접관입니다. 다음 지침을 따라 면접을 진행해주세요:\n\n" +
                "1. 면접자의 답변을 주의 깊게 듣고, 관련된 후속 질문을 해주세요.\n" +
                "2. 답변이 불완전하거나 모호한 경우, 구체적인 예시나 설명을 요청해주세요.\n" +
                "3. 면접자의 경험과 기술을 정확하게 평가해주세요.\n" +
                "4. 전문적이고 객관적인 태도를 유지해주세요.\n" +
                "5. 면접자의 긴장감을 완화하고 편안한 분위기를 만들어주세요.")
            .active(true)
            .createdAt(now)
            .updatedAt(now)
            .build());

        // 인터뷰어 스타일
        for (InterviewerStyle style : InterviewerStyle.values()) {
            String content = "";
            switch (style) {
                case FRIENDLY:
                    content = "당신은 {{면접관_스타일}}입니다. 면접자의 긴장감을 완화하고 편안한 분위기에서 면접을 진행해주세요. 긍정적인 피드백을 자주 제공하고, 대화하듯이 질문해주세요.";
                    break;
                case TECHNICAL:
                    content = "당신은 {{면접관_스타일}}입니다. 깊이 있는 기술적 질문과 함께 면접자의 기술적 역량을 정확하게 평가해주세요. 구체적인 기술 개념과 실무 경험에 대해 심층적으로 질문해주세요.";
                    break;
                case CHALLENGING:
                    content = "당신은 {{면접관_스타일}}입니다. 까다로운 질문과 압박 상황을 통해 면접자의 문제 해결 능력과 스트레스 대처 능력을 평가해주세요. 예상치 못한 질문과 실시간 문제 해결을 요구해주세요.";
                    break;
            }
            
            prompts.add(InterviewPrompt.builder()
                .name(style.getDisplayName())
                .category(PromptCategory.INTERVIEWER_STYLE)
                .content(content)
                .active(true)
                .createdAt(now)
                .updatedAt(now)
                .build());
        }

        // 직무 정보
        for (JobRole role : JobRole.values()) {
            String content = "{{직무}} 포지션에 대한 면접을 진행합니다. ";
            
            // 직무별 내용 추가
            if (role.name().contains("DEVELOPER") || role.name().contains("ENGINEER")) {
                content += "기술적인 역량과 개발 경험에 중점을 두고 평가해주세요.";
            } else if (role.name().contains("DESIGNER")) {
                content += "디자인 철학, 포트폴리오, 사용자 경험에 중점을 두고 평가해주세요.";
            } else if (role.name().contains("MARKETER")) {
                content += "마케팅 전략, 캠페인 경험, 성과 측정 능력에 중점을 두고 평가해주세요.";
            } else {
                content += "관련 업무 경험과 전문성에 중점을 두고 평가해주세요.";
            }
            
            prompts.add(InterviewPrompt.builder()
                .name(role.getDisplayName())
                .category(PromptCategory.JOB_INFO)
                .content(content)
                .active(true)
                .createdAt(now)
                .updatedAt(now)
                .build());
        }

        // 경험 및 스킬
        for (ExperienceLevel level : ExperienceLevel.values()) {
            String content = "";
            switch (level) {
                case ENTRY:
                    content = "{{경력_수준}}으로서의 기본적인 지식과 학습 능력에 대해 평가해주세요. 교육 배경, 프로젝트 경험, 기초 기술에 중점을 두고 질문해주세요.";
                    break;
                case JUNIOR:
                    content = "{{경력_수준}}으로서의 실무 경험과 기술 적용 능력에 대해 평가해주세요. 간단한 문제 해결 능력과, 팀 협업 경험에 중점을 두고 질문해주세요.";
                    break;
                case MID_LEVEL:
                    content = "{{경력_수준}}으로서의 심화된 기술 지식과 프로젝트 경험에 대해 평가해주세요. 복잡한 문제 해결 능력과 주도적인 업무 수행 경험에 중점을 두고 질문해주세요.";
                    break;
                case SENIOR:
                    content = "{{경력_수준}}으로서의 깊은 전문성과 리더십 경험에 대해 평가해주세요. 아키텍처 설계, 기술 의사결정, 팀 멘토링 경험에 중점을 두고 질문해주세요.";
                    break;
                case EXECUTIVE:
                    content = "{{경력_수준}}으로서의 전략적 사고와 조직 관리 능력에 대해 평가해주세요. 비즈니스 이해도, 의사결정 프로세스, 조직 성장에 대한 비전에 중점을 두고 질문해주세요.";
                    break;
            }
            
            prompts.add(InterviewPrompt.builder()
                .name(level.getDisplayName())
                .category(PromptCategory.EXPERIENCE_SKILLS)
                .content(content)
                .active(true)
                .createdAt(now)
                .updatedAt(now)
                .build());
        }

        // 난이도 및 스타일
        for (InterviewDifficulty difficulty : InterviewDifficulty.values()) {
            String content = "";
            switch (difficulty) {
                case BEGINNER:
                    content = "{{난이도}} 수준의 면접을 진행합니다. 기초적인 개념과 지식을 확인하는 질문을 주로 하고, 면접자가 편안하게 답변할 수 있도록 안내해주세요.";
                    break;
                case INTERMEDIATE:
                    content = "{{난이도}} 수준의 면접을 진행합니다. 실무 경험과 문제 해결 능력을 확인하는 질문을 주로 하고, 구체적인 사례를 요청해주세요.";
                    break;
                case ADVANCED:
                    content = "{{난이도}} 수준의 면접을 진행합니다. 심층적인 기술 지식과 복잡한 문제 해결 능력을 확인하는 질문을 주로 하고, 다양한 상황에서의 의사결정 과정을 평가해주세요.";
                    break;
                case EXPERT:
                    content = "{{난이도}} 수준의 면접을 진행합니다. 최신 기술 트렌드에 대한 이해와 고난도 기술적 문제에 대한 접근 방식을 확인하는 질문을 주로 하고, 혁신적인 해결책을 제시할 수 있는지 평가해주세요.";
                    break;
            }
            
            prompts.add(InterviewPrompt.builder()
                .name(difficulty.getDisplayName())
                .category(PromptCategory.DIFFICULTY_STYLE)
                .content(content)
                .active(true)
                .createdAt(now)
                .updatedAt(now)
                .build());
        }

        // 시간 및 질문
        for (InterviewDuration duration : InterviewDuration.values()) {
            String content = "{{면접_시간}} 동안 면접을 진행합니다. ";
            
            switch (duration) {
                case SHORT:
                    content += "핵심적인 질문 위주로 간결하게 진행하며, 약 5-7개 정도의 주요 질문을 준비해주세요.";
                    break;
                case MEDIUM:
                    content += "중요 주제에 대해 적절한 깊이로 질문하며, 약 10-12개 정도의 다양한 질문을 준비해주세요.";
                    break;
                case LONG:
                    content += "주요 영역별로 심층적인 질문을 하며, 약 15-18개 정도의 포괄적인 질문을 준비해주세요.";
                    break;
                case EXTENDED:
                    content += "모든 역량 영역을 종합적으로 평가하는 심층 질문을 하며, 약 20개 이상의 다각적인 질문을 준비해주세요.";
                    break;
            }
            
            prompts.add(InterviewPrompt.builder()
                .name(duration.getDisplayName())
                .category(PromptCategory.TIME_QUESTIONS)
                .content(content)
                .active(true)
                .createdAt(now)
                .updatedAt(now)
                .build());
        }

        // 인터뷰 프로세스
        for (InterviewMode mode : InterviewMode.values()) {
            String content = "";
            switch (mode) {
                case TEXT:
                    content = "{{면접_모드}}로 진행합니다. 질문과 답변을 텍스트로 주고받으며, 명확하고 이해하기 쉬운 의사소통을 해주세요. 대화의 맥락을 잘 유지하며 면접을 진행해주세요.";
                    break;
                case VOICE:
                    content = "{{면접_모드}}로 진행합니다. 자연스러운 대화를 통해 면접자의 의사소통 능력과 전문성을 평가해주세요. 음성 톤과 대화 흐름을 고려하여 면접을 진행해주세요.";
                    break;
            }
            
            prompts.add(InterviewPrompt.builder()
                .name(mode.getDisplayName())
                .category(PromptCategory.INTERVIEW_PROCESS)
                .content(content)
                .active(true)
                .createdAt(now)
                .updatedAt(now)
                .build());
        }

        // 언어 설정
        for (InterviewLanguage language : InterviewLanguage.values()) {
            String content = "";
            switch (language) {
                case KO:
                    content = "면접은 {{언어}}로 진행됩니다. 면접자의 한국어 의사소통 능력을 평가하고, 전문 용어 사용의 정확성을 확인해주세요.";
                    break;
                case EN:
                    content = "면접은 {{언어}}로 진행됩니다. 면접자의 영어 의사소통 능력을 평가하고, 국제적인 업무 환경에서의 의사소통 역량을 확인해주세요.";
                    break;
            }
            
            prompts.add(InterviewPrompt.builder()
                .name(language.getDisplayName())
                .category(PromptCategory.LANGUAGE)
                .content(content)
                .active(true)
                .createdAt(now)
                .updatedAt(now)
                .build());
        }

        // 마무리 지침
        prompts.add(InterviewPrompt.builder()
            .name("마무리 지침")
            .category(PromptCategory.CLOSING)
            .content("면접이 끝나기 전에 다음 사항을 확인해주세요:\n\n" +
                "1. 면접자가 추가 질문이 있는지 확인해주세요.\n" +
                "2. 면접 결과에 대한 피드백을 제공해주세요.\n" +
                "3. 다음 단계에 대한 안내를 해주세요.\n" +
                "4. 면접자의 시간과 노력에 대해 감사를 표해주세요.")
            .active(true)
            .createdAt(now)
            .updatedAt(now)
            .build());

        interviewPromptRepository.saveAll(prompts);
        log.info("{}개의 프롬프트 데이터가 초기화되었습니다.", prompts.size());
    }
} 