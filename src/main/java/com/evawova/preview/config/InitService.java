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
import com.evawova.preview.domain.user.service.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InitService {

    private final UserService userService;
    private final InterviewPromptRepository interviewPromptRepository;
    private final InterviewCategoryRepository interviewCategoryRepository;
    private final JobPositionRepository jobPositionRepository;
    private final SkillRepository skillRepository;

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

        // 기존 prompts 추가 로직...
        interviewPromptRepository.saveAll(prompts);
        log.info("{}개의 프롬프트 데이터가 초기화되었습니다.", prompts.size());
    }

    private void initializeInterviewCategories() {
        if (interviewCategoryRepository.count() > 0) {
            log.info("인터뷰 카테고리 데이터가 이미 존재합니다. 초기화를 건너뜁니다.");
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        List<InterviewCategory> categories = new ArrayList<>();

        // IT 개발 직군 세분화
        categories.add(InterviewCategory.builder()
                .icon("Icons.code")
                .title("백엔드 개발")
                .titleEn("Backend Development")
                .description("서버, API, 데이터베이스 설계 및 개발을 담당하는 직군")
                .descriptionEn("Roles responsible for server-side logic, API development, and database design")
                .type(InterviewType.BACKEND)
                .createdAt(now)
                .updatedAt(now)
                .build());

        categories.add(InterviewCategory.builder()
                .icon("Icons.web")
                .title("프론트엔드 개발")
                .titleEn("Frontend Development")
                .description("웹/앱 인터페이스 및 사용자 경험 구현을 담당하는 직군")
                .descriptionEn("Roles responsible for implementing user interfaces and experiences")
                .type(InterviewType.FRONTEND)
                .createdAt(now)
                .updatedAt(now)
                .build());

        categories.add(InterviewCategory.builder()
                .icon("Icons.all_inclusive")
                .title("풀스택 개발")
                .titleEn("Full Stack Development")
                .description("프론트엔드와 백엔드 모두 개발 가능한 직군")
                .descriptionEn("Roles covering both frontend and backend development")
                .type(InterviewType.FULLSTACK)
                .createdAt(now)
                .updatedAt(now)
                .build());

        categories.add(InterviewCategory.builder()
                .icon("Icons.phone_android")
                .title("모바일 앱 개발")
                .titleEn("Mobile App Development")
                .description("iOS, Android, 크로스 플랫폼 앱 개발을 담당하는 직군")
                .descriptionEn("Roles focused on iOS, Android, and cross-platform app development")
                .type(InterviewType.MOBILE)
                .createdAt(now)
                .updatedAt(now)
                .build());

        categories.add(InterviewCategory.builder()
                .icon("Icons.cloud")
                .title("데브옵스/인프라")
                .titleEn("DevOps/Infrastructure")
                .description("개발 및 운영 자동화, 클라우드 인프라 관리를 담당하는 직군")
                .descriptionEn("Roles managing automation, deployment, and cloud infrastructure")
                .type(InterviewType.DEVOPS)
                .createdAt(now)
                .updatedAt(now)
                .build());

        categories.add(InterviewCategory.builder()
                .icon("Icons.storage")
                .title("데이터 엔지니어링")
                .titleEn("Data Engineering")
                .description("데이터 파이프라인 구축, ETL 프로세스, 데이터 웨어하우스 관리를 담당하는 직군")
                .descriptionEn("Roles building data pipelines, ETL processes, and managing data warehouses")
                .type(InterviewType.DATA_ENGINEERING)
                .createdAt(now)
                .updatedAt(now)
                .build());

        categories.add(InterviewCategory.builder()
                .icon("Icons.analytics")
                .title("데이터 사이언스/AI")
                .titleEn("Data Science/AI")
                .description("머신러닝, 딥러닝, 데이터 분석, AI 모델 개발을 담당하는 직군")
                .descriptionEn("Roles focused on machine learning, deep learning, and AI model development")
                .type(InterviewType.DATA_SCIENCE)
                .createdAt(now)
                .updatedAt(now)
                .build());

        categories.add(InterviewCategory.builder()
                .icon("Icons.sports_esports")
                .title("게임 개발")
                .titleEn("Game Development")
                .description("게임 클라이언트/서버 개발, 그래픽 엔진 활용을 담당하는 직군")
                .descriptionEn("Roles developing game clients, servers, and utilizing graphics engines")
                .type(InterviewType.GAME_DEVELOPMENT)
                .createdAt(now)
                .updatedAt(now)
                .build());

        categories.add(InterviewCategory.builder()
                .icon("Icons.link")
                .title("블록체인 개발")
                .titleEn("Blockchain Development")
                .description("블록체인 프로토콜, 스마트 컨트랙트, DApp 개발을 담당하는 직군")
                .descriptionEn("Roles developing blockchain protocols, smart contracts, and decentralized applications")
                .type(InterviewType.BLOCKCHAIN)
                .createdAt(now)
                .updatedAt(now)
                .build());

        categories.add(InterviewCategory.builder()
                .icon("Icons.memory")
                .title("임베디드/IoT")
                .titleEn("Embedded/IoT")
                .description("임베디드 시스템, IoT 기기, 펌웨어 개발을 담당하는 직군")
                .descriptionEn("Roles developing embedded systems, IoT devices, and firmware")
                .type(InterviewType.EMBEDDED)
                .createdAt(now)
                .updatedAt(now)
                .build());

        categories.add(InterviewCategory.builder()
                .icon("Icons.bug_report")
                .title("QA/테스트")
                .titleEn("QA/Testing")
                .description("품질 보증, 자동화 테스트, 테스트 프레임워크 개발을 담당하는 직군")
                .descriptionEn("Roles ensuring quality assurance and developing test automation")
                .type(InterviewType.QA)
                .createdAt(now)
                .updatedAt(now)
                .build());

        categories.add(InterviewCategory.builder()
                .icon("Icons.security")
                .title("보안 엔지니어링")
                .titleEn("Security Engineering")
                .description("어플리케이션 보안, 인프라 보안, 취약점 분석을 담당하는 직군")
                .descriptionEn(
                        "Roles focused on application security, infrastructure security, and vulnerability analysis")
                .type(InterviewType.SECURITY)
                .createdAt(now)
                .updatedAt(now)
                .build());

        // 기존 카테고리 (디자인, 마케팅, 경영지원)
        categories.add(InterviewCategory.builder()
                .icon("Icons.design_services")
                .title("디자인")
                .titleEn("Design")
                .description("UI/UX, 그래픽, 제품 디자인 직군")
                .descriptionEn("UI/UX, graphic, and product design roles")
                .type(InterviewType.DESIGN)
                .createdAt(now)
                .updatedAt(now)
                .build());

        categories.add(InterviewCategory.builder()
                .icon("Icons.campaign")
                .title("마케팅")
                .titleEn("Marketing")
                .description("디지털 마케팅, 콘텐츠 기획 직군")
                .descriptionEn("Digital marketing and content planning roles")
                .type(InterviewType.MARKETING)
                .createdAt(now)
                .updatedAt(now)
                .build());

        categories.add(InterviewCategory.builder()
                .icon("Icons.business")
                .title("경영지원")
                .titleEn("Business Operations")
                .description("인사, 재무, 행정 지원 직군")
                .descriptionEn("HR, finance, and administrative support roles")
                .type(InterviewType.BUSINESS)
                .createdAt(now)
                .updatedAt(now)
                .build());

        interviewCategoryRepository.saveAll(categories);
        log.info("{}개의 인터뷰 카테고리 데이터가 초기화되었습니다.", categories.size());
    }

    private void initializeSkills() {
        if (skillRepository.count() > 0) {
            log.info("스킬 데이터가 이미 존재합니다. 초기화를 건너뜁니다.");
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        List<Skill> skills = new ArrayList<>();

        // 개발 관련 스킬 - 백엔드
        skills.add(createSkill("자바", "Java", now));
        skills.add(createSkill("파이썬", "Python", now));
        skills.add(createSkill("자바스크립트", "JavaScript", now));
        skills.add(createSkill("타입스크립트", "TypeScript", now));
        skills.add(createSkill("C#", "C#", now));
        skills.add(createSkill("PHP", "PHP", now));
        skills.add(createSkill("고", "Go", now));
        skills.add(createSkill("루비", "Ruby", now));
        skills.add(createSkill("코틀린", "Kotlin", now));
        skills.add(createSkill("스칼라", "Scala", now));
        skills.add(createSkill("러스트", "Rust", now));

        // 백엔드 프레임워크
        skills.add(createSkill("스프링", "Spring", now));
        skills.add(createSkill("스프링 부트", "Spring Boot", now));
        skills.add(createSkill("장고", "Django", now));
        skills.add(createSkill("플라스크", "Flask", now));
        skills.add(createSkill("익스프레스", "Express.js", now));
        skills.add(createSkill("FastAPI", "FastAPI", now));
        skills.add(createSkill("라라벨", "Laravel", now));
        skills.add(createSkill("ASP.NET Core", "ASP.NET Core", now));
        skills.add(createSkill("스프링 시큐리티", "Spring Security", now));
        skills.add(createSkill("스프링 데이터", "Spring Data", now));
        skills.add(createSkill("스프링 클라우드", "Spring Cloud", now));

        // 데이터베이스
        skills.add(createSkill("SQL", "SQL", now));
        skills.add(createSkill("MySQL", "MySQL", now));
        skills.add(createSkill("PostgreSQL", "PostgreSQL", now));
        skills.add(createSkill("MongoDB", "MongoDB", now));
        skills.add(createSkill("Redis", "Redis", now));
        skills.add(createSkill("엘라스틱서치", "Elasticsearch", now));
        skills.add(createSkill("오라클", "Oracle", now));
        skills.add(createSkill("MS SQL 서버", "MS SQL Server", now));
        skills.add(createSkill("Firebase", "Firebase", now));
        skills.add(createSkill("DynamoDB", "DynamoDB", now));
        skills.add(createSkill("카산드라", "Cassandra", now));

        // ORM
        skills.add(createSkill("JPA", "JPA", now));
        skills.add(createSkill("하이버네이트", "Hibernate", now));
        skills.add(createSkill("MyBatis", "MyBatis", now));
        skills.add(createSkill("Sequelize", "Sequelize", now));
        skills.add(createSkill("TypeORM", "TypeORM", now));
        skills.add(createSkill("Prisma", "Prisma", now));

        // API 개발
        skills.add(createSkill("REST API", "REST API", now));
        skills.add(createSkill("GraphQL", "GraphQL", now));
        skills.add(createSkill("gRPC", "gRPC", now));
        skills.add(createSkill("웹소켓", "WebSockets", now));
        skills.add(createSkill("소켓.IO", "Socket.IO", now));
        skills.add(createSkill("스웨거", "Swagger", now));
        skills.add(createSkill("OpenAPI", "OpenAPI", now));

        // 서버/인프라
        skills.add(createSkill("도커", "Docker", now));
        skills.add(createSkill("쿠버네티스", "Kubernetes", now));
        skills.add(createSkill("AWS", "AWS", now));
        skills.add(createSkill("GCP", "GCP", now));
        skills.add(createSkill("Azure", "Azure", now));
        skills.add(createSkill("Nginx", "Nginx", now));
        skills.add(createSkill("Apache", "Apache", now));
        skills.add(createSkill("리눅스", "Linux", now));
        skills.add(createSkill("테라폼", "Terraform", now));
        skills.add(createSkill("앤서블", "Ansible", now));
        skills.add(createSkill("CloudFormation", "CloudFormation", now));

        // 메시징/비동기 처리
        skills.add(createSkill("카프카", "Kafka", now));
        skills.add(createSkill("RabbitMQ", "RabbitMQ", now));
        skills.add(createSkill("ActiveMQ", "ActiveMQ", now));
        skills.add(createSkill("Redis Pub/Sub", "Redis Pub/Sub", now));
        skills.add(createSkill("이벤트 소싱", "Event Sourcing", now));
        skills.add(createSkill("CQRS", "CQRS", now));

        // 테스트
        skills.add(createSkill("JUnit", "JUnit", now));
        skills.add(createSkill("모킹", "Mockito", now));
        skills.add(createSkill("pytest", "pytest", now));
        skills.add(createSkill("Jest", "Jest", now));
        skills.add(createSkill("테스트 컨테이너", "Testcontainers", now));
        skills.add(createSkill("통합 테스트", "Integration Testing", now));
        skills.add(createSkill("E2E 테스트", "E2E Testing", now));

        // 인증/보안
        skills.add(createSkill("OAuth 2.0", "OAuth 2.0", now));
        skills.add(createSkill("JWT", "JWT", now));
        skills.add(createSkill("인증", "Authentication", now));
        skills.add(createSkill("권한 부여", "Authorization", now));
        skills.add(createSkill("OIDC", "OIDC", now));
        skills.add(createSkill("보안 모범 사례", "Security Best Practices", now));

        // 개발 관련 스킬 - 프론트엔드
        skills.add(createSkill("HTML5", "HTML5", now));
        skills.add(createSkill("CSS3", "CSS3", now));
        skills.add(createSkill("리액트", "React", now));
        skills.add(createSkill("뷰", "Vue.js", now));
        skills.add(createSkill("앵귤러", "Angular", now));
        skills.add(createSkill("스벨트", "Svelte", now));
        skills.add(createSkill("넥스트.js", "Next.js", now));
        skills.add(createSkill("넉스트.js", "Nuxt.js", now));
        skills.add(createSkill("개츠비", "Gatsby", now));
        skills.add(createSkill("리믹스", "Remix", now));
        skills.add(createSkill("SolidJS", "SolidJS", now));

        // 상태 관리
        skills.add(createSkill("리덕스", "Redux", now));
        skills.add(createSkill("리덕스 툴킷", "Redux Toolkit", now));
        skills.add(createSkill("리코일", "Recoil", now));
        skills.add(createSkill("주스탄트", "Zustand", now));
        skills.add(createSkill("MobX", "MobX", now));
        skills.add(createSkill("컨텍스트 API", "Context API", now));
        skills.add(createSkill("XState", "XState", now));
        skills.add(createSkill("Jotai", "Jotai", now));
        skills.add(createSkill("Pinia", "Pinia", now));
        skills.add(createSkill("Vuex", "Vuex", now));
        skills.add(createSkill("Tanstack Query", "Tanstack Query", now));
        skills.add(createSkill("SWR", "SWR", now));

        // CSS 및 스타일링
        skills.add(createSkill("Sass/SCSS", "Sass/SCSS", now));
        skills.add(createSkill("Less", "Less", now));
        skills.add(createSkill("스타일드 컴포넌트", "Styled Components", now));
        skills.add(createSkill("이모션", "Emotion", now));
        skills.add(createSkill("테일윈드 CSS", "Tailwind CSS", now));
        skills.add(createSkill("CSS 모듈", "CSS Modules", now));
        skills.add(createSkill("부트스트랩", "Bootstrap", now));
        skills.add(createSkill("머티리얼 UI", "Material UI", now));
        skills.add(createSkill("차크라 UI", "Chakra UI", now));
        skills.add(createSkill("Ant Design", "Ant Design", now));
        skills.add(createSkill("CSS-in-JS", "CSS-in-JS", now));

        // 프론트엔드 테스트
        skills.add(createSkill("리액트 테스팅 라이브러리", "React Testing Library", now));
        skills.add(createSkill("사이프레스", "Cypress", now));
        skills.add(createSkill("플레이라이트", "Playwright", now));
        skills.add(createSkill("비테스트", "Vitest", now));
        skills.add(createSkill("스토리북", "Storybook", now));
        skills.add(createSkill("MSW", "MSW", now));

        // 빌드 도구 및 번들러
        skills.add(createSkill("웹팩", "Webpack", now));
        skills.add(createSkill("바이트", "Vite", now));
        skills.add(createSkill("바벨", "Babel", now));
        skills.add(createSkill("이에스빌드", "esbuild", now));
        skills.add(createSkill("롤업", "Rollup", now));
        skills.add(createSkill("파셀", "Parcel", now));
        skills.add(createSkill("터보팩", "Turbopack", now));
        skills.add(createSkill("Lerna", "Lerna", now));
        skills.add(createSkill("NX", "NX", now));

        // 웹 성능 최적화
        skills.add(createSkill("코드 스플리팅", "Code Splitting", now));
        skills.add(createSkill("지연 로딩", "Lazy Loading", now));
        skills.add(createSkill("메모이제이션", "Memoization", now));
        skills.add(createSkill("번들 분석", "Bundle Analysis", now));
        skills.add(createSkill("이미지 최적화", "Image Optimization", now));
        skills.add(createSkill("서버 사이드 렌더링", "Server-side Rendering", now));
        skills.add(createSkill("정적 사이트 생성", "Static Site Generation", now));
        skills.add(createSkill("점진적 향상", "Progressive Enhancement", now));

        // 웹 표준/접근성
        skills.add(createSkill("반응형 디자인", "Responsive Design", now));
        skills.add(createSkill("접근성", "Accessibility (a11y)", now));
        skills.add(createSkill("ARIA", "ARIA", now));
        skills.add(createSkill("PWA", "PWA", now));
        skills.add(createSkill("웹 컴포넌트", "Web Components", now));
        skills.add(createSkill("시맨틱 HTML", "Semantic HTML", now));
        skills.add(createSkill("WCAG", "WCAG", now));

        // 모바일 개발 스킬
        skills.add(createSkill("안드로이드", "Android", now));
        skills.add(createSkill("iOS", "iOS", now));
        skills.add(createSkill("스위프트", "Swift", now));
        skills.add(createSkill("코틀린 안드로이드", "Kotlin for Android", now));
        skills.add(createSkill("리액트 네이티브", "React Native", now));
        skills.add(createSkill("플러터", "Flutter", now));
        skills.add(createSkill("다트", "Dart", now));
        skills.add(createSkill("Ionic", "Ionic", now));
        skills.add(createSkill("Capacitor", "Capacitor", now));
        skills.add(createSkill("SwiftUI", "SwiftUI", now));
        skills.add(createSkill("Jetpack Compose", "Jetpack Compose", now));
        skills.add(createSkill("Objective-C", "Objective-C", now));

        // 디자인 관련 스킬 - UI/UX 디자인
        skills.add(createSkill("피그마", "Figma", now));
        skills.add(createSkill("스케치", "Sketch", now));
        skills.add(createSkill("어도비 XD", "Adobe XD", now));
        skills.add(createSkill("프로토파이", "Protopie", now));
        skills.add(createSkill("ProtoPie", "ProtoPie", now));
        skills.add(createSkill("인비전", "InVision", now));
        skills.add(createSkill("Axure RP", "Axure RP", now));
        skills.add(createSkill("프레이머", "Framer", now));
        skills.add(createSkill("원노트", "Whimsical", now));

        // 그래픽 디자인
        skills.add(createSkill("포토샵", "Photoshop", now));
        skills.add(createSkill("일러스트레이터", "Illustrator", now));
        skills.add(createSkill("어도비 애프터 이펙트", "Adobe After Effects", now));
        skills.add(createSkill("애니메이션", "Animation", now));
        skills.add(createSkill("모션 그래픽", "Motion Graphics", now));
        skills.add(createSkill("타이포그래피", "Typography", now));
        skills.add(createSkill("컬러 이론", "Color Theory", now));
        skills.add(createSkill("로고 디자인", "Logo Design", now));
        skills.add(createSkill("아이콘 디자인", "Icon Design", now));

        // UX 디자인
        skills.add(createSkill("UI 디자인", "UI Design", now));
        skills.add(createSkill("UX 디자인", "UX Design", now));
        skills.add(createSkill("사용자 리서치", "User Research", now));
        skills.add(createSkill("사용성 테스트", "Usability Testing", now));
        skills.add(createSkill("인터랙션 디자인", "Interaction Design", now));
        skills.add(createSkill("와이어프레이밍", "Wireframing", now));
        skills.add(createSkill("프로토타이핑", "Prototyping", now));
        skills.add(createSkill("정보 구조", "Information Architecture", now));
        skills.add(createSkill("사용자 경험 매핑", "User Journey Mapping", now));
        skills.add(createSkill("인물 페르소나", "Persona Creation", now));
        skills.add(createSkill("A/B 테스트", "A/B Testing", now));
        skills.add(createSkill("모바일 디자인", "Mobile Design", now));
        skills.add(createSkill("웹 디자인", "Web Design", now));

        // 디자인 시스템
        skills.add(createSkill("디자인 시스템", "Design Systems", now));
        skills.add(createSkill("브랜드 디자인", "Brand Design", now));
        skills.add(createSkill("브랜드 가이드라인", "Brand Guidelines", now));
        skills.add(createSkill("스타일 가이드", "Style Guides", now));
        skills.add(createSkill("컴포넌트 라이브러리", "Component Libraries", now));
        skills.add(createSkill("디자인 토큰", "Design Tokens", now));

        // 마케팅 관련 스킬
        skills.add(createSkill("검색 엔진 최적화", "SEO", now));
        skills.add(createSkill("검색 엔진 마케팅", "SEM", now));
        skills.add(createSkill("소셜 미디어 마케팅", "Social Media Marketing", now));
        skills.add(createSkill("콘텐츠 마케팅", "Content Marketing", now));
        skills.add(createSkill("인플루언서 마케팅", "Influencer Marketing", now));
        skills.add(createSkill("이메일 마케팅", "Email Marketing", now));
        skills.add(createSkill("디지털 광고", "Digital Advertising", now));
        skills.add(createSkill("마케팅 자동화", "Marketing Automation", now));
        skills.add(createSkill("퍼포먼스 마케팅", "Performance Marketing", now));
        skills.add(createSkill("애널리틱스", "Analytics", now));
        skills.add(createSkill("그로스 해킹", "Growth Hacking", now));
        skills.add(createSkill("브랜드 마케팅", "Brand Marketing", now));
        skills.add(createSkill("CRM", "CRM", now));

        // 마케팅 플랫폼/툴
        skills.add(createSkill("구글 애널리틱스", "Google Analytics", now));
        skills.add(createSkill("구글 태그 매니저", "Google Tag Manager", now));
        skills.add(createSkill("구글 애즈", "Google Ads", now));
        skills.add(createSkill("페이스북 광고", "Facebook Ads", now));
        skills.add(createSkill("인스타그램 광고", "Instagram Ads", now));
        skills.add(createSkill("트위터 광고", "Twitter Ads", now));
        skills.add(createSkill("링크드인 광고", "LinkedIn Ads", now));
        skills.add(createSkill("틱톡 광고", "TikTok Ads", now));
        skills.add(createSkill("훅", "Hootsuite", now));
        skills.add(createSkill("버퍼", "Buffer", now));
        skills.add(createSkill("메일침프", "Mailchimp", now));
        skills.add(createSkill("센드그리드", "SendGrid", now));
        skills.add(createSkill("HubSpot", "HubSpot", now));
        skills.add(createSkill("Marketo", "Marketo", now));
        skills.add(createSkill("세일즈포스", "Salesforce", now));
        skills.add(createSkill("아하프스", "Ahrefs", now));
        skills.add(createSkill("SEMrush", "SEMrush", now));
        skills.add(createSkill("Moz", "Moz", now));

        // 데이터 분석
        skills.add(createSkill("데이터 분석", "Data Analysis", now));
        skills.add(createSkill("GA4", "GA4", now));
        skills.add(createSkill("핫자", "Hotjar", now));
        skills.add(createSkill("크레이지 에그", "Crazy Egg", now));
        skills.add(createSkill("컨버전 최적화", "Conversion Rate Optimization", now));
        skills.add(createSkill("키워드 리서치", "Keyword Research", now));
        skills.add(createSkill("마케팅 ROI", "Marketing ROI", now));
        skills.add(createSkill("대시보드", "Dashboards", now));
        skills.add(createSkill("ABM", "Account-based Marketing", now));
        skills.add(createSkill("퍼널 분석", "Funnel Analysis", now));

        // 경영 관련 스킬
        skills.add(createSkill("프로젝트 관리", "Project Management", now));
        skills.add(createSkill("애자일", "Agile", now));
        skills.add(createSkill("스크럼", "Scrum", now));
        skills.add(createSkill("칸반", "Kanban", now));
        skills.add(createSkill("린", "Lean", now));
        skills.add(createSkill("워터폴", "Waterfall", now));
        skills.add(createSkill("PMO", "PMO", now));
        skills.add(createSkill("리스크 관리", "Risk Management", now));
        skills.add(createSkill("문제 해결", "Problem Solving", now));
        skills.add(createSkill("비즈니스 전략", "Business Strategy", now));
        skills.add(createSkill("시장 조사", "Market Research", now));
        skills.add(createSkill("데이터 기반 의사결정", "Data-driven Decision Making", now));

        // 경영 도구
        skills.add(createSkill("지라", "Jira", now));
        skills.add(createSkill("컨플루언스", "Confluence", now));
        skills.add(createSkill("아사나", "Asana", now));
        skills.add(createSkill("트렐로", "Trello", now));
        skills.add(createSkill("먼데이닷컴", "Monday.com", now));
        skills.add(createSkill("노션", "Notion", now));
        skills.add(createSkill("클릭업", "ClickUp", now));
        skills.add(createSkill("마이크로소프트 프로젝트", "Microsoft Project", now));
        skills.add(createSkill("슬랙", "Slack", now));
        skills.add(createSkill("마이크로소프트 팀즈", "Microsoft Teams", now));
        skills.add(createSkill("줌", "Zoom", now));
        skills.add(createSkill("미로", "Miro", now));

        // 인사/조직
        skills.add(createSkill("인사 관리", "HR Management", now));
        skills.add(createSkill("채용", "Recruiting", now));
        skills.add(createSkill("온보딩", "Onboarding", now));
        skills.add(createSkill("인재 개발", "Talent Development", now));
        skills.add(createSkill("성과 관리", "Performance Management", now));
        skills.add(createSkill("문화 구축", "Culture Building", now));
        skills.add(createSkill("리더십", "Leadership", now));
        skills.add(createSkill("팀 관리", "Team Management", now));
        skills.add(createSkill("갈등 해결", "Conflict Resolution", now));
        skills.add(createSkill("코칭", "Coaching", now));
        skills.add(createSkill("다양성과 포용", "Diversity & Inclusion", now));
        skills.add(createSkill("원격 팀 관리", "Remote Team Management", now));

        // 재무/운영
        skills.add(createSkill("재무 분석", "Financial Analysis", now));
        skills.add(createSkill("예산 관리", "Budget Management", now));
        skills.add(createSkill("원가 분석", "Cost Analysis", now));
        skills.add(createSkill("수익 모델링", "Revenue Modeling", now));
        skills.add(createSkill("비즈니스 모델 개발", "Business Model Development", now));
        skills.add(createSkill("운영 관리", "Operations Management", now));
        skills.add(createSkill("공급망 관리", "Supply Chain Management", now));
        skills.add(createSkill("린 운영", "Lean Operations", now));
        skills.add(createSkill("품질 관리", "Quality Management", now));
        skills.add(createSkill("벤더 관리", "Vendor Management", now));
        skills.add(createSkill("계약 협상", "Contract Negotiation", now));

        // 데이터 과학/AI
        skills.add(createSkill("데이터 과학", "Data Science", now));
        skills.add(createSkill("머신러닝", "Machine Learning", now));
        skills.add(createSkill("인공지능", "Artificial Intelligence", now));
        skills.add(createSkill("딥러닝", "Deep Learning", now));
        skills.add(createSkill("자연어 처리", "Natural Language Processing", now));
        skills.add(createSkill("데이터 시각화", "Data Visualization", now));
        skills.add(createSkill("통계 분석", "Statistical Analysis", now));
        skills.add(createSkill("예측 모델링", "Predictive Modeling", now));
        skills.add(createSkill("판다스", "Pandas", now));
        skills.add(createSkill("넘파이", "NumPy", now));
        skills.add(createSkill("텐서플로우", "TensorFlow", now));
        skills.add(createSkill("파이토치", "PyTorch", now));
        skills.add(createSkill("생성형 AI", "Generative AI", now));
        skills.add(createSkill("프롬프트 엔지니어링", "Prompt Engineering", now));
        skills.add(createSkill("A/B 테스트 설계", "A/B Test Design", now));
        skills.add(createSkill("빅데이터", "Big Data", now));
        skills.add(createSkill("하둡", "Hadoop", now));
        skills.add(createSkill("스파크", "Spark", now));

        skillRepository.saveAll(skills);
        log.info("{}개의 스킬 데이터가 초기화되었습니다.", skills.size());
    }

    private Skill createSkill(String name, String nameEn, LocalDateTime now) {
        return Skill.builder()
                .name(name)
                .nameEn(nameEn)
                .createdAt(now)
                .updatedAt(now)
                .build();
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

        // 카테고리 맵 생성 (타입 -> 엔티티)
        Map<InterviewType, InterviewCategory> categoryMap = interviewCategoryRepository.findAll().stream()
                .collect(Collectors.toMap(InterviewCategory::getType, category -> category));

        // ===== 백엔드 개발 직무 =====
        positions.add(createJobPosition(
                "backend_developer",
                JobRole.BACKEND_DEVELOPER,
                "백엔드 개발자",
                "Backend Developer",
                "서버, API, 데이터베이스 설계 및 관리 등 서비스의 핵심 기능을 담당",
                "Responsible for server-side logic, API development, database design, and managing the core functions of services",
                "FontAwesomeIcons.server",
                categoryMap.get(InterviewType.BACKEND),
                List.of("Java", "Python", "Spring", "Spring Boot", "Django", "SQL", "MySQL", "PostgreSQL", "MongoDB",
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
                categoryMap.get(InterviewType.BACKEND),
                List.of("Java", "Spring", "Spring Boot", "JPA", "Hibernate", "MySQL", "Oracle", "RESTful API", "JWT",
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
                categoryMap.get(InterviewType.BACKEND),
                List.of("Python", "Django", "Flask", "FastAPI", "PostgreSQL", "SQLAlchemy", "REST API", "Celery",
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
                categoryMap.get(InterviewType.FRONTEND),
                List.of("JavaScript", "TypeScript", "React", "Vue.js", "Angular", "HTML5", "CSS3", "Tailwind CSS",
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
                categoryMap.get(InterviewType.FRONTEND),
                List.of("JavaScript", "TypeScript", "React", "Redux", "Next.js", "React Query", "HTML5", "CSS3",
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
                categoryMap.get(InterviewType.FULLSTACK),
                List.of("JavaScript", "TypeScript", "React", "Node.js", "Express.js", "MongoDB", "MySQL", "Redis",
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
                categoryMap.get(InterviewType.FULLSTACK),
                List.of("MongoDB", "Express.js", "React", "Node.js", "JavaScript", "REST API", "Redux", "JWT", "AWS",
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
                categoryMap.get(InterviewType.MOBILE),
                List.of("Android", "Kotlin", "Java", "XML", "Android SDK", "Jetpack", "Room", "Retrofit", "Coroutines",
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
                categoryMap.get(InterviewType.MOBILE),
                List.of("iOS", "Swift", "SwiftUI", "UIKit", "Core Data", "Combine", "XCode", "CocoaPods", "MVVM",
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
                categoryMap.get(InterviewType.DEVOPS),
                List.of("Docker", "Kubernetes", "AWS", "CI/CD", "Jenkins", "GitLab CI", "Terraform", "Ansible",
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
                categoryMap.get(InterviewType.DEVOPS),
                List.of("AWS", "Azure", "GCP", "Terraform", "CloudFormation", "Kubernetes", "Docker", "Networking",
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
                categoryMap.get(InterviewType.DATA_SCIENCE),
                List.of("Python", "R", "SQL", "Statistics", "Machine Learning", "Data Visualization", "Pandas", "NumPy",
                        "Scikit-learn", "TensorFlow", "PyTorch", "A/B Testing"),
                skillMap,
                now));

        positions.add(createJobPosition(
                "machine_learning_engineer",
                JobRole.AI_ENGINEER,
                "머신러닝 엔지니어",
                "Machine Learning Engineer",
                "머신러닝 모델 개발, 배포 및 모니터링",
                "Developing, deploying, and monitoring machine learning models",
                "FontAwesomeIcons.brain",
                categoryMap.get(InterviewType.DATA_SCIENCE),
                List.of("Python", "TensorFlow", "PyTorch", "Scikit-learn", "MLOps", "Feature Engineering",
                        "Model Deployment", "Docker", "Kubernetes", "Model Monitoring", "Deep Learning", "NLP"),
                skillMap,
                now));

        // ===== QA/테스트 직무 =====
        positions.add(createJobPosition(
                "qa_engineer",
                JobRole.QA_ENGINEER,
                "QA 엔지니어",
                "QA Engineer",
                "소프트웨어 품질 보증 및 테스트 자동화",
                "Software quality assurance and test automation",
                "FontAwesomeIcons.vial",
                categoryMap.get(InterviewType.QA),
                List.of("Test Planning", "Manual Testing", "Automated Testing", "Selenium", "Cypress", "JUnit",
                        "TestNG", "JIRA", "Test Cases", "Bug Tracking", "Performance Testing", "API Testing"),
                skillMap,
                now));

        // ===== 보안 직무 =====
        positions.add(createJobPosition(
                "security_engineer",
                JobRole.SECURITY_ENGINEER,
                "보안 엔지니어",
                "Security Engineer",
                "어플리케이션 및 인프라 보안 취약점 분석 및 대응",
                "Analyzing and responding to application and infrastructure security vulnerabilities",
                "FontAwesomeIcons.shield",
                categoryMap.get(InterviewType.SECURITY),
                List.of("Network Security", "Application Security", "Penetration Testing", "Vulnerability Assessment",
                        "OWASP", "Security Protocols", "Encryption", "Authentication", "Authorization",
                        "Security Auditing", "Security Tools", "Incident Response"),
                skillMap,
                now));

        // ===== 디자인 직무 =====
        positions.add(createJobPosition(
                "ui_ux_designer",
                JobRole.UI_UX_DESIGNER,
                "UI/UX 디자이너",
                "UI/UX Designer",
                "사용자 인터페이스/경험 디자인, 와이어프레임, 프로토타입 제작",
                "Designing user interfaces/experiences, creating wireframes and prototypes",
                "FontAwesomeIcons.penRuler",
                categoryMap.get(InterviewType.DESIGN),
                List.of("Figma", "Sketch", "Adobe XD", "Photoshop", "Illustrator", "UI Design", "UX Design",
                        "Wireframing", "Prototyping", "User Research", "Usability Testing", "Design Systems"),
                skillMap,
                now));

        positions.add(createJobPosition(
                "product_designer",
                JobRole.PRODUCT_DESIGNER,
                "제품 디자이너",
                "Product Designer",
                "사용자 중심의 디지털 제품 디자인 및 UX 프로세스 관리",
                "Designing user-centered digital products and managing UX processes",
                "FontAwesomeIcons.objectGroup",
                categoryMap.get(InterviewType.DESIGN),
                List.of("UI Design", "UX Design", "Product Thinking", "Design Systems", "Interaction Design",
                        "User Research", "Wireframing", "Prototyping", "Figma", "Design Critique", "Collaboration",
                        "A/B Testing"),
                skillMap,
                now));

        positions.add(createJobPosition(
                "graphic_designer",
                JobRole.GRAPHIC_DESIGNER,
                "그래픽 디자이너",
                "Graphic Designer",
                "시각적 콘텐츠 및 브랜드 아이덴티티 디자인",
                "Designing visual content and brand identities",
                "FontAwesomeIcons.paintBrush",
                categoryMap.get(InterviewType.DESIGN),
                List.of("Photoshop", "Illustrator", "InDesign", "Typography", "Brand Design", "Logo Design",
                        "Color Theory", "Print Design", "Digital Media", "Marketing Materials", "Composition",
                        "Visual Communication"),
                skillMap,
                now));

        // ===== 마케팅 직무 =====
        positions.add(createJobPosition(
                "digital_marketer",
                JobRole.DIGITAL_MARKETER,
                "디지털 마케터",
                "Digital Marketer",
                "온라인 마케팅 전략 수립, 캠페인 기획 및 성과 분석",
                "Developing online marketing strategies, planning campaigns, and analyzing performance",
                "FontAwesomeIcons.chartLine",
                categoryMap.get(InterviewType.MARKETING),
                List.of("SEO", "SEM", "Social Media Marketing", "Content Marketing", "Google Analytics",
                        "Growth Hacking", "Email Marketing", "Digital Advertising", "Marketing Automation", "CRM",
                        "Conversion Optimization", "A/B Testing"),
                skillMap,
                now));

        positions.add(createJobPosition(
                "content_marketer",
                JobRole.CONTENT_MARKETER,
                "콘텐츠 마케터",
                "Content Marketer",
                "브랜드 콘텐츠 전략 수립 및 양질의 콘텐츠 제작",
                "Developing brand content strategies and creating high-quality content",
                "FontAwesomeIcons.fileAlt",
                categoryMap.get(InterviewType.MARKETING),
                List.of("Content Strategy", "Blog Writing", "SEO Writing", "Social Media Content", "Video Production",
                        "Storytelling", "Brand Voice", "Content Calendar", "Content Distribution", "Analytics",
                        "Email Newsletters", "Editing"),
                skillMap,
                now));

        positions.add(createJobPosition(
                "growth_marketer",
                JobRole.GROWTH_HACKER,
                "그로스 마케터",
                "Growth Marketer",
                "데이터 기반 마케팅 및 성장 전략 수립",
                "Data-driven marketing and growth strategy development",
                "FontAwesomeIcons.bullseye",
                categoryMap.get(InterviewType.MARKETING),
                List.of("Growth Hacking", "Conversion Optimization", "User Acquisition", "Retention Strategies",
                        "Analytics", "A/B Testing", "Marketing Automation", "Funnel Optimization", "Customer Journey",
                        "Performance Marketing", "SaaS Marketing", "Viral Marketing"),
                skillMap,
                now));

        // ===== 경영지원 직무 =====
        positions.add(createJobPosition(
                "project_manager",
                JobRole.PROJECT_MANAGER,
                "프로젝트 관리자",
                "Project Manager",
                "프로젝트 기획, 일정 관리, 리소스 관리 및 이해관계자 소통",
                "Planning projects, managing schedules, resources, and communicating with stakeholders",
                "FontAwesomeIcons.listCheck",
                categoryMap.get(InterviewType.BUSINESS),
                List.of("Project Management", "Agile", "Scrum", "Leadership", "Risk Management",
                        "Stakeholder Management", "Project Planning", "Budgeting", "Resource Allocation", "Jira",
                        "MS Project", "Reporting"),
                skillMap,
                now));

        positions.add(createJobPosition(
                "hr_manager",
                JobRole.HR_MANAGER,
                "인사 담당자",
                "HR Manager",
                "채용, 인재 개발, 성과 관리 및 HR 정책 수립",
                "Recruiting, talent development, performance management, and establishing HR policies",
                "FontAwesomeIcons.userTie",
                categoryMap.get(InterviewType.BUSINESS),
                List.of("Recruiting", "Talent Acquisition", "Onboarding", "Performance Management",
                        "Compensation & Benefits", "Employee Relations", "HR Policies", "Training & Development",
                        "HR Analytics", "HRIS", "Labor Laws", "Conflict Resolution"),
                skillMap,
                now));

        positions.add(createJobPosition(
                "finance_manager",
                JobRole.FINANCE_MANAGER,
                "재무 담당자",
                "Finance Manager",
                "재무 계획, 예산 관리, 재무 분석 및 보고",
                "Financial planning, budget management, financial analysis, and reporting",
                "FontAwesomeIcons.chartPie",
                categoryMap.get(InterviewType.BUSINESS),
                List.of("Financial Analysis", "Budgeting", "Forecasting", "Financial Reporting", "Excel", "ERP Systems",
                        "Accounting", "Cash Flow Management", "Cost Analysis", "Investment Analysis",
                        "Financial Modeling", "Taxation"),
                skillMap,
                now));

        // ===== 디자인 직무 추가 =====
        // 모션 그래픽 디자이너
        positions.add(createJobPosition(
                "motion_designer",
                JobRole.GRAPHIC_DESIGNER,
                "모션 그래픽 디자이너",
                "Motion Graphic Designer",
                "움직이는 그래픽 요소 및 애니메이션 디자인 제작",
                "Creating animated graphic elements and visual effects for various media",
                "FontAwesomeIcons.film",
                categoryMap.get(InterviewType.DESIGN),
                List.of("After Effects", "Premiere Pro", "Motion Graphics", "Animation", "Storyboarding",
                        "Video Editing",
                        "Cinema 4D", "Illustrator", "Visual Effects", "Typography", "Composition", "Color Theory"),
                skillMap,
                now));

        // 브랜드 디자이너
        positions.add(createJobPosition(
                "brand_designer",
                JobRole.BRAND_DESIGNER,
                "브랜드 디자이너",
                "Brand Designer",
                "브랜드 아이덴티티 개발 및 일관된 브랜드 경험 디자인",
                "Developing brand identities and designing consistent brand experiences",
                "FontAwesomeIcons.certificate",
                categoryMap.get(InterviewType.DESIGN),
                List.of("Brand Identity", "Logo Design", "Brand Guidelines", "Visual Identity", "Typography",
                        "Color Theory", "Brand Strategy", "Packaging Design", "Marketing Collateral",
                        "Illustrator", "Photoshop", "Brand Storytelling"),
                skillMap,
                now));

        // 일러스트레이터
        positions.add(createJobPosition(
                "illustrator",
                JobRole.GRAPHIC_DESIGNER,
                "일러스트레이터",
                "Illustrator",
                "디지털 또는 전통적인 매체를 사용한 그림 및 삽화 제작",
                "Creating illustrations and drawings using digital or traditional media",
                "FontAwesomeIcons.pencilRuler",
                categoryMap.get(InterviewType.DESIGN),
                List.of("Illustrator", "Photoshop", "Procreate", "Digital Illustration", "Character Design",
                        "Concept Art", "Editorial Illustration", "Storyboarding", "Drawing", "Digital Painting",
                        "Vector Art", "Composition"),
                skillMap,
                now));

        // 3D 모델링 디자이너
        positions.add(createJobPosition(
                "3d_designer",
                JobRole.GRAPHIC_DESIGNER,
                "3D 모델링 디자이너",
                "3D Modeling Designer",
                "제품, 캐릭터, 환경 등의 3D 모델 디자인 및 제작",
                "Designing and creating 3D models of products, characters, environments, etc.",
                "FontAwesomeIcons.cube",
                categoryMap.get(InterviewType.DESIGN),
                List.of("Blender", "Maya", "3Ds Max", "Cinema 4D", "ZBrush", "Substance Painter", "3D Modeling",
                        "Texturing", "Rendering", "Animation", "Rigging", "UV Mapping"),
                skillMap,
                now));

        // UI 개발자
        positions.add(createJobPosition(
                "ui_developer",
                JobRole.UI_UX_DESIGNER,
                "UI 개발자",
                "UI Developer",
                "디자인 시스템을 코드로 구현하고 프론트엔드 개발자와 디자이너 간 협업 담당",
                "Implementing design systems in code and bridging the gap between frontend developers and designers",
                "FontAwesomeIcons.code",
                categoryMap.get(InterviewType.DESIGN),
                List.of("HTML5", "CSS3", "JavaScript", "Sass/SCSS", "Design Systems", "Component Libraries",
                        "Accessibility", "Responsive Design", "Figma", "React", "Storybook", "CSS-in-JS"),
                skillMap,
                now));

        // ===== 마케팅 직무 추가 =====
        // 브랜드 마케터
        positions.add(createJobPosition(
                "brand_marketer",
                JobRole.BRAND_MARKETER,
                "브랜드 마케터",
                "Brand Marketer",
                "브랜드 전략 수립 및 브랜드 인지도와 충성도 향상 활동",
                "Developing brand strategies and activities to enhance brand awareness and loyalty",
                "FontAwesomeIcons.crown",
                categoryMap.get(InterviewType.MARKETING),
                List.of("Brand Strategy", "Brand Marketing", "Brand Guidelines", "Brand Management",
                        "Brand Storytelling",
                        "Market Research", "Competitor Analysis", "Content Marketing", "Analytics",
                        "Digital Marketing", "Creative Strategy", "Social Media Marketing"),
                skillMap,
                now));

        // 소셜 미디어 마케터
        positions.add(createJobPosition(
                "social_media_marketer",
                JobRole.DIGITAL_MARKETER,
                "소셜 미디어 마케터",
                "Social Media Marketer",
                "소셜 미디어 플랫폼 전략 수립 및 콘텐츠 제작, 커뮤니티 관리",
                "Developing strategies for social media platforms, creating content, and managing communities",
                "FontAwesomeIcons.hashtag",
                categoryMap.get(InterviewType.MARKETING),
                List.of("Social Media Marketing", "Content Marketing", "Community Management", "Analytics",
                        "Facebook Ads", "Instagram Ads", "Twitter Ads", "LinkedIn Ads", "TikTok Ads",
                        "Digital Advertising", "Influencer Marketing", "Content Strategy"),
                skillMap,
                now));

        // PR/홍보 담당자
        positions.add(createJobPosition(
                "pr_specialist",
                JobRole.DIGITAL_MARKETER,
                "PR/홍보 담당자",
                "PR Specialist",
                "기업 이미지 관리 및 언론 관계 구축, 보도자료 작성",
                "Managing company image, building media relations, and writing press releases",
                "FontAwesomeIcons.newspaper",
                categoryMap.get(InterviewType.MARKETING),
                List.of("Content Marketing", "Brand Marketing", "Content Strategy", "Brand Storytelling",
                        "Digital Marketing", "Analytics", "SEM", "Social Media Marketing",
                        "Brand Strategy", "SEO", "PR Strategy", "Media Management"),
                skillMap,
                now));

        // 마케팅 분석가
        positions.add(createJobPosition(
                "marketing_analyst",
                JobRole.DIGITAL_MARKETER,
                "마케팅 분석가",
                "Marketing Analyst",
                "마케팅 캠페인 성과 분석 및 데이터 기반 인사이트 도출",
                "Analyzing marketing campaign performance and deriving data-driven insights",
                "FontAwesomeIcons.chartBar",
                categoryMap.get(InterviewType.MARKETING),
                List.of("Data Analysis", "Google Analytics", "GA4", "A/B Testing", "Conversion Rate Optimization",
                        "Statistical Analysis", "SQL", "Data Visualization", "Analytics",
                        "Digital Marketing", "SEO", "Marketing ROI"),
                skillMap,
                now));

        // 제품 마케터
        positions.add(createJobPosition(
                "product_marketer",
                JobRole.DIGITAL_MARKETER,
                "제품 마케터",
                "Product Marketer",
                "제품의 시장 진입 전략 수립 및 포지셔닝, 고객 니즈 분석",
                "Developing go-to-market strategies, positioning products, and analyzing customer needs",
                "FontAwesomeIcons.boxOpen",
                categoryMap.get(InterviewType.MARKETING),
                List.of("Product Marketing", "Go-to-Market Strategy", "Competitive Analysis", "Market Research",
                        "Content Marketing", "User Research", "Messaging", "Sales Enablement",
                        "Digital Marketing", "Content Strategy", "Analytics", "Product Management"),
                skillMap,
                now));

        // ===== 경영지원 직무 추가 =====
        // 사업 개발 담당자
        positions.add(createJobPosition(
                "business_developer",
                JobRole.BUSINESS_DEVELOPMENT,
                "사업 개발 담당자",
                "Business Developer",
                "새로운 비즈니스 기회 발굴 및 파트너십 구축, 시장 확장 전략 수립",
                "Identifying new business opportunities, building partnerships, and developing market expansion strategies",
                "FontAwesomeIcons.handshake",
                categoryMap.get(InterviewType.BUSINESS),
                List.of("Business Strategy", "Market Research", "Negotiation", "Contract Negotiation",
                        "Business Model Development", "Strategic Planning", "Client Management", "Presentation Skills",
                        "Partnership Management", "Leadership", "Problem Solving", "Financial Analysis"),
                skillMap,
                now));

        // 전략 기획자
        positions.add(createJobPosition(
                "strategic_planner",
                JobRole.BUSINESS_DEVELOPMENT,
                "전략 기획자",
                "Strategic Planner",
                "기업의 중장기 성장 전략 수립 및 신규 사업 기획",
                "Developing mid to long-term growth strategies and planning new business initiatives",
                "FontAwesomeIcons.chessKnight",
                categoryMap.get(InterviewType.BUSINESS),
                List.of("Strategic Planning", "Business Strategy", "Market Research", "SWOT Analysis",
                        "Financial Analysis", "Business Model Development", "Data-driven Decision Making",
                        "Project Management", "Risk Management", "Competitive Analysis", "Market Research",
                        "Leadership"),
                skillMap,
                now));

        // 운영 관리자
        positions.add(createJobPosition(
                "operations_manager",
                JobRole.BUSINESS_DEVELOPMENT,
                "운영 관리자",
                "Operations Manager",
                "조직의 일상 업무 프로세스 관리 및 효율성 최적화",
                "Managing day-to-day operational processes and optimizing efficiency in an organization",
                "FontAwesomeIcons.tasks",
                categoryMap.get(InterviewType.BUSINESS),
                List.of("Operations Management", "Process Optimization", "Lean Operations", "Quality Management",
                        "Supply Chain Management", "Vendor Management", "Project Management", "Risk Management",
                        "Budget Management", "Team Management", "Problem Solving", "ERP Systems"),
                skillMap,
                now));

        // 고객 성공 관리자
        positions.add(createJobPosition(
                "customer_success_manager",
                JobRole.BUSINESS_DEVELOPMENT,
                "고객 성공 관리자",
                "Customer Success Manager",
                "고객 관계 관리 및 고객 만족도 향상, 이탈 방지 활동",
                "Managing customer relationships, improving satisfaction, and preventing churn",
                "FontAwesomeIcons.userCheck",
                categoryMap.get(InterviewType.BUSINESS),
                List.of("CRM", "Client Management", "Onboarding", "Problem Solving", "Project Management",
                        "Team Management", "Leadership", "Performance Management", "Product Management",
                        "Communication", "Customer Feedback", "Data Analysis"),
                skillMap,
                now));

        // 법무 담당자
        positions.add(createJobPosition(
                "legal_counsel",
                JobRole.BUSINESS_DEVELOPMENT,
                "법무 담당자",
                "Legal Counsel",
                "계약 검토 및 법률 자문, 기업 법률 리스크 관리",
                "Reviewing contracts, providing legal advice, and managing legal risks for the company",
                "FontAwesomeIcons.balanceScale",
                categoryMap.get(InterviewType.BUSINESS),
                List.of("Contract Negotiation", "Negotiation", "Legal Documentation", "Corporate Law",
                        "Risk Management", "Compliance", "Problem Solving", "Research", "Project Management",
                        "Communication", "Leadership", "Ethics"),
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
}