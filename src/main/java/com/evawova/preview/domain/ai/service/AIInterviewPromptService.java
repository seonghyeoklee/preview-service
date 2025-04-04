package com.evawova.preview.domain.ai.service;

import com.evawova.preview.domain.interview.dto.InterviewSettings;
import com.evawova.preview.domain.interview.entity.Skill;
import com.evawova.preview.domain.interview.model.*;
import com.evawova.preview.domain.interview.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 면접 프롬프트 생성 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AIInterviewPromptService {

    private final SkillRepository skillRepository;

    /**
     * 면접 설정을 기반으로 OpenAI 시스템 프롬프트 생성
     */
    public String generateInterviewPrompt(InterviewSettings settings) {
        StringBuilder promptBuilder = new StringBuilder();

        // 기본 역할 설정
        promptBuilder.append("당신은 전문적인 면접관입니다. 다음의 세부 설정에 따라 면접을 진행해주세요:\n\n");

        // 직무 및 직군 정보 추가
        if (settings.getJobRole() != null) {
            promptBuilder.append("## 직무 정보\n");
            promptBuilder.append("- 직무: ").append(getJobRoleDisplayName(settings.getJobRole())).append("\n");
            promptBuilder.append("- 직군: ").append(getInterviewTypeDisplayName(settings.getType())).append("\n\n");

            // 직무별 특화 지침 추가
            promptBuilder.append(getJobRoleSpecificGuidelines(settings.getJobRole())).append("\n\n");
        }

        // 면접관 스타일 설정
        if (settings.getInterviewerStyle() != null) {
            promptBuilder.append("## 면접관 스타일\n");
            promptBuilder.append("- 스타일: ").append(getInterviewerStyleDisplayName(settings.getInterviewerStyle()))
                    .append("\n");
            promptBuilder.append(getInterviewerStyleGuidelines(settings.getInterviewerStyle())).append("\n\n");
        }

        // 난이도 설정
        if (settings.getDifficulty() != null) {
            promptBuilder.append("## 난이도 설정\n");
            promptBuilder.append("- 난이도: ").append(getDifficultyDisplayName(settings.getDifficulty())).append("\n");
            promptBuilder.append(getDifficultyGuidelines(settings.getDifficulty())).append("\n\n");
        }

        // 경력 수준 설정
        if (settings.getExperienceLevel() != null) {
            promptBuilder.append("## 경력 수준\n");
            promptBuilder.append("- 경력: ").append(getExperienceLevelDisplayName(settings.getExperienceLevel()))
                    .append("\n");
            promptBuilder.append(getExperienceLevelGuidelines(settings.getExperienceLevel())).append("\n\n");
        }

        // 기술 스택 정보 추가
        if (settings.getTechnicalSkills() != null && !settings.getTechnicalSkills().isEmpty()) {
            promptBuilder.append("## 기술 스택 정보\n");
            promptBuilder.append("다음 기술 스택에 대해 질문해주세요:\n");
            for (String skill : settings.getTechnicalSkills()) {
                promptBuilder.append("- ").append(skill).append("\n");
            }
            promptBuilder.append("\n");
        }

        // 면접 시간 설정
        if (settings.getDuration() != null) {
            promptBuilder.append("## 면접 시간\n");
            promptBuilder.append("- 면접 시간: ").append(getDurationDisplayName(settings.getDuration())).append("\n\n");
        }

        // 언어 설정
        if (settings.getLanguage() != null) {
            promptBuilder.append("## 면접 언어\n");
            promptBuilder.append("- 언어: ").append(getLanguageDisplayName(settings.getLanguage())).append("\n");
            promptBuilder.append("면접 전체를 해당 언어로 진행해주세요.\n\n");
        }

        // 면접 지침 추가
        promptBuilder.append("## 면접 지침\n");
        promptBuilder.append("1. 면접자의 답변을 주의 깊게 듣고, 관련된 후속 질문을 해주세요.\n");
        promptBuilder.append("2. 답변이 불완전하거나 모호한 경우, 구체적인 예시나 설명을 요청해주세요.\n");
        promptBuilder.append("3. 면접자의 경험과 기술을 정확하게 평가해주세요.\n");
        promptBuilder.append("4. 전문적이고 객관적인 태도를 유지해주세요.\n");
        promptBuilder.append("5. 첫 질문은 간략한 자기소개와 함께 기본적인 경력이나 관심사에 대해 물어보세요.\n");
        promptBuilder.append("6. 면접 종료 시점에는 지원자에게 질문이 있는지 물어봐주세요.\n");

        return promptBuilder.toString();
    }

    /**
     * 직무별 특화 지침 가져오기
     */
    private String getJobRoleSpecificGuidelines(JobRole role) {
        if (role == null) {
            return "- 일반적인 직무 역량과 관련된 질문을 해주세요.";
        }

        return switch (role) {
            case FRONTEND_DEVELOPER ->
                "- UI/UX 구현 능력, JavaScript/TypeScript 이해도, 프론트엔드 프레임워크(React, Vue.js, Angular 등) 활용 경험을 확인하세요.\n" +
                        "- 반응형 디자인, 웹 성능 최적화, 상태 관리 방법에 대해 질문하세요.\n" +
                        "- 브라우저 호환성 이슈 해결 경험, 웹 접근성 고려 방식을 확인하세요.";

            case BACKEND_DEVELOPER ->
                "- 서버 아키텍처, 데이터베이스 설계 및 최적화, API 개발 경험을 확인하세요.\n" +
                        "- 확장 가능한 시스템 설계, 캐싱 전략, 비동기 처리 방식에 대해 질문하세요.\n" +
                        "- 보안 관련 지식, 성능 튜닝, 트랜잭션 관리 경험을 확인하세요.";

            case FULLSTACK_DEVELOPER ->
                "- 프론트엔드와 백엔드 개발 모두에 대한 균형 잡힌 지식을 확인하세요.\n" +
                        "- 전체 시스템 아키텍처 설계 경험, 다양한 기술 스택 활용 능력을 평가하세요.\n" +
                        "- 프론트엔드-백엔드 통합 과정에서의 문제 해결 경험을 확인하세요.";

            case MOBILE_DEVELOPER ->
                "- 모바일 앱 개발 경험, 네이티브/하이브리드 개발 이해도를 확인하세요.\n" +
                        "- UI/UX 최적화, 성능 관리, 오프라인 모드 지원 방식에 대해 질문하세요.\n" +
                        "- 앱 스토어 배포 경험, 버전 관리, 사용자 피드백 반영 방식을 확인하세요.";

            case DEVOPS_DEVELOPER ->
                "- CI/CD 파이프라인 구축 경험, 컨테이너화 및 오케스트레이션 지식을 확인하세요.\n" +
                        "- 클라우드 인프라 관리, 모니터링 시스템 구축, 장애 대응 방식에 대해 질문하세요.\n" +
                        "- 자동화 스크립트 작성 능력, 보안 관련 지식을 확인하세요.";

            case DATA_SCIENTIST ->
                "- 데이터 분석 방법론, 통계적 모델링, 머신러닝 알고리즘 이해도를 확인하세요.\n" +
                        "- 데이터 전처리, 특성 추출, 모델 평가 방식에 대해 질문하세요.\n" +
                        "- 실제 비즈니스 문제 해결을 위한 데이터 활용 경험을 확인하세요.";

            case AI_ENGINEER ->
                "- 머신러닝/딥러닝 모델 개발 경험, 알고리즘 최적화 능력을 확인하세요.\n" +
                        "- 모델 학습 및 배포 과정, MLOps 관련 지식에 대해 질문하세요.\n" +
                        "- 최신 AI 트렌드 이해도, 실제 프로덕션 환경에서의 AI 구현 경험을 확인하세요.";

            default ->
                "- 해당 직무에 필요한 핵심 역량과 기술적 지식을 확인하세요.\n" +
                        "- 실무 경험과 문제 해결 방식에 대해 질문하세요.\n" +
                        "- 팀 협업 방식과 커뮤니케이션 능력을 평가하세요.";
        };
    }

    /**
     * 기술 스택 목록 가져오기
     */
    public List<String> getAvailableSkills() {
        return skillRepository.findAll().stream()
                .map(Skill::getName)
                .collect(Collectors.toList());
    }

    /**
     * JobRole 표시 이름 가져오기
     */
    private String getJobRoleDisplayName(JobRole role) {
        if (role == null)
            return "직무 미지정";

        return switch (role) {
            case FRONTEND_DEVELOPER -> "프론트엔드 개발자";
            case BACKEND_DEVELOPER -> "백엔드 개발자";
            case FULLSTACK_DEVELOPER -> "풀스택 개발자";
            case MOBILE_DEVELOPER -> "모바일 개발자";
            case DEVOPS_DEVELOPER -> "DevOps 엔지니어";
            case DATA_SCIENTIST -> "데이터 사이언티스트";
            case AI_ENGINEER -> "AI/ML 엔지니어";
            case SECURITY_ENGINEER -> "보안 엔지니어";
            case QA_ENGINEER -> "QA 엔지니어";
            case UI_UX_DESIGNER -> "UI/UX 디자이너";
            case GRAPHIC_DESIGNER -> "그래픽 디자이너";
            case PRODUCT_DESIGNER -> "제품 디자이너";
            case BRAND_DESIGNER -> "브랜드 디자이너";
            case DIGITAL_MARKETER -> "디지털 마케터";
            case CONTENT_MARKETER -> "콘텐츠 마케터";
            case BRAND_MARKETER -> "브랜드 마케터";
            case GROWTH_HACKER -> "그로스 해커";
            case HR_MANAGER -> "인사 담당자";
            case FINANCE_MANAGER -> "재무 담당자";
            case BUSINESS_DEVELOPMENT -> "사업 개발자";
            case PROJECT_MANAGER -> "프로젝트 관리자";
        };
    }

    /**
     * InterviewType 표시 이름 가져오기
     */
    private String getInterviewTypeDisplayName(InterviewType type) {
        if (type == null)
            return "일반 면접";

        return switch (type) {
            case DEVELOPMENT -> "개발";
            case DESIGN -> "디자인";
            case MARKETING -> "마케팅";
            case BUSINESS -> "경영지원";
            case SALES -> "영업/세일즈";
            case CUSTOMER_SERVICE -> "고객 지원";
            case MEDIA -> "미디어/콘텐츠";
            case EDUCATION -> "교육";
            case LOGISTICS -> "물류/유통";
        };
    }

    /**
     * InterviewerStyle 표시 이름 가져오기
     */
    private String getInterviewerStyleDisplayName(InterviewerStyle style) {
        if (style == null)
            return "기본 스타일";

        return switch (style) {
            case FRIENDLY -> "친근한 면접관";
            case TECHNICAL -> "기술 중심 면접관";
            case CHALLENGING -> "도전적인 면접관";
        };
    }

    /**
     * InterviewerStyle 가이드라인 가져오기
     */
    private String getInterviewerStyleGuidelines(InterviewerStyle style) {
        return switch (style) {
            case FRIENDLY ->
                "- 지원자가 편안하게 느낄 수 있도록 대화하듯 면접을 진행하세요.\n- 긍정적인 피드백을 적절히 제공하고, 지원자의 긴장을 풀어주세요.\n- 하지만 평가의 객관성은 유지해주세요.";
            case TECHNICAL ->
                "- 깊이 있는 기술적 질문을 통해 지원자의 기술 역량을 평가하세요.\n- 기본 개념부터 심화 개념까지 단계적으로 질문하세요.\n- 실제 문제 해결 능력을 확인할 수 있는 질문을 포함하세요.";
            case CHALLENGING ->
                "- 지원자에게 도전적인 질문을 통해 대처 능력과 문제 해결 방식을 평가하세요.\n- 압박 상황에서의 대응과 사고 과정을 관찰하세요.\n- 개방형 질문과 가정 상황에 대한 질문을 포함하세요.";
        };
    }

    /**
     * InterviewDifficulty 표시 이름 가져오기
     */
    private String getDifficultyDisplayName(InterviewDifficulty difficulty) {
        if (difficulty == null)
            return "중급";

        return switch (difficulty) {
            case BEGINNER -> "초급";
            case INTERMEDIATE -> "중급";
            case ADVANCED -> "고급";
            case EXPERT -> "전문가";
        };
    }

    /**
     * InterviewDifficulty 가이드라인 가져오기
     */
    private String getDifficultyGuidelines(InterviewDifficulty difficulty) {
        return switch (difficulty) {
            case BEGINNER -> "- 기본적인 개념과 용어에 대한 이해를 확인하세요.\n- 실무 경험이 없더라도 대답할 수 있는 질문을 주로 하세요.";
            case INTERMEDIATE -> "- 실무에서 자주 사용되는 기술과 방법론에 대해 질문하세요.\n- 간단한 문제 해결 능력을 평가하세요.";
            case ADVANCED -> "- 복잡한 문제 해결 능력과 심화된 기술 지식을 확인하세요.\n- 설계와 아키텍처 관련 질문도 포함하세요.";
            case EXPERT ->
                "- 최신 기술 트렌드와 심층적인 기술 지식을 확인하세요.\n- 복잡한 시스템 설계와 최적화 관련 질문을 포함하세요.\n- 기술적 의사결정 과정과 리더십 관련 질문도 해보세요.";
        };
    }

    /**
     * ExperienceLevel 표시 이름 가져오기
     */
    private String getExperienceLevelDisplayName(ExperienceLevel level) {
        if (level == null)
            return "경력 미지정";

        return switch (level) {
            case ENTRY -> "신입";
            case JUNIOR -> "주니어 (1-3년)";
            case MID_LEVEL -> "미드레벨 (4-7년)";
            case SENIOR -> "시니어 (8년 이상)";
            case EXECUTIVE -> "임원급";
        };
    }

    /**
     * ExperienceLevel 가이드라인 가져오기
     */
    private String getExperienceLevelGuidelines(ExperienceLevel level) {
        return switch (level) {
            case ENTRY -> "- 학습 능력과 잠재력을 중점적으로 평가하세요.\n- 기본 개념에 대한 이해도를 확인하세요.\n- 완벽한 답변보다는 사고 과정을 중요하게 봐주세요.";
            case JUNIOR ->
                "- 기본적인 실무 경험과 역량을 확인하세요.\n- 업무에 필요한 핵심 기술에 대한 이해도를 평가하세요.\n- 팀 내에서의 협업 경험과 코드 품질에 대한 인식을 확인하세요.";
            case MID_LEVEL ->
                "- 복잡한 문제 해결 경험과 독립적인 업무 수행 능력을 확인하세요.\n- 프로젝트 관리 및 팀 협업 능력을 평가하세요.\n- 기술적 의사결정 과정과 그 이유에 대해 질문하세요.";
            case SENIOR ->
                "- 심층적인 기술 지식과 리더십 경험을 확인하세요.\n- 아키텍처 설계 및 기술적 의사결정 능력을 평가하세요.\n- 주니어 개발자 멘토링과 팀 성장 기여도를 확인하세요.";
            case EXECUTIVE ->
                "- 전략적 사고와 비즈니스 이해도를 확인하세요.\n- 조직 관리 및 리더십 역량을 중점적으로 평가하세요.\n- 기술 비전 수립 및 의사결정 능력을 확인하세요.";
        };
    }

    /**
     * InterviewDuration 표시 이름 가져오기
     */
    private String getDurationDisplayName(InterviewDuration duration) {
        if (duration == null)
            return "30분";

        return switch (duration) {
            case SHORT -> "15분";
            case MEDIUM -> "30분";
            case LONG -> "45분";
            case EXTENDED -> "60분";
        };
    }

    /**
     * InterviewLanguage 표시 이름 가져오기
     */
    private String getLanguageDisplayName(InterviewLanguage language) {
        if (language == null)
            return "한국어";

        return switch (language) {
            case KO -> "한국어";
            case EN -> "영어 (English)";
        };
    }
}