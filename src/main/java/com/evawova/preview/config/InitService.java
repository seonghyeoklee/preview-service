package com.evawova.preview.config;

import com.evawova.preview.domain.interview.entity.InterviewCategory;
import com.evawova.preview.domain.interview.entity.InterviewPrompt;
import com.evawova.preview.domain.interview.entity.JobPosition;
import com.evawova.preview.domain.interview.entity.Skill;
import com.evawova.preview.domain.interview.model.*;
import com.evawova.preview.domain.interview.repository.InterviewCategoryRepository;
import com.evawova.preview.domain.interview.repository.InterviewPromptRepository;
import com.evawova.preview.domain.interview.repository.JobPositionRepository;
import com.evawova.preview.domain.interview.repository.SkillRepository;
import com.evawova.preview.domain.interview.service.JobRoleSkillService;
import com.evawova.preview.domain.user.entity.Plan;
import com.evawova.preview.domain.user.entity.PlanType;
import com.evawova.preview.domain.user.repository.PlanRepository;
import com.evawova.preview.domain.user.service.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InitService {

    private final UserService userService;
    private final InterviewPromptRepository interviewPromptRepository;
    private final InterviewCategoryRepository interviewCategoryRepository;
    private final JobPositionRepository jobPositionRepository;
    private final SkillRepository skillRepository;
    private final PlanRepository planRepository;
    private final JobRoleSkillService jobRoleSkillService;

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
        initializeInterviewPrompts();
        log.info("프롬프트 데이터 초기화 완료");

        // 인터뷰 카테고리 데이터 초기화
        log.info("인터뷰 카테고리 데이터 초기화 시작...");
        initializeInterviewCategories();
        log.info("인터뷰 카테고리 데이터 초기화 완료");

        // 스킬 데이터 초기화
        log.info("스킬 데이터 초기화 시작...");
        initializeSkills();
        log.info("스킬 데이터 초기화 완료");

        // 직무 포지션 데이터 초기화
        log.info("직무 포지션 데이터 초기화 시작...");
        initializeJobPositions();
        log.info("직무 포지션 데이터 초기화 완료");

        // 플랜 데이터 초기화
        log.info("플랜 데이터 초기화 시작...");
        initializePlans();
        log.info("플랜 데이터 초기화 완료");

        // 직무별 기술 스택 초기화
        initializeJobRoleSkills();
    }

    /**
     * 면접 프롬프트 데이터 초기화
     */
    private void initializeInterviewPrompts() {
        if (interviewPromptRepository.count() > 0) {
            log.info("이미 프롬프트 데이터가 존재합니다. 초기화를 건너뜁니다.");
            return;
        }

        LocalDateTime now = LocalDateTime.now();

        // 기본 프롬프트 (최상위)
        InterviewPrompt basicPrompt = InterviewPrompt.builder()
                .name("기본 프롬프트")
                .category(PromptCategory.BASIC)
                .content("당신은 전문적인 면접관입니다. 다음 지침을 따라 면접을 진행해주세요:\n\n" +
                        "1. 면접자의 답변을 주의 깊게 듣고, 관련된 후속 질문을 해주세요.\n" +
                        "2. 답변이 불완전하거나 모호한 경우, 구체적인 예시나 설명을 요청해주세요.\n" +
                        "3. 면접자의 경험과 기술을 정확하게 평가해주세요.\n" +
                        "4. 전문적이고 객관적인 태도를 유지해주세요.\n" +
                        "5. 면접자의 긴장감을 완화하고 편안한 분위기를 만들어주세요.")
                .active(true)
                .level(1)
                .createdAt(now)
                .updatedAt(now)
                .build();
        interviewPromptRepository.save(basicPrompt);

        // 면접관 스타일 프롬프트
        InterviewPrompt stylePrompt = InterviewPrompt.builder()
                .name("면접관 스타일")
                .category(PromptCategory.INTERVIEWER_STYLE)
                .content("면접관 스타일에 따라 면접을 진행해주세요. 다양한 스타일의 면접을 통해 지원자의 역량을 다각도로 평가할 수 있습니다.\n\n" +
                        "당신은 {{면접관_스타일}}입니다. 면접자의 긴장감을 완화하고 편안한 분위기에서 면접을 진행해주세요. 긍정적인 피드백을 자주 제공하고, 대화하듯이 질문해주세요.")
                .active(true)
                .level(1)
                .createdAt(now)
                .updatedAt(now)
                .build();
        interviewPromptRepository.save(stylePrompt);

        // 직무 정보 프롬프트
        InterviewPrompt jobInfoPrompt = InterviewPrompt.builder()
                .name("직무 정보")
                .category(PromptCategory.JOB_INFO)
                .content("{{직무}} 면접을 진행합니다. 해당 직무에 필요한 역량과 기술을 중심으로 질문해주세요.")
                .active(true)
                .level(1)
                .createdAt(now)
                .updatedAt(now)
                .build();
        interviewPromptRepository.save(jobInfoPrompt);

        // 경력 수준 프롬프트
        InterviewPrompt experiencePrompt = InterviewPrompt.builder()
                .name("경력 수준")
                .category(PromptCategory.EXPERIENCE_SKILLS)
                .content("{{경력_수준}} 면접을 진행합니다. 해당 경력 수준에 맞는 깊이와 범위로 질문을 진행해주세요.\n\n" +
                        "기술 스택: {{기술_스택}}")
                .active(true)
                .level(1)
                .createdAt(now)
                .updatedAt(now)
                .build();
        interviewPromptRepository.save(experiencePrompt);

        // 난이도 및 면접 방식 프롬프트
        InterviewPrompt difficultyPrompt = InterviewPrompt.builder()
                .name("난이도 및 면접 방식")
                .category(PromptCategory.DIFFICULTY_STYLE)
                .content("면접 난이도는 {{난이도}}입니다. 해당 난이도에 맞는 질문의 복잡성과 깊이를 유지해주세요.\n\n" +
                        "면접 모드는 {{면접_모드}}입니다. 해당 모드에 적합한 형태로 질문을 구성해주세요.")
                .active(true)
                .level(1)
                .createdAt(now)
                .updatedAt(now)
                .build();
        interviewPromptRepository.save(difficultyPrompt);

        // 면접 시간 및 질문 수 프롬프트
        InterviewPrompt timePrompt = InterviewPrompt.builder()
                .name("면접 시간 및 질문")
                .category(PromptCategory.TIME_QUESTIONS)
                .content("면접 시간은 {{면접_시간}}입니다. 주어진 시간에 맞게 적절한 양의 질문을 준비해주세요.")
                .active(true)
                .level(1)
                .createdAt(now)
                .updatedAt(now)
                .build();
        interviewPromptRepository.save(timePrompt);

        // 언어 설정 프롬프트
        InterviewPrompt languagePrompt = InterviewPrompt.builder()
                .name("언어 설정")
                .category(PromptCategory.LANGUAGE)
                .content("면접 언어는 {{언어}}입니다. 해당 언어로 면접을 진행해주세요.")
                .active(true)
                .level(1)
                .createdAt(now)
                .updatedAt(now)
                .build();
        interviewPromptRepository.save(languagePrompt);

        // 마무리 지침 프롬프트
        InterviewPrompt closingPrompt = InterviewPrompt.builder()
                .name("마무리 지침")
                .category(PromptCategory.CLOSING)
                .content("면접을 마무리할 때는 다음과 같이 진행해주세요:\n\n" +
                        "1. 지원자에게 추가 질문이 있는지 물어봐주세요.\n" +
                        "2. 면접 과정에서 좋았던 부분에 대해 긍정적인 피드백을 제공해주세요.\n" +
                        "3. 다음 단계나 결과 통보 방식에 대해 안내해주세요.\n" +
                        "4. 면접 참여에 대한 감사 인사를 전해주세요.")
                .active(true)
                .level(1)
                .createdAt(now)
                .updatedAt(now)
                .build();
        interviewPromptRepository.save(closingPrompt);

        // 직무별 상세 프롬프트 추가
        addJobRolePrompts(jobInfoPrompt, now);

        // 경력 수준별 상세 프롬프트 추가
        addExperienceLevelPrompts(experiencePrompt, now);

        // 난이도별 상세 프롬프트 추가
        addDifficultyPrompts(difficultyPrompt, now);

        log.info("면접 프롬프트 데이터 초기화 완료: {} 개의 프롬프트 생성", interviewPromptRepository.count());
    }

    /**
     * 직무별 상세 프롬프트 추가
     */
    private void addJobRolePrompts(InterviewPrompt parent, LocalDateTime now) {
        // 백엔드 개발자 프롬프트
        InterviewPrompt backendPrompt = InterviewPrompt.builder()
                .name("백엔드 개발자")
                .category(PromptCategory.JOB_INFO)
                .content("백엔드 개발자 면접을 진행합니다. 서버, 데이터베이스, API 설계, 성능 최적화 등에 관한 질문을 포함해주세요.")
                .active(true)
                .level(2)
                .createdAt(now)
                .updatedAt(now)
                .build();
        parent.addChild(backendPrompt);

        // 프론트엔드 개발자 프롬프트
        InterviewPrompt frontendPrompt = InterviewPrompt.builder()
                .name("프론트엔드 개발자")
                .category(PromptCategory.JOB_INFO)
                .content("프론트엔드 개발자 면접을 진행합니다. UI/UX, 자바스크립트, 프레임워크, 반응형 디자인 등에 관한 질문을 포함해주세요.")
                .active(true)
                .level(2)
                .createdAt(now)
                .updatedAt(now)
                .build();
        parent.addChild(frontendPrompt);

        // 풀스택 개발자 프롬프트
        InterviewPrompt fullstackPrompt = InterviewPrompt.builder()
                .name("풀스택 개발자")
                .category(PromptCategory.JOB_INFO)
                .content("풀스택 개발자 면접을 진행합니다. 프론트엔드와 백엔드 모두에 대한 질문을 균형있게 포함해주세요.")
                .active(true)
                .level(2)
                .createdAt(now)
                .updatedAt(now)
                .build();
        parent.addChild(fullstackPrompt);

        // 모바일 개발자 프롬프트
        InterviewPrompt mobilePrompt = InterviewPrompt.builder()
                .name("모바일 개발자")
                .category(PromptCategory.JOB_INFO)
                .content("모바일 개발자 면접을 진행합니다. 모바일 앱 개발, 네이티브/하이브리드 개발, 성능 최적화 등에 관한 질문을 포함해주세요.")
                .active(true)
                .level(2)
                .createdAt(now)
                .updatedAt(now)
                .build();
        parent.addChild(mobilePrompt);

        // DevOps 엔지니어 프롬프트
        InterviewPrompt devopsPrompt = InterviewPrompt.builder()
                .name("DevOps 엔지니어")
                .category(PromptCategory.JOB_INFO)
                .content("DevOps 엔지니어 면접을 진행합니다. CI/CD, 클라우드 인프라, 컨테이너화, 자동화 등에 관한 질문을 포함해주세요.")
                .active(true)
                .level(2)
                .createdAt(now)
                .updatedAt(now)
                .build();
        parent.addChild(devopsPrompt);

        // 부모 프롬프트 저장 (자식들도 cascade로 함께 저장됨)
        interviewPromptRepository.save(parent);
    }

    /**
     * 경력 수준별 상세 프롬프트 추가
     */
    private void addExperienceLevelPrompts(InterviewPrompt parent, LocalDateTime now) {
        // 신입 프롬프트
        InterviewPrompt entryPrompt = InterviewPrompt.builder()
                .name("신입")
                .category(PromptCategory.EXPERIENCE_SKILLS)
                .content("신입 개발자 면접을 진행합니다. 기본적인 개념 이해도와 학습 능력, 성장 가능성을 중점적으로 평가해주세요.")
                .active(true)
                .level(2)
                .createdAt(now)
                .updatedAt(now)
                .build();
        parent.addChild(entryPrompt);

        // 주니어 프롬프트
        InterviewPrompt juniorPrompt = InterviewPrompt.builder()
                .name("주니어 (1-3년)")
                .category(PromptCategory.EXPERIENCE_SKILLS)
                .content("주니어 개발자 면접을 진행합니다. 실무 경험과 기본적인 문제 해결 능력을 중점적으로 평가해주세요.")
                .active(true)
                .level(2)
                .createdAt(now)
                .updatedAt(now)
                .build();
        parent.addChild(juniorPrompt);

        // 미드레벨 프롬프트
        InterviewPrompt midLevelPrompt = InterviewPrompt.builder()
                .name("미드레벨 (4-7년)")
                .category(PromptCategory.EXPERIENCE_SKILLS)
                .content("미드레벨 개발자 면접을 진행합니다. 깊이 있는 기술 지식과 비즈니스 이해도를 중점적으로 평가해주세요.")
                .active(true)
                .level(2)
                .createdAt(now)
                .updatedAt(now)
                .build();
        parent.addChild(midLevelPrompt);

        // 시니어 프롬프트
        InterviewPrompt seniorPrompt = InterviewPrompt.builder()
                .name("시니어 (8년 이상)")
                .category(PromptCategory.EXPERIENCE_SKILLS)
                .content("시니어 개발자 면접을 진행합니다. 아키텍처 설계 능력과 리더십, 멘토링 역량을 중점적으로 평가해주세요.")
                .active(true)
                .level(2)
                .createdAt(now)
                .updatedAt(now)
                .build();
        parent.addChild(seniorPrompt);

        // 부모 프롬프트 저장 (자식들도 cascade로 함께 저장됨)
        interviewPromptRepository.save(parent);
    }

    /**
     * 난이도별 상세 프롬프트 추가
     */
    private void addDifficultyPrompts(InterviewPrompt parent, LocalDateTime now) {
        // 초급 프롬프트
        InterviewPrompt beginnerPrompt = InterviewPrompt.builder()
                .name("초급")
                .category(PromptCategory.DIFFICULTY_STYLE)
                .content("초급 난이도로 면접을 진행합니다. 기본적인 개념과 이론에 관한 질문을 주로 해주세요.")
                .active(true)
                .level(2)
                .createdAt(now)
                .updatedAt(now)
                .build();
        parent.addChild(beginnerPrompt);

        // 중급 프롬프트
        InterviewPrompt intermediatePrompt = InterviewPrompt.builder()
                .name("중급")
                .category(PromptCategory.DIFFICULTY_STYLE)
                .content("중급 난이도로 면접을 진행합니다. 실무 활용 능력과 문제 해결 방법에 관한 질문을 주로 해주세요.")
                .active(true)
                .level(2)
                .createdAt(now)
                .updatedAt(now)
                .build();
        parent.addChild(intermediatePrompt);

        // 고급 프롬프트
        InterviewPrompt advancedPrompt = InterviewPrompt.builder()
                .name("고급")
                .category(PromptCategory.DIFFICULTY_STYLE)
                .content("고급 난이도로 면접을 진행합니다. 복잡한 문제 상황과 최적화, 심화 개념에 관한 질문을 주로 해주세요.")
                .active(true)
                .level(2)
                .createdAt(now)
                .updatedAt(now)
                .build();
        parent.addChild(advancedPrompt);

        // 전문가 프롬프트
        InterviewPrompt expertPrompt = InterviewPrompt.builder()
                .name("전문가")
                .category(PromptCategory.DIFFICULTY_STYLE)
                .content("전문가 난이도로 면접을 진행합니다. 최신 트렌드, 아키텍처 설계, 확장성 있는 솔루션에 관한 질문을 주로 해주세요.")
                .active(true)
                .level(2)
                .createdAt(now)
                .updatedAt(now)
                .build();
        parent.addChild(expertPrompt);

        // 부모 프롬프트 저장 (자식들도 cascade로 함께 저장됨)
        interviewPromptRepository.save(parent);
    }

    private void initializeInterviewCategories() {
        if (interviewCategoryRepository.count() > 0) {
            log.info("인터뷰 카테고리 데이터가 이미 존재합니다. 초기화를 건너뜁니다.");
            return;
        }

        LocalDateTime now = LocalDateTime.now();

        // 스킬 맵 생성 (영문명 -> 엔티티)
        Map<String, Skill> skillMap = skillRepository.findAll().stream()
                .collect(Collectors.toMap(Skill::getNameEn, skill -> skill));

        // 카테고리 맵 생성 (타입 -> 엔티티) - level=1인 대분류 카테고리만 포함
        Map<InterviewType, InterviewCategory> categoryMap = new HashMap<>();
        interviewCategoryRepository.findAll().stream()
                .filter(category -> category.getLevel() == 1)
                .forEach(category -> categoryMap.put(category.getType(), category));

        // 1. 대분류 카테고리 생성
        InterviewCategory development = InterviewCategory.builder()
                .icon("Icons.code")
                .title("개발")
                .titleEn("Development")
                .description("다양한 IT 개발 직군 포함")
                .descriptionEn("Various IT development roles")
                .type(InterviewType.DEVELOPMENT)
                .level(1)
                .createdAt(now)
                .updatedAt(now)
                .build();

        InterviewCategory design = InterviewCategory.builder()
                .icon("Icons.design_services")
                .title("디자인")
                .titleEn("Design")
                .description("UI/UX, 그래픽, 제품 디자인 직군")
                .descriptionEn("UI/UX, graphic, and product design roles")
                .type(InterviewType.DESIGN)
                .level(1)
                .createdAt(now)
                .updatedAt(now)
                .build();

        InterviewCategory marketing = InterviewCategory.builder()
                .icon("Icons.campaign")
                .title("마케팅")
                .titleEn("Marketing")
                .description("디지털 마케팅, 콘텐츠 기획 직군")
                .descriptionEn("Digital marketing and content planning roles")
                .type(InterviewType.MARKETING)
                .level(1)
                .createdAt(now)
                .updatedAt(now)
                .build();

        InterviewCategory business = InterviewCategory.builder()
                .icon("Icons.business")
                .title("경영지원")
                .titleEn("Business Operations")
                .description("인사, 재무, 행정 지원 직군")
                .descriptionEn("HR, finance, and administrative support roles")
                .type(InterviewType.BUSINESS)
                .level(1)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // 새로운 대분류 카테고리 추가
        InterviewCategory sales = InterviewCategory.builder()
                .icon("Icons.point_of_sale")
                .title("영업/세일즈")
                .titleEn("Sales")
                .description("제품 및 서비스 판매, 고객 관계 유지 관련 직군")
                .descriptionEn("Roles related to selling products and services and maintaining customer relationships")
                .type(InterviewType.SALES)
                .level(1)
                .createdAt(now)
                .updatedAt(now)
                .build();

        InterviewCategory customerService = InterviewCategory.builder()
                .icon("Icons.support_agent")
                .title("고객 지원")
                .titleEn("Customer Support")
                .description("고객 문의 응대, 기술 지원, 사용자 경험 관련 직군")
                .descriptionEn("Roles related to customer inquiries, technical support, and user experience")
                .type(InterviewType.CUSTOMER_SERVICE)
                .level(1)
                .createdAt(now)
                .updatedAt(now)
                .build();

        InterviewCategory media = InterviewCategory.builder()
                .icon("Icons.video_library")
                .title("미디어/콘텐츠")
                .titleEn("Media/Content")
                .description("콘텐츠 제작, 미디어 관리, 방송 관련 직군")
                .descriptionEn("Roles related to content creation, media management, and broadcasting")
                .type(InterviewType.MEDIA)
                .level(1)
                .createdAt(now)
                .updatedAt(now)
                .build();

        InterviewCategory education = InterviewCategory.builder()
                .icon("Icons.school")
                .title("교육")
                .titleEn("Education")
                .description("교육, 훈련, 학습 콘텐츠 개발 관련 직군")
                .descriptionEn("Roles related to education, training, and learning content development")
                .type(InterviewType.EDUCATION)
                .level(1)
                .createdAt(now)
                .updatedAt(now)
                .build();

        InterviewCategory logistics = InterviewCategory.builder()
                .icon("Icons.local_shipping")
                .title("물류/유통")
                .titleEn("Logistics/Distribution")
                .description("공급망 관리, 운송, 물류 최적화 관련 직군")
                .descriptionEn("Roles related to supply chain management, transportation, and logistics optimization")
                .type(InterviewType.LOGISTICS)
                .level(1)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // 2. 대분류 카테고리 저장
        List<InterviewCategory> mainCategories = List.of(development, design, marketing, business,
                sales, customerService, media, education, logistics);
        interviewCategoryRepository.saveAll(mainCategories);

        // 3. 개발 분야 중분류 카테고리 생성 및 연결
        InterviewCategory backendDev = InterviewCategory.builder()
                .icon("Icons.code")
                .title("백엔드 개발")
                .titleEn("Backend Development")
                .description("서버, API, 데이터베이스 설계 및 개발을 담당하는 직군")
                .descriptionEn("Roles responsible for server-side logic, API development, and database design")
                .type(InterviewType.DEVELOPMENT)
                .parent(development)
                .level(2)
                .createdAt(now)
                .updatedAt(now)
                .build();
        development.addChild(backendDev);

        InterviewCategory frontendDev = InterviewCategory.builder()
                .icon("Icons.web")
                .title("프론트엔드 개발")
                .titleEn("Frontend Development")
                .description("웹/앱 인터페이스 및 사용자 경험 구현을 담당하는 직군")
                .descriptionEn("Roles responsible for implementing user interfaces and experiences")
                .type(InterviewType.DEVELOPMENT)
                .parent(development)
                .level(2)
                .createdAt(now)
                .updatedAt(now)
                .build();
        development.addChild(frontendDev);

        InterviewCategory fullstackDev = InterviewCategory.builder()
                .icon("Icons.all_inclusive")
                .title("풀스택 개발")
                .titleEn("Full Stack Development")
                .description("프론트엔드와 백엔드 모두 개발할 수 있는 개발자")
                .descriptionEn("Roles covering both frontend and backend development")
                .type(InterviewType.DEVELOPMENT)
                .parent(development)
                .level(2)
                .createdAt(now)
                .updatedAt(now)
                .build();
        development.addChild(fullstackDev);

        InterviewCategory mobileDev = InterviewCategory.builder()
                .icon("Icons.phone_android")
                .title("모바일 개발자")
                .titleEn("Mobile App Development")
                .description("iOS, Android, 크로스 플랫폼 앱 개발을 담당하는 직군")
                .descriptionEn("Roles focused on iOS, Android, and cross-platform app development")
                .type(InterviewType.DEVELOPMENT)
                .parent(development)
                .level(2)
                .createdAt(now)
                .updatedAt(now)
                .build();
        development.addChild(mobileDev);

        InterviewCategory devOps = InterviewCategory.builder()
                .icon("Icons.cloud")
                .title("데브옵스/인프라")
                .titleEn("DevOps/Infrastructure")
                .description("개발 및 운영 자동화, 클라우드 인프라 관리를 담당하는 직군")
                .descriptionEn("Roles managing automation, deployment, and cloud infrastructure")
                .type(InterviewType.DEVELOPMENT)
                .parent(development)
                .level(2)
                .createdAt(now)
                .updatedAt(now)
                .build();
        development.addChild(devOps);

        // 새로운 개발 직군 추가 시작
        InterviewCategory aiMlDev = InterviewCategory.builder()
                .icon("Icons.psychology")
                .title("AI/머신러닝")
                .titleEn("AI/Machine Learning")
                .description("인공지능, 머신러닝, 딥러닝 솔루션 개발을 담당하는 직군")
                .descriptionEn(
                        "Roles developing artificial intelligence, machine learning, and deep learning solutions")
                .type(InterviewType.DEVELOPMENT)
                .parent(development)
                .level(2)
                .createdAt(now)
                .updatedAt(now)
                .build();
        development.addChild(aiMlDev);

        InterviewCategory dataEngineer = InterviewCategory.builder()
                .icon("Icons.storage")
                .title("데이터 엔지니어링")
                .titleEn("Data Engineering")
                .description("데이터 파이프라인 구축, ETL 프로세스, 데이터 인프라 관리를 담당하는 직군")
                .descriptionEn("Roles building data pipelines, ETL processes, and managing data infrastructure")
                .type(InterviewType.DEVELOPMENT)
                .parent(development)
                .level(2)
                .createdAt(now)
                .updatedAt(now)
                .build();
        development.addChild(dataEngineer);

        InterviewCategory dataScientist = InterviewCategory.builder()
                .icon("Icons.bar_chart")
                .title("데이터 사이언스")
                .titleEn("Data Science")
                .description("데이터 분석, 모델링, 통계적 추론을 통해 인사이트를 도출하는 직군")
                .descriptionEn("Roles deriving insights through data analysis, modeling, and statistical inference")
                .type(InterviewType.DEVELOPMENT)
                .parent(development)
                .level(2)
                .createdAt(now)
                .updatedAt(now)
                .build();
        development.addChild(dataScientist);

        InterviewCategory gameDev = InterviewCategory.builder()
                .icon("Icons.sports_esports")
                .title("게임 개발")
                .titleEn("Game Development")
                .description("게임 엔진, 그래픽스, 게임 로직 및 게임 시스템 개발을 담당하는 직군")
                .descriptionEn("Roles developing game engines, graphics, game logic, and game systems")
                .type(InterviewType.DEVELOPMENT)
                .parent(development)
                .level(2)
                .createdAt(now)
                .updatedAt(now)
                .build();
        development.addChild(gameDev);

        InterviewCategory arVrDev = InterviewCategory.builder()
                .icon("Icons.view_in_ar")
                .title("AR/VR 개발")
                .titleEn("AR/VR Development")
                .description("증강현실(AR) 및 가상현실(VR) 애플리케이션 개발을 담당하는 직군")
                .descriptionEn("Roles developing augmented reality (AR) and virtual reality (VR) applications")
                .type(InterviewType.DEVELOPMENT)
                .parent(development)
                .level(2)
                .createdAt(now)
                .updatedAt(now)
                .build();
        development.addChild(arVrDev);

        InterviewCategory embeddedDev = InterviewCategory.builder()
                .icon("Icons.memory")
                .title("임베디드/IoT 개발")
                .titleEn("Embedded/IoT Development")
                .description("임베디드 시스템, 펌웨어, IoT 디바이스 및 솔루션 개발을 담당하는 직군")
                .descriptionEn("Roles developing embedded systems, firmware, IoT devices, and solutions")
                .type(InterviewType.DEVELOPMENT)
                .parent(development)
                .level(2)
                .createdAt(now)
                .updatedAt(now)
                .build();
        development.addChild(embeddedDev);

        InterviewCategory securityDev = InterviewCategory.builder()
                .icon("Icons.security")
                .title("보안 엔지니어링")
                .titleEn("Security Engineering")
                .description("애플리케이션 및 시스템 보안, 취약점 분석, 보안 아키텍처 설계를 담당하는 직군")
                .descriptionEn(
                        "Roles handling application and system security, vulnerability analysis, and security architecture design")
                .type(InterviewType.DEVELOPMENT)
                .parent(development)
                .level(2)
                .createdAt(now)
                .updatedAt(now)
                .build();
        development.addChild(securityDev);

        InterviewCategory qaDev = InterviewCategory.builder()
                .icon("Icons.checklist")
                .title("QA/테스트")
                .titleEn("QA/Testing")
                .description("소프트웨어 품질 검증, 테스트 자동화, 품질 프로세스 개선을 담당하는 직군")
                .descriptionEn("Roles verifying software quality, automating tests, and improving quality processes")
                .type(InterviewType.DEVELOPMENT)
                .parent(development)
                .level(2)
                .createdAt(now)
                .updatedAt(now)
                .build();
        development.addChild(qaDev);

        InterviewCategory blockchainDev = InterviewCategory.builder()
                .icon("Icons.link")
                .title("블록체인 개발")
                .titleEn("Blockchain Development")
                .description("블록체인 기술, 스마트 계약, 분산 애플리케이션 개발을 담당하는 직군")
                .descriptionEn(
                        "Roles developing blockchain technology, smart contracts, and decentralized applications")
                .type(InterviewType.DEVELOPMENT)
                .parent(development)
                .level(2)
                .createdAt(now)
                .updatedAt(now)
                .build();
        development.addChild(blockchainDev);
        // 새로운 개발 직군 추가 끝

        // 4. 디자인 분야 중분류 카테고리 생성 및 연결
        InterviewCategory uiuxDesign = InterviewCategory.builder()
                .icon("Icons.palette")
                .title("UI/UX 디자인")
                .titleEn("UI/UX Design")
                .description("사용자 인터페이스와 경험을 설계하고 개선하는 직군")
                .descriptionEn("Roles focused on designing and improving user interfaces and experiences")
                .type(InterviewType.DESIGN)
                .parent(design)
                .level(2)
                .createdAt(now)
                .updatedAt(now)
                .build();
        design.addChild(uiuxDesign);

        InterviewCategory graphicDesign = InterviewCategory.builder()
                .icon("Icons.brush")
                .title("그래픽 디자인")
                .titleEn("Graphic Design")
                .description("시각적 콘텐츠와 브랜드 아이덴티티를 제작하는 직군")
                .descriptionEn("Roles creating visual content and brand identity")
                .type(InterviewType.DESIGN)
                .parent(design)
                .level(2)
                .createdAt(now)
                .updatedAt(now)
                .build();
        design.addChild(graphicDesign);

        InterviewCategory productDesign = InterviewCategory.builder()
                .icon("Icons.devices")
                .title("제품 디자인")
                .titleEn("Product Design")
                .description("제품의 형태와 기능을 설계하고 개발하는 직군")
                .descriptionEn("Roles designing and developing product form and function")
                .type(InterviewType.DESIGN)
                .parent(design)
                .level(2)
                .createdAt(now)
                .updatedAt(now)
                .build();
        design.addChild(productDesign);

        InterviewCategory brandDesign = InterviewCategory.builder()
                .icon("Icons.branding_watermark")
                .title("브랜드 디자인")
                .titleEn("Brand Design")
                .description("브랜드 아이덴티티와 시각적 요소를 개발하는 직군")
                .descriptionEn("Roles developing brand identity and visual elements")
                .type(InterviewType.DESIGN)
                .parent(design)
                .level(2)
                .createdAt(now)
                .updatedAt(now)
                .build();
        design.addChild(brandDesign);

        InterviewCategory illustration = InterviewCategory.builder()
                .icon("Icons.format_paint")
                .title("일러스트레이션")
                .titleEn("Illustration")
                .description("그림과 시각적 콘텐츠를 제작하는 직군")
                .descriptionEn("Roles creating drawings and visual content")
                .type(InterviewType.DESIGN)
                .parent(design)
                .level(2)
                .createdAt(now)
                .updatedAt(now)
                .build();
        design.addChild(illustration);

        // 5. 마케팅 분야 중분류 카테고리 생성 및 연결
        InterviewCategory digitalMarketing = InterviewCategory.builder()
                .icon("Icons.public")
                .title("디지털 마케팅")
                .titleEn("Digital Marketing")
                .description("온라인 채널을 통한 마케팅 전략 수립 및 실행을 담당하는 직군")
                .descriptionEn(
                        "Roles responsible for planning and executing marketing strategies through online channels")
                .type(InterviewType.MARKETING)
                .parent(marketing)
                .level(2)
                .createdAt(now)
                .updatedAt(now)
                .build();
        marketing.addChild(digitalMarketing);

        InterviewCategory contentMarketing = InterviewCategory.builder()
                .icon("Icons.article")
                .title("콘텐츠 마케팅")
                .titleEn("Content Marketing")
                .description("가치 있는 콘텐츠를 제작하고 배포하는 전략을 담당하는 직군")
                .descriptionEn("Roles handling strategies for creating and distributing valuable content")
                .type(InterviewType.MARKETING)
                .parent(marketing)
                .level(2)
                .createdAt(now)
                .updatedAt(now)
                .build();
        marketing.addChild(contentMarketing);

        InterviewCategory socialMediaMarketing = InterviewCategory.builder()
                .icon("Icons.share")
                .title("소셜 미디어 마케팅")
                .titleEn("Social Media Marketing")
                .description("소셜 미디어 플랫폼을 활용한 마케팅 활동을 담당하는 직군")
                .descriptionEn("Roles focused on marketing activities using social media platforms")
                .type(InterviewType.MARKETING)
                .parent(marketing)
                .level(2)
                .createdAt(now)
                .updatedAt(now)
                .build();
        marketing.addChild(socialMediaMarketing);

        InterviewCategory brandMarketing = InterviewCategory.builder()
                .icon("Icons.loyalty")
                .title("브랜드 마케팅")
                .titleEn("Brand Marketing")
                .description("브랜드 가치와 인지도를 높이기 위한 전략을 담당하는 직군")
                .descriptionEn("Roles handling strategies to enhance brand value and awareness")
                .type(InterviewType.MARKETING)
                .parent(marketing)
                .level(2)
                .createdAt(now)
                .updatedAt(now)
                .build();
        marketing.addChild(brandMarketing);

        InterviewCategory marketingAnalytics = InterviewCategory.builder()
                .icon("Icons.insights")
                .title("마케팅 분석")
                .titleEn("Marketing Analytics")
                .description("마케팅 데이터를 분석하고 인사이트를 도출하는 직군")
                .descriptionEn("Roles analyzing marketing data and deriving insights")
                .type(InterviewType.MARKETING)
                .parent(marketing)
                .level(2)
                .createdAt(now)
                .updatedAt(now)
                .build();
        marketing.addChild(marketingAnalytics);

        // 6. 경영지원 분야 중분류 카테고리 생성 및 연결
        InterviewCategory hr = InterviewCategory.builder()
                .icon("Icons.people")
                .title("인사(HR)")
                .titleEn("Human Resources")
                .description("인재 채용, 교육, 조직 문화 및 복리후생을 담당하는 직군")
                .descriptionEn(
                        "Roles responsible for recruitment, training, organizational culture, and employee benefits")
                .type(InterviewType.BUSINESS)
                .parent(business)
                .level(2)
                .createdAt(now)
                .updatedAt(now)
                .build();
        business.addChild(hr);

        InterviewCategory finance = InterviewCategory.builder()
                .icon("Icons.account_balance")
                .title("재무/회계")
                .titleEn("Finance/Accounting")
                .description("재무 계획, 회계, 자금 관리를 담당하는 직군")
                .descriptionEn("Roles handling financial planning, accounting, and fund management")
                .type(InterviewType.BUSINESS)
                .parent(business)
                .level(2)
                .createdAt(now)
                .updatedAt(now)
                .build();
        business.addChild(finance);

        InterviewCategory admin = InterviewCategory.builder()
                .icon("Icons.assignment")
                .title("총무/행정")
                .titleEn("Administration")
                .description("사무실 운영, 시설 관리, 행정 업무를 담당하는 직군")
                .descriptionEn("Roles managing office operations, facilities, and administrative tasks")
                .type(InterviewType.BUSINESS)
                .parent(business)
                .level(2)
                .createdAt(now)
                .updatedAt(now)
                .build();
        business.addChild(admin);

        InterviewCategory legal = InterviewCategory.builder()
                .icon("Icons.gavel")
                .title("법무")
                .titleEn("Legal")
                .description("법적 문서 검토, 계약, 지적 재산권 관리를 담당하는 직군")
                .descriptionEn("Roles reviewing legal documents, contracts, and managing intellectual property")
                .type(InterviewType.BUSINESS)
                .parent(business)
                .level(2)
                .createdAt(now)
                .updatedAt(now)
                .build();
        business.addChild(legal);

        InterviewCategory strategy = InterviewCategory.builder()
                .icon("Icons.trending_up")
                .title("전략기획")
                .titleEn("Strategic Planning")
                .description("기업의 비전과 성장 전략을 수립하고 실행하는 직군")
                .descriptionEn("Roles establishing and executing corporate vision and growth strategies")
                .type(InterviewType.BUSINESS)
                .parent(business)
                .level(2)
                .createdAt(now)
                .updatedAt(now)
                .build();
        business.addChild(strategy);

        // 7. 영업/세일즈 분야 중분류 카테고리 생성 및 연결
        InterviewCategory salesManager = InterviewCategory.builder()
                .icon("Icons.trending_up")
                .title("영업 관리자")
                .titleEn("Sales Manager")
                .description("영업팀 목표 설정 및 관리, 영업 전략 수립을 담당하는 직군")
                .descriptionEn(
                        "Roles responsible for setting and managing sales team goals and developing sales strategies")
                .type(InterviewType.SALES)
                .parent(sales)
                .level(2)
                .createdAt(now)
                .updatedAt(now)
                .build();
        sales.addChild(salesManager);

        InterviewCategory salesRep = InterviewCategory.builder()
                .icon("Icons.people")
                .title("영업 담당자")
                .titleEn("Sales Representative")
                .description("제품 및 서비스 판매, 고객 관계 구축을 담당하는 직군")
                .descriptionEn("Roles focused on selling products and services and building customer relationships")
                .type(InterviewType.SALES)
                .parent(sales)
                .level(2)
                .createdAt(now)
                .updatedAt(now)
                .build();
        sales.addChild(salesRep);

        InterviewCategory accountManager = InterviewCategory.builder()
                .icon("Icons.assignment")
                .title("계정 관리자")
                .titleEn("Account Manager")
                .description("주요 고객 관계 유지 및 관리를 담당하는 직군")
                .descriptionEn("Roles responsible for maintaining and managing key customer relationships")
                .type(InterviewType.SALES)
                .parent(sales)
                .level(2)
                .createdAt(now)
                .updatedAt(now)
                .build();
        sales.addChild(accountManager);

        InterviewCategory bdManager = InterviewCategory.builder()
                .icon("Icons.handshake")
                .title("사업개발 매니저")
                .titleEn("Business Development Manager")
                .description("신규 사업 기회 발굴 및 전략적 파트너십 구축을 담당하는 직군")
                .descriptionEn(
                        "Roles focused on identifying new business opportunities and building strategic partnerships")
                .type(InterviewType.SALES)
                .parent(sales)
                .level(2)
                .createdAt(now)
                .updatedAt(now)
                .build();
        sales.addChild(bdManager);

        // 8. 고객 지원 분야 중분류 카테고리 생성 및 연결
        InterviewCategory customerSupportRep = InterviewCategory.builder()
                .icon("Icons.headset_mic")
                .title("고객 지원 담당자")
                .titleEn("Customer Support Representative")
                .description("고객 문의 및 불만 처리, 문제 해결을 담당하는 직군")
                .descriptionEn("Roles handling customer inquiries, complaints, and problem-solving")
                .type(InterviewType.CUSTOMER_SERVICE)
                .parent(customerService)
                .level(2)
                .createdAt(now)
                .updatedAt(now)
                .build();
        customerService.addChild(customerSupportRep);

        InterviewCategory technicalSupport = InterviewCategory.builder()
                .icon("Icons.engineering")
                .title("기술 지원 엔지니어")
                .titleEn("Technical Support Engineer")
                .description("제품 및 서비스의 기술적 문제 해결을 담당하는 직군")
                .descriptionEn("Roles focused on resolving technical issues with products and services")
                .type(InterviewType.CUSTOMER_SERVICE)
                .parent(customerService)
                .level(2)
                .createdAt(now)
                .updatedAt(now)
                .build();
        customerService.addChild(technicalSupport);

        InterviewCategory customerSuccessManager = InterviewCategory.builder()
                .icon("Icons.verified_user")
                .title("고객 성공 매니저")
                .titleEn("Customer Success Manager")
                .description("고객의 성공적인 제품 사용 및 경험 향상을 지원하는 직군")
                .descriptionEn(
                        "Roles supporting customers in successfully using products and enhancing their experience")
                .type(InterviewType.CUSTOMER_SERVICE)
                .parent(customerService)
                .level(2)
                .createdAt(now)
                .updatedAt(now)
                .build();
        customerService.addChild(customerSuccessManager);

        // 9. 미디어/콘텐츠 분야 중분류 카테고리 생성 및 연결
        InterviewCategory contentCreator = InterviewCategory.builder()
                .icon("Icons.create")
                .title("콘텐츠 크리에이터")
                .titleEn("Content Creator")
                .description("다양한 형식의 콘텐츠 기획 및 제작을 담당하는 직군")
                .descriptionEn("Roles planning and producing content in various formats")
                .type(InterviewType.MEDIA)
                .parent(media)
                .level(2)
                .createdAt(now)
                .updatedAt(now)
                .build();
        media.addChild(contentCreator);

        InterviewCategory videoEditor = InterviewCategory.builder()
                .icon("Icons.videocam")
                .title("영상 편집자")
                .titleEn("Video Editor")
                .description("영상 촬영, 편집, 후반 작업을 담당하는 직군")
                .descriptionEn("Roles responsible for video shooting, editing, and post-production")
                .type(InterviewType.MEDIA)
                .parent(media)
                .level(2)
                .createdAt(now)
                .updatedAt(now)
                .build();
        media.addChild(videoEditor);

        InterviewCategory copywriter = InterviewCategory.builder()
                .icon("Icons.edit_note")
                .title("카피라이터")
                .titleEn("Copywriter")
                .description("광고, 마케팅, 웹사이트 등의 글 작성을 담당하는 직군")
                .descriptionEn("Roles writing copy for advertisements, marketing materials, websites, etc.")
                .type(InterviewType.MEDIA)
                .parent(media)
                .level(2)
                .createdAt(now)
                .updatedAt(now)
                .build();
        media.addChild(copywriter);

        InterviewCategory socialMediaManager = InterviewCategory.builder()
                .icon("Icons.groups")
                .title("소셜 미디어 매니저")
                .titleEn("Social Media Manager")
                .description("소셜 미디어 계정 관리 및 콘텐츠 전략 수립을 담당하는 직군")
                .descriptionEn("Roles managing social media accounts and developing content strategies")
                .type(InterviewType.MEDIA)
                .parent(media)
                .level(2)
                .createdAt(now)
                .updatedAt(now)
                .build();
        media.addChild(socialMediaManager);

        // 10. 교육 분야 중분류 카테고리 생성 및 연결
        InterviewCategory instructor = InterviewCategory.builder()
                .icon("Icons.cast_for_education")
                .title("강사/트레이너")
                .titleEn("Instructor/Trainer")
                .description("교육 프로그램 진행 및 학습 지도를 담당하는 직군")
                .descriptionEn("Roles conducting educational programs and providing learning guidance")
                .type(InterviewType.EDUCATION)
                .parent(education)
                .level(2)
                .createdAt(now)
                .updatedAt(now)
                .build();
        education.addChild(instructor);

        InterviewCategory curriculum = InterviewCategory.builder()
                .icon("Icons.menu_book")
                .title("교육과정 개발자")
                .titleEn("Curriculum Developer")
                .description("교육 과정 및 교재 개발을 담당하는 직군")
                .descriptionEn("Roles developing educational curricula and materials")
                .type(InterviewType.EDUCATION)
                .parent(education)
                .level(2)
                .createdAt(now)
                .updatedAt(now)
                .build();
        education.addChild(curriculum);

        InterviewCategory elearning = InterviewCategory.builder()
                .icon("Icons.computer")
                .title("이러닝 전문가")
                .titleEn("E-Learning Specialist")
                .description("온라인 학습 콘텐츠 및 시스템 개발을 담당하는 직군")
                .descriptionEn("Roles developing online learning content and systems")
                .type(InterviewType.EDUCATION)
                .parent(education)
                .level(2)
                .createdAt(now)
                .updatedAt(now)
                .build();
        education.addChild(elearning);

        // 11. 물류/유통 분야 중분류 카테고리 생성 및 연결
        InterviewCategory supplyChain = InterviewCategory.builder()
                .icon("Icons.inventory")
                .title("공급망 관리자")
                .titleEn("Supply Chain Manager")
                .description("전체 공급망 최적화 및 관리를 담당하는 직군")
                .descriptionEn("Roles optimizing and managing the entire supply chain")
                .type(InterviewType.LOGISTICS)
                .parent(logistics)
                .level(2)
                .createdAt(now)
                .updatedAt(now)
                .build();
        logistics.addChild(supplyChain);

        InterviewCategory procurement = InterviewCategory.builder()
                .icon("Icons.shopping_cart")
                .title("구매/조달 전문가")
                .titleEn("Procurement Specialist")
                .description("자재, 서비스 구매 및 공급업체 관리를 담당하는 직군")
                .descriptionEn("Roles purchasing materials and services and managing suppliers")
                .type(InterviewType.LOGISTICS)
                .parent(logistics)
                .level(2)
                .createdAt(now)
                .updatedAt(now)
                .build();
        logistics.addChild(procurement);

        InterviewCategory warehouseManager = InterviewCategory.builder()
                .icon("Icons.warehouse")
                .title("물류 센터 관리자")
                .titleEn("Warehouse Manager")
                .description("창고 운영, 재고 관리, 물류 효율화를 담당하는 직군")
                .descriptionEn("Roles managing warehouse operations, inventory, and logistics efficiency")
                .type(InterviewType.LOGISTICS)
                .parent(logistics)
                .level(2)
                .createdAt(now)
                .updatedAt(now)
                .build();
        logistics.addChild(warehouseManager);

        InterviewCategory logistics_analyst = InterviewCategory.builder()
                .icon("Icons.analytics")
                .title("물류 분석가")
                .titleEn("Logistics Analyst")
                .description("물류 데이터 분석 및 최적화 방안 제시를 담당하는 직군")
                .descriptionEn("Roles analyzing logistics data and suggesting optimization methods")
                .type(InterviewType.LOGISTICS)
                .parent(logistics)
                .level(2)
                .createdAt(now)
                .updatedAt(now)
                .build();
        logistics.addChild(logistics_analyst);

        // 7. 중분류 카테고리 저장
        List<InterviewCategory> subCategories = List.of(
                // 개발 분야
                backendDev, frontendDev, fullstackDev, mobileDev, devOps, aiMlDev, dataEngineer, dataScientist, gameDev,
                arVrDev, embeddedDev, securityDev, qaDev, blockchainDev,
                // 디자인 분야
                uiuxDesign, graphicDesign, productDesign, brandDesign, illustration,
                // 마케팅 분야
                digitalMarketing, contentMarketing, socialMediaMarketing, brandMarketing, marketingAnalytics,
                // 경영지원 분야
                hr, finance, admin, legal, strategy,
                // 영업/세일즈 분야
                salesManager, salesRep, accountManager, bdManager,
                // 고객 지원 분야
                customerSupportRep, technicalSupport, customerSuccessManager,
                // 미디어/콘텐츠 분야
                contentCreator, videoEditor, copywriter, socialMediaManager,
                // 교육 분야
                instructor, curriculum, elearning,
                // 물류/유통 분야
                supplyChain, procurement, warehouseManager, logistics_analyst);
        interviewCategoryRepository.saveAll(subCategories);

        // 개발 분야 카테고리에 스킬 연결 (기존 코드)
        // 백엔드 개발 스킬
        addSkillsToCategory(backendDev, List.of("Java", "Python", "Spring", "Spring Boot", "Django",
                "SQL", "MySQL", "PostgreSQL", "MongoDB", "REST API", "GraphQL", "AWS"), skillMap);

        // 프론트엔드 개발 스킬
        addSkillsToCategory(frontendDev, List.of("JavaScript", "TypeScript", "React", "Vue.js", "Angular",
                "HTML5", "CSS3", "Tailwind CSS", "Next.js", "Redux", "Webpack", "Jest"), skillMap);

        // 풀스택 개발 스킬
        addSkillsToCategory(fullstackDev, List.of("JavaScript", "TypeScript", "React", "Node.js", "Express.js",
                "MongoDB", "MySQL", "Redis", "REST API", "GraphQL", "Docker", "Git"), skillMap);

        // 모바일 앱 개발 스킬
        addSkillsToCategory(mobileDev, List.of("Android", "Kotlin", "Java", "Swift", "SwiftUI", "React Native",
                "Flutter", "Firebase", "RESTful API", "MVC", "MVVM", "SQLite"), skillMap);

        // 데브옵스/인프라 스킬
        addSkillsToCategory(devOps, List.of("Docker", "Kubernetes", "AWS", "Azure", "GCP", "CI/CD",
                "Jenkins", "Terraform", "Ansible", "Prometheus", "Grafana", "ELK Stack"), skillMap);

        log.info("대분류 카테고리 {}개, 중분류 카테고리 {}개가 초기화되었습니다.",
                mainCategories.size(), subCategories.size());
    }

    /**
     * 카테고리에 스킬 목록 추가
     */
    private void addSkillsToCategory(InterviewCategory category, List<String> skillNames, Map<String, Skill> skillMap) {
        for (String skillName : skillNames) {
            Skill skill = skillMap.get(skillName);
            if (skill != null) {
                category.addSkill(skill);
            } else {
                log.warn("스킬 '{}' 을(를) 찾을 수 없습니다.", skillName);
            }
        }
    }

    /**
     * 직무별 기술 스택 초기화
     */
    @Transactional
    public void initializeJobRoleSkills() {
        log.info("직무별 기술 스택 초기화 시작");

        // 기술 스택이 없으면 먼저 생성
        if (skillRepository.count() == 0) {
            initializeSkills();
        }

        // 직무별 기술 스택 매핑
        JobRoleSkillService.JobRoleSkillMapping[] mappings = {
                // 프론트엔드 개발자 필수 기술
                new JobRoleSkillService.JobRoleSkillMapping(JobRole.FRONTEND_DEVELOPER, "JavaScript", 1),
                new JobRoleSkillService.JobRoleSkillMapping(JobRole.FRONTEND_DEVELOPER, "HTML", 1),
                new JobRoleSkillService.JobRoleSkillMapping(JobRole.FRONTEND_DEVELOPER, "CSS", 1),
                new JobRoleSkillService.JobRoleSkillMapping(JobRole.FRONTEND_DEVELOPER, "React", 1),
                // 프론트엔드 개발자 권장 기술
                new JobRoleSkillService.JobRoleSkillMapping(JobRole.FRONTEND_DEVELOPER, "TypeScript", 2),
                new JobRoleSkillService.JobRoleSkillMapping(JobRole.FRONTEND_DEVELOPER, "Redux", 2),
                new JobRoleSkillService.JobRoleSkillMapping(JobRole.FRONTEND_DEVELOPER, "Webpack", 2),
                // 프론트엔드 개발자 선택 기술
                new JobRoleSkillService.JobRoleSkillMapping(JobRole.FRONTEND_DEVELOPER, "Vue", 3),
                new JobRoleSkillService.JobRoleSkillMapping(JobRole.FRONTEND_DEVELOPER, "Angular", 3),
                new JobRoleSkillService.JobRoleSkillMapping(JobRole.FRONTEND_DEVELOPER, "Next", 3),

                // 백엔드 개발자 필수 기술
                new JobRoleSkillService.JobRoleSkillMapping(JobRole.BACKEND_DEVELOPER, "Java", 1),
                new JobRoleSkillService.JobRoleSkillMapping(JobRole.BACKEND_DEVELOPER, "Spring", 1),
                new JobRoleSkillService.JobRoleSkillMapping(JobRole.BACKEND_DEVELOPER, "MySQL", 1),
                new JobRoleSkillService.JobRoleSkillMapping(JobRole.BACKEND_DEVELOPER, "JPA", 1),
                // 백엔드 개발자 권장 기술
                new JobRoleSkillService.JobRoleSkillMapping(JobRole.BACKEND_DEVELOPER, "SpringBoot", 2),
                new JobRoleSkillService.JobRoleSkillMapping(JobRole.BACKEND_DEVELOPER, "Redis", 2),
                new JobRoleSkillService.JobRoleSkillMapping(JobRole.BACKEND_DEVELOPER, "AWS", 2),
                // 백엔드 개발자 선택 기술
                new JobRoleSkillService.JobRoleSkillMapping(JobRole.BACKEND_DEVELOPER, "Docker", 3),
                new JobRoleSkillService.JobRoleSkillMapping(JobRole.BACKEND_DEVELOPER, "Kubernetes", 3),
                new JobRoleSkillService.JobRoleSkillMapping(JobRole.BACKEND_DEVELOPER, "MongoDB", 3),

                // 풀스택 개발자 필수 기술
                new JobRoleSkillService.JobRoleSkillMapping(JobRole.FULLSTACK_DEVELOPER, "JavaScript", 1),
                new JobRoleSkillService.JobRoleSkillMapping(JobRole.FULLSTACK_DEVELOPER, "Java", 1),
                new JobRoleSkillService.JobRoleSkillMapping(JobRole.FULLSTACK_DEVELOPER, "HTML", 1),
                new JobRoleSkillService.JobRoleSkillMapping(JobRole.FULLSTACK_DEVELOPER, "CSS", 1),
                new JobRoleSkillService.JobRoleSkillMapping(JobRole.FULLSTACK_DEVELOPER, "React", 1),
                new JobRoleSkillService.JobRoleSkillMapping(JobRole.FULLSTACK_DEVELOPER, "Spring", 1),
                // 풀스택 개발자 권장 기술
                new JobRoleSkillService.JobRoleSkillMapping(JobRole.FULLSTACK_DEVELOPER, "TypeScript", 2),
                new JobRoleSkillService.JobRoleSkillMapping(JobRole.FULLSTACK_DEVELOPER, "MySQL", 2),
                // 풀스택 개발자 선택 기술
                new JobRoleSkillService.JobRoleSkillMapping(JobRole.FULLSTACK_DEVELOPER, "AWS", 3),
                new JobRoleSkillService.JobRoleSkillMapping(JobRole.FULLSTACK_DEVELOPER, "Docker", 3),

                // 모바일 개발자 필수 기술
                new JobRoleSkillService.JobRoleSkillMapping(JobRole.MOBILE_DEVELOPER, "Swift", 1),
                new JobRoleSkillService.JobRoleSkillMapping(JobRole.MOBILE_DEVELOPER, "Kotlin", 1),
                new JobRoleSkillService.JobRoleSkillMapping(JobRole.MOBILE_DEVELOPER, "Java", 1),
                // 모바일 개발자 권장 기술
                new JobRoleSkillService.JobRoleSkillMapping(JobRole.MOBILE_DEVELOPER, "AndroidSDK", 2),
                new JobRoleSkillService.JobRoleSkillMapping(JobRole.MOBILE_DEVELOPER, "iOSSDK", 2),
                new JobRoleSkillService.JobRoleSkillMapping(JobRole.MOBILE_DEVELOPER, "Flutter", 2),
                // 모바일 개발자 선택 기술
                new JobRoleSkillService.JobRoleSkillMapping(JobRole.MOBILE_DEVELOPER, "ReactNative", 3),
                new JobRoleSkillService.JobRoleSkillMapping(JobRole.MOBILE_DEVELOPER, "Firebase", 3),

                // DevOps 개발자 필수 기술
                new JobRoleSkillService.JobRoleSkillMapping(JobRole.DEVOPS_DEVELOPER, "Docker", 1),
                new JobRoleSkillService.JobRoleSkillMapping(JobRole.DEVOPS_DEVELOPER, "Kubernetes", 1),
                new JobRoleSkillService.JobRoleSkillMapping(JobRole.DEVOPS_DEVELOPER, "AWS", 1),
                // DevOps 개발자 권장 기술
                new JobRoleSkillService.JobRoleSkillMapping(JobRole.DEVOPS_DEVELOPER, "Terraform", 2),
                new JobRoleSkillService.JobRoleSkillMapping(JobRole.DEVOPS_DEVELOPER, "Jenkins", 2),
                new JobRoleSkillService.JobRoleSkillMapping(JobRole.DEVOPS_DEVELOPER, "Linux", 2),
                // DevOps 개발자 선택 기술
                new JobRoleSkillService.JobRoleSkillMapping(JobRole.DEVOPS_DEVELOPER, "Ansible", 3),
                new JobRoleSkillService.JobRoleSkillMapping(JobRole.DEVOPS_DEVELOPER, "Prometheus", 3)
        };

        jobRoleSkillService.createJobRoleSkillMappings(Arrays.asList(mappings));
        log.info("직무별 기술 스택 초기화 완료");
    }

    /**
     * 기술 스택 초기화
     */
    @Transactional
    public void initializeSkills() {
        log.info("기술 스택 초기화 시작");

        List<Skill> skills = Arrays.asList(
                // 개발 언어
                Skill.builder().name("Java").nameEn("Java")
                        .icon("https://cdn.jsdelivr.net/gh/devicons/devicon/icons/java/java-original.svg")
                        .primaryJobRole(JobRole.BACKEND_DEVELOPER)
                        .isPopular(true)
                        .build(),
                Skill.builder().name("JavaScript").nameEn("JavaScript")
                        .icon("https://cdn.jsdelivr.net/gh/devicons/devicon/icons/javascript/javascript-original.svg")
                        .primaryJobRole(JobRole.FRONTEND_DEVELOPER)
                        .isPopular(true)
                        .build(),
                Skill.builder().name("TypeScript").nameEn("TypeScript")
                        .icon("https://cdn.jsdelivr.net/gh/devicons/devicon/icons/typescript/typescript-original.svg")
                        .primaryJobRole(JobRole.FRONTEND_DEVELOPER)
                        .isPopular(true)
                        .build(),
                Skill.builder().name("Python").nameEn("Python")
                        .icon("https://cdn.jsdelivr.net/gh/devicons/devicon/icons/python/python-original.svg")
                        .primaryJobRole(JobRole.DATA_SCIENTIST)
                        .isPopular(true)
                        .build(),
                Skill.builder().name("C++").nameEn("Cpp")
                        .icon("https://cdn.jsdelivr.net/gh/devicons/devicon/icons/cplusplus/cplusplus-original.svg")
                        .primaryJobRole(JobRole.BACKEND_DEVELOPER)
                        .isPopular(false)
                        .build(),
                Skill.builder().name("C#").nameEn("CSharp")
                        .icon("https://cdn.jsdelivr.net/gh/devicons/devicon/icons/csharp/csharp-original.svg")
                        .primaryJobRole(JobRole.BACKEND_DEVELOPER)
                        .isPopular(false)
                        .build(),
                Skill.builder().name("Go").nameEn("Go")
                        .icon("https://cdn.jsdelivr.net/gh/devicons/devicon/icons/go/go-original.svg")
                        .primaryJobRole(JobRole.BACKEND_DEVELOPER)
                        .isPopular(false)
                        .build(),
                Skill.builder().name("Swift").nameEn("Swift")
                        .icon("https://cdn.jsdelivr.net/gh/devicons/devicon/icons/swift/swift-original.svg")
                        .primaryJobRole(JobRole.MOBILE_DEVELOPER)
                        .isPopular(true)
                        .build(),
                Skill.builder().name("Kotlin").nameEn("Kotlin")
                        .icon("https://cdn.jsdelivr.net/gh/devicons/devicon/icons/kotlin/kotlin-original.svg")
                        .primaryJobRole(JobRole.MOBILE_DEVELOPER)
                        .isPopular(true)
                        .build(),

                // 웹 프론트엔드
                Skill.builder().name("HTML").nameEn("HTML")
                        .icon("https://cdn.jsdelivr.net/gh/devicons/devicon/icons/html5/html5-original.svg")
                        .primaryJobRole(JobRole.FRONTEND_DEVELOPER)
                        .isPopular(true)
                        .build(),
                Skill.builder().name("CSS").nameEn("CSS")
                        .icon("https://cdn.jsdelivr.net/gh/devicons/devicon/icons/css3/css3-original.svg")
                        .primaryJobRole(JobRole.FRONTEND_DEVELOPER)
                        .isPopular(true)
                        .build(),
                Skill.builder().name("React").nameEn("React")
                        .icon("https://cdn.jsdelivr.net/gh/devicons/devicon/icons/react/react-original.svg")
                        .primaryJobRole(JobRole.FRONTEND_DEVELOPER)
                        .isPopular(true)
                        .build(),
                Skill.builder().name("Vue.js").nameEn("Vue")
                        .icon("https://cdn.jsdelivr.net/gh/devicons/devicon/icons/vuejs/vuejs-original.svg")
                        .primaryJobRole(JobRole.FRONTEND_DEVELOPER)
                        .isPopular(true)
                        .build(),
                Skill.builder().name("Angular").nameEn("Angular")
                        .icon("https://cdn.jsdelivr.net/gh/devicons/devicon/icons/angularjs/angularjs-original.svg")
                        .primaryJobRole(JobRole.FRONTEND_DEVELOPER)
                        .isPopular(false)
                        .build(),
                Skill.builder().name("Redux").nameEn("Redux")
                        .icon("https://cdn.jsdelivr.net/gh/devicons/devicon/icons/redux/redux-original.svg")
                        .primaryJobRole(JobRole.FRONTEND_DEVELOPER)
                        .isPopular(false)
                        .build(),
                Skill.builder().name("Webpack").nameEn("Webpack")
                        .icon("https://cdn.jsdelivr.net/gh/devicons/devicon/icons/webpack/webpack-original.svg")
                        .primaryJobRole(JobRole.FRONTEND_DEVELOPER)
                        .isPopular(false)
                        .build(),
                Skill.builder().name("Next.js").nameEn("Next")
                        .icon("https://cdn.jsdelivr.net/gh/devicons/devicon/icons/nextjs/nextjs-original.svg")
                        .primaryJobRole(JobRole.FRONTEND_DEVELOPER)
                        .isPopular(true)
                        .build(),

                // 백엔드
                Skill.builder().name("Spring").nameEn("Spring")
                        .icon("https://cdn.jsdelivr.net/gh/devicons/devicon/icons/spring/spring-original.svg")
                        .primaryJobRole(JobRole.BACKEND_DEVELOPER)
                        .isPopular(true)
                        .build(),
                Skill.builder().name("Spring Boot").nameEn("SpringBoot")
                        .icon("https://cdn.jsdelivr.net/gh/devicons/devicon/icons/spring/spring-original.svg")
                        .primaryJobRole(JobRole.BACKEND_DEVELOPER)
                        .isPopular(true)
                        .build(),
                Skill.builder().name("JPA").nameEn("JPA")
                        .icon("https://www.vectorlogo.zone/logos/hibernate/hibernate-icon.svg")
                        .primaryJobRole(JobRole.BACKEND_DEVELOPER)
                        .isPopular(true)
                        .build(),
                Skill.builder().name("Node.js").nameEn("Node")
                        .icon("https://cdn.jsdelivr.net/gh/devicons/devicon/icons/nodejs/nodejs-original.svg")
                        .primaryJobRole(JobRole.BACKEND_DEVELOPER)
                        .isPopular(true)
                        .build(),
                Skill.builder().name("Express").nameEn("Express")
                        .icon("https://cdn.jsdelivr.net/gh/devicons/devicon/icons/express/express-original.svg")
                        .primaryJobRole(JobRole.BACKEND_DEVELOPER)
                        .isPopular(false)
                        .build(),
                Skill.builder().name("Django").nameEn("Django")
                        .icon("https://cdn.jsdelivr.net/gh/devicons/devicon/icons/django/django-plain.svg")
                        .primaryJobRole(JobRole.BACKEND_DEVELOPER)
                        .isPopular(false)
                        .build(),
                Skill.builder().name("Flask").nameEn("Flask")
                        .icon("https://cdn.jsdelivr.net/gh/devicons/devicon/icons/flask/flask-original.svg")
                        .primaryJobRole(JobRole.BACKEND_DEVELOPER)
                        .isPopular(false)
                        .build(),
                Skill.builder().name(".NET").nameEn("DotNet")
                        .icon("https://cdn.jsdelivr.net/gh/devicons/devicon/icons/dot-net/dot-net-original.svg")
                        .primaryJobRole(JobRole.BACKEND_DEVELOPER)
                        .isPopular(false)
                        .build(),

                // 데이터베이스
                Skill.builder().name("MySQL").nameEn("MySQL")
                        .icon("https://cdn.jsdelivr.net/gh/devicons/devicon/icons/mysql/mysql-original.svg")
                        .primaryJobRole(JobRole.BACKEND_DEVELOPER)
                        .isPopular(true)
                        .build(),
                Skill.builder().name("PostgreSQL").nameEn("PostgreSQL")
                        .icon("https://cdn.jsdelivr.net/gh/devicons/devicon/icons/postgresql/postgresql-original.svg")
                        .primaryJobRole(JobRole.BACKEND_DEVELOPER)
                        .isPopular(false)
                        .build(),
                Skill.builder().name("MongoDB").nameEn("MongoDB")
                        .icon("https://cdn.jsdelivr.net/gh/devicons/devicon/icons/mongodb/mongodb-original.svg")
                        .primaryJobRole(JobRole.BACKEND_DEVELOPER)
                        .isPopular(false)
                        .build(),
                Skill.builder().name("Redis").nameEn("Redis")
                        .icon("https://cdn.jsdelivr.net/gh/devicons/devicon/icons/redis/redis-original.svg")
                        .primaryJobRole(JobRole.BACKEND_DEVELOPER)
                        .isPopular(false)
                        .build(),
                Skill.builder().name("Oracle").nameEn("Oracle")
                        .icon("https://cdn.jsdelivr.net/gh/devicons/devicon/icons/oracle/oracle-original.svg")
                        .primaryJobRole(JobRole.BACKEND_DEVELOPER)
                        .isPopular(false)
                        .build(),
                Skill.builder().name("MS SQL Server").nameEn("MsSQL")
                        .icon("https://cdn.jsdelivr.net/gh/devicons/devicon/icons/microsoftsqlserver/microsoftsqlserver-plain.svg")
                        .primaryJobRole(JobRole.BACKEND_DEVELOPER)
                        .isPopular(false)
                        .build(),

                // 클라우드 및 인프라
                Skill.builder().name("AWS").nameEn("AWS")
                        .icon("https://cdn.jsdelivr.net/gh/devicons/devicon/icons/amazonwebservices/amazonwebservices-original.svg")
                        .primaryJobRole(JobRole.DEVOPS_DEVELOPER)
                        .isPopular(true)
                        .build(),
                Skill.builder().name("GCP").nameEn("GCP")
                        .icon("https://cdn.jsdelivr.net/gh/devicons/devicon/icons/googlecloud/googlecloud-original.svg")
                        .primaryJobRole(JobRole.DEVOPS_DEVELOPER)
                        .isPopular(false)
                        .build(),
                Skill.builder().name("Azure").nameEn("Azure")
                        .icon("https://cdn.jsdelivr.net/gh/devicons/devicon/icons/azure/azure-original.svg")
                        .primaryJobRole(JobRole.DEVOPS_DEVELOPER)
                        .isPopular(false)
                        .build(),
                Skill.builder().name("Docker").nameEn("Docker")
                        .icon("https://cdn.jsdelivr.net/gh/devicons/devicon/icons/docker/docker-original.svg")
                        .primaryJobRole(JobRole.DEVOPS_DEVELOPER)
                        .isPopular(true)
                        .build(),
                Skill.builder().name("Kubernetes").nameEn("Kubernetes")
                        .icon("https://cdn.jsdelivr.net/gh/devicons/devicon/icons/kubernetes/kubernetes-plain.svg")
                        .primaryJobRole(JobRole.DEVOPS_DEVELOPER)
                        .isPopular(true)
                        .build(),
                Skill.builder().name("Terraform").nameEn("Terraform")
                        .icon("https://cdn.jsdelivr.net/gh/devicons/devicon/icons/terraform/terraform-original.svg")
                        .primaryJobRole(JobRole.DEVOPS_DEVELOPER)
                        .isPopular(false)
                        .build(),
                Skill.builder().name("Jenkins").nameEn("Jenkins")
                        .icon("https://cdn.jsdelivr.net/gh/devicons/devicon/icons/jenkins/jenkins-original.svg")
                        .primaryJobRole(JobRole.DEVOPS_DEVELOPER)
                        .isPopular(false)
                        .build(),
                Skill.builder().name("Linux").nameEn("Linux")
                        .icon("https://cdn.jsdelivr.net/gh/devicons/devicon/icons/linux/linux-original.svg")
                        .primaryJobRole(JobRole.DEVOPS_DEVELOPER)
                        .isPopular(false)
                        .build(),
                Skill.builder().name("Ansible").nameEn("Ansible")
                        .icon("https://cdn.jsdelivr.net/gh/devicons/devicon/icons/ansible/ansible-original.svg")
                        .primaryJobRole(JobRole.DEVOPS_DEVELOPER)
                        .isPopular(false)
                        .build(),
                Skill.builder().name("Prometheus").nameEn("Prometheus")
                        .icon("https://cdn.jsdelivr.net/gh/devicons/devicon/icons/prometheus/prometheus-original.svg")
                        .primaryJobRole(JobRole.DEVOPS_DEVELOPER)
                        .isPopular(false)
                        .build(),

                // 모바일
                Skill.builder().name("Android SDK").nameEn("AndroidSDK")
                        .icon("https://cdn.jsdelivr.net/gh/devicons/devicon/icons/android/android-original.svg")
                        .primaryJobRole(JobRole.MOBILE_DEVELOPER)
                        .isPopular(true)
                        .build(),
                Skill.builder().name("iOS SDK").nameEn("iOSSDK")
                        .icon("https://cdn.jsdelivr.net/gh/devicons/devicon/icons/apple/apple-original.svg")
                        .primaryJobRole(JobRole.MOBILE_DEVELOPER)
                        .isPopular(true)
                        .build(),
                Skill.builder().name("Flutter").nameEn("Flutter")
                        .icon("https://cdn.jsdelivr.net/gh/devicons/devicon/icons/flutter/flutter-original.svg")
                        .primaryJobRole(JobRole.MOBILE_DEVELOPER)
                        .isPopular(true)
                        .build(),
                Skill.builder().name("React Native").nameEn("ReactNative")
                        .icon("https://cdn.jsdelivr.net/gh/devicons/devicon/icons/react/react-original.svg")
                        .primaryJobRole(JobRole.MOBILE_DEVELOPER)
                        .isPopular(true)
                        .build(),
                Skill.builder().name("Firebase").nameEn("Firebase")
                        .icon("https://cdn.jsdelivr.net/gh/devicons/devicon/icons/firebase/firebase-plain.svg")
                        .primaryJobRole(JobRole.MOBILE_DEVELOPER)
                        .isPopular(false)
                        .build());

        skillRepository.saveAll(skills);
        log.info("기술 스택 초기화 완료 - {}개 저장됨", skills.size());
    }

    private void initializeJobPositions() {
        if (jobPositionRepository.count() > 0) {
            log.info("직무 포지션 데이터가 이미 존재합니다. 초기화를 건너뜁니다.");
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        List<JobPosition> positions = new ArrayList<>();

        // 스킬 맵 생성 (영문명 -> 엔티티)
        Map<String, Skill> skillMap = skillRepository.findAll().stream()
                .collect(Collectors.toMap(Skill::getNameEn, skill -> skill));

        // 카테고리 맵 생성 (타입 -> 엔티티) - level=1인 대분류 카테고리만 포함
        Map<InterviewType, InterviewCategory> categoryMap = new HashMap<>();
        interviewCategoryRepository.findAll().stream()
                .filter(category -> category.getLevel() == 1)
                .forEach(category -> categoryMap.put(category.getType(), category));

        // 개발 세부 카테고리에 개발 대분류 카테고리 매핑
        InterviewCategory developmentCategory = categoryMap.get(InterviewType.DEVELOPMENT);

        // ===== 백엔드 개발 직무 =====
        positions.add(createJobPosition(
                "backend_developer",
                JobRole.BACKEND_DEVELOPER,
                "백엔드 개발자",
                "Backend Developer",
                "서버, 데이터베이스, API 설계, 성능 최적화 등에 관한 질문을 포함해주세요.",
                "Responsible for server-side logic, API development, database design, and managing the core functions of services",
                "FontAwesomeIcons.server",
                developmentCategory,
                List.of("Java", "Python", "Spring", "Spring Boot", "Django", "SQL", "MySQL",
                        "PostgreSQL", "MongoDB",
                        "REST API", "GraphQL", "Docker", "Kubernetes", "AWS"),
                skillMap,
                now));

        positions.add(createJobPosition(
                "java_backend_developer",
                JobRole.BACKEND_DEVELOPER,
                "Java 백엔드 개발자",
                "Java Backend Developer",
                "Java 기반의 엔터프라이즈 백엔드 시스템 개발 및 유지보수",
                "Development and maintenance of Java-based enterprise backend systems",
                "FontAwesomeIcons.java",
                developmentCategory,
                List.of("Java", "Spring", "Spring Boot", "JPA", "Hibernate", "MySQL", "Oracle",
                        "RESTful API", "JWT",
                        "Redis", "Kafka", "JUnit"),
                skillMap,
                now));

        positions.add(createJobPosition(
                "python_backend_developer",
                JobRole.BACKEND_DEVELOPER,
                "Python 백엔드 개발자",
                "Python Backend Developer",
                "Python 기반의 웹 애플리케이션 및 API 서비스 개발",
                "Development of Python-based web applications and API services",
                "FontAwesomeIcons.python",
                developmentCategory,
                List.of("Python", "Django", "Flask", "FastAPI", "PostgreSQL", "SQLAlchemy", "REST API",
                        "Celery",
                        "Redis", "AWS", "Docker", "pytest"),
                skillMap,
                now));

        // ===== 프론트엔드 개발 직무 =====
        positions.add(createJobPosition(
                "frontend_developer",
                JobRole.FRONTEND_DEVELOPER,
                "프론트엔드 개발자",
                "Frontend Developer",
                "웹 사이트, 애플리케이션의 사용자 인터페이스 및 사용자 경험 개발",
                "Developing user interfaces and user experiences for websites and applications",
                "FontAwesomeIcons.display",
                developmentCategory,
                List.of("JavaScript", "TypeScript", "React", "Vue.js", "Angular", "HTML5", "CSS3",
                        "Tailwind CSS",
                        "Next.js", "Redux", "Webpack", "Jest"),
                skillMap,
                now));

        positions.add(createJobPosition(
                "react_frontend_developer",
                JobRole.FRONTEND_DEVELOPER,
                "React 프론트엔드 개발자",
                "React Frontend Developer",
                "React 기반의 웹 애플리케이션 및 컴포넌트 개발",
                "Development of React-based web applications and components",
                "FontAwesomeIcons.react",
                developmentCategory,
                List.of("JavaScript", "TypeScript", "React", "Redux", "Next.js", "React Query", "HTML5",
                        "CSS3",
                        "Styled Components", "Tailwind", "Jest", "React Testing Library"),
                skillMap,
                now));

        // ===== 풀스택 개발 직무 =====
        positions.add(createJobPosition(
                "fullstack_developer",
                JobRole.FULLSTACK_DEVELOPER,
                "풀스택 개발자",
                "Full Stack Developer",
                "프론트엔드와 백엔드를 모두 개발할 수 있는 개발자",
                "Developer capable of handling both frontend and backend development",
                "FontAwesomeIcons.layerGroup",
                developmentCategory,
                List.of("JavaScript", "TypeScript", "React", "Node.js", "Express.js", "MongoDB",
                        "MySQL", "Redis",
                        "REST API", "GraphQL", "Docker", "Git"),
                skillMap,
                now));

        positions.add(createJobPosition(
                "mern_stack_developer",
                JobRole.FULLSTACK_DEVELOPER,
                "MERN 스택 개발자",
                "MERN Stack Developer",
                "MongoDB, Express, React, Node.js 기반의 웹 애플리케이션 개발",
                "Development of web applications using MongoDB, Express, React, and Node.js",
                "FontAwesomeIcons.reacteurope",
                developmentCategory,
                List.of("MongoDB", "Express.js", "React", "Node.js", "JavaScript", "REST API", "Redux",
                        "JWT", "AWS",
                        "Docker", "Mongoose", "Git"),
                skillMap,
                now));

        // ===== 모바일 앱 개발 직무 =====
        positions.add(createJobPosition(
                "android_developer",
                JobRole.MOBILE_DEVELOPER,
                "Android 개발자",
                "Android Developer",
                "Android 플랫폼용 모바일 애플리케이션 개발",
                "Development of mobile applications for the Android platform",
                "FontAwesomeIcons.android",
                developmentCategory,
                List.of("Android", "Kotlin", "Java", "XML", "Android SDK", "Jetpack", "Room",
                        "Retrofit", "Coroutines",
                        "MVVM", "Firebase", "Gradle"),
                skillMap,
                now));

        positions.add(createJobPosition(
                "ios_developer",
                JobRole.MOBILE_DEVELOPER,
                "iOS 개발자",
                "iOS Developer",
                "iOS 플랫폼용 모바일 애플리케이션 개발",
                "Development of mobile applications for the iOS platform",
                "FontAwesomeIcons.apple",
                developmentCategory,
                List.of("iOS", "Swift", "SwiftUI", "UIKit", "Core Data", "Combine", "XCode",
                        "CocoaPods", "MVVM",
                        "RESTful API", "Firebase", "TestFlight"),
                skillMap,
                now));

        // ===== DevOps 직무 =====
        positions.add(createJobPosition(
                "devops_engineer",
                JobRole.DEVOPS_DEVELOPER,
                "DevOps 엔지니어",
                "DevOps Engineer",
                "개발과 운영을 통합하여 소프트웨어 개발 및 배포 프로세스를 자동화",
                "Automating software development and deployment processes by integrating development and operations",
                "FontAwesomeIcons.gears",
                developmentCategory,
                List.of("Docker", "Kubernetes", "AWS", "CI/CD", "Jenkins", "GitLab CI", "Terraform",
                        "Ansible",
                        "Prometheus", "Grafana", "ELK Stack", "Shell Scripting"),
                skillMap,
                now));

        positions.add(createJobPosition(
                "cloud_engineer",
                JobRole.DEVOPS_DEVELOPER,
                "클라우드 엔지니어",
                "Cloud Engineer",
                "클라우드 인프라 설계, 구축 및 관리",
                "Designing, building, and managing cloud infrastructure",
                "FontAwesomeIcons.cloud",
                developmentCategory,
                List.of("AWS", "Azure", "GCP", "Terraform", "CloudFormation", "Kubernetes", "Docker",
                        "Networking",
                        "Security", "IAM", "Load Balancing", "Auto Scaling"),
                skillMap,
                now));

        // ===== 데이터 관련 직무 =====
        positions.add(createJobPosition(
                "data_scientist",
                JobRole.DATA_SCIENTIST,
                "데이터 사이언티스트",
                "Data Scientist",
                "데이터를 분석하고 모델링하여 비즈니스 인사이트 도출",
                "Analyzing and modeling data to derive business insights",
                "FontAwesomeIcons.chartLine",
                developmentCategory,
                List.of("Python", "R", "SQL", "Machine Learning", "Deep Learning", "TensorFlow",
                        "PyTorch", "Pandas",
                        "NumPy", "Scikit-learn", "Data Visualization", "Statistics"),
                skillMap,
                now));

        positions.add(createJobPosition(
                "machine_learning_engineer",
                JobRole.AI_ENGINEER,
                "머신러닝 엔지니어",
                "Machine Learning Engineer",
                "머신러닝 모델의 개발, 배포 및 유지 관리",
                "Development, deployment, and maintenance of machine learning models",
                "FontAwesomeIcons.brain",
                developmentCategory,
                List.of("Python", "TensorFlow", "PyTorch", "Scikit-learn", "MLOps", "Docker",
                        "Kubernetes", "SQL",
                        "Model Training", "Model Deployment", "Cloud ML Tools",
                        "Deep Learning"),
                skillMap,
                now));

        // ===== QA 직무 =====
        positions.add(createJobPosition(
                "qa_engineer",
                JobRole.QA_ENGINEER,
                "QA 엔지니어",
                "QA Engineer",
                "소프트웨어 품질을 보장하기 위한 테스트 및 품질 관리",
                "Testing and quality assurance to ensure software quality",
                "FontAwesomeIcons.checkSquare",
                developmentCategory,
                List.of("Selenium", "JUnit", "TestNG", "JIRA", "Postman", "BDD", "TDD", "TestRail",
                        "Performance Testing",
                        "Manual Testing", "API Testing", "Automated Testing"),
                skillMap,
                now));

        // ===== 보안 직무 =====
        positions.add(createJobPosition(
                "security_engineer",
                JobRole.SECURITY_ENGINEER,
                "보안 엔지니어",
                "Security Engineer",
                "애플리케이션 및 인프라의 보안 설계, 구현 및 관리",
                "Security design, implementation, and management of applications and infrastructure",
                "FontAwesomeIcons.shieldAlt",
                developmentCategory,
                List.of("Network Security", "Cloud Security", "SIEM", "Vulnerability Assessment",
                        "Penetration Testing",
                        "WAF", "IAM", "Security Protocols", "Encryption", "OWASP",
                        "Secure Coding", "Incident Response"),
                skillMap,
                now));

        jobPositionRepository.saveAll(positions);
        log.info("{}개의 직무 포지션 데이터가 초기화되었습니다.", positions.size());
    }

    // JobPosition 생성 헬퍼 메서드
    private JobPosition createJobPosition(
            String positionId,
            JobRole role,
            String title,
            String titleEn,
            String description,
            String descriptionEn,
            String icon,
            InterviewCategory category,
            List<String> skillNames,
            Map<String, Skill> skillMap,
            LocalDateTime now) {

        JobPosition position = JobPosition.builder()
                .positionId(positionId)
                .role(role)
                .title(title)
                .titleEn(titleEn)
                .description(description)
                .descriptionEn(descriptionEn)
                .icon(icon)
                .createdAt(now)
                .updatedAt(now)
                .build();

        position.setCategory(category);

        // 스킬 연결
        skillNames.forEach(skillName -> {
            Skill skill = skillMap.get(skillName);
            if (skill != null) {
                position.getSkills().add(skill);
            }
        });

        return position;
    }

    private void initializePlans() {
        if (planRepository.count() > 0) {
            log.info("플랜 데이터가 이미 존재합니다. 초기화를 건너뜁니다.");
            return;
        }

        // FREE 플랜 생성
        Plan freePlan = Plan.createPlan(
                "무료",
                PlanType.FREE,
                0,
                0,
                10000,
                true);

        // STANDARD 플랜 생성
        Plan standardPlan = Plan.createPlan(
                "스탠다드",
                PlanType.STANDARD,
                9900,
                99000,
                100000,
                true);

        // PRO 플랜 생성
        Plan proPlan = Plan.createPlan(
                "프로",
                PlanType.PRO,
                19900,
                199000,
                1000000,
                true);

        // 세 가지 플랜 저장
        planRepository.saveAll(List.of(freePlan, standardPlan, proPlan));

        log.info("총 {}개의 플랜 데이터가 초기화되었습니다.", 3);
    }
}