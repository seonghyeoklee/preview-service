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
        List<InterviewCategory> categories = List.of(
                InterviewCategory.builder().icon("Icons.computer").title("IT 개발").description("소프트웨어/하드웨어 개발 직군")
                        .type(InterviewType.TECHNICAL).createdAt(now).updatedAt(now).build(),
                InterviewCategory.builder().icon("Icons.design_services").title("디자인")
                        .description("UI/UX, 그래픽, 제품 디자인 직군").type(InterviewType.DESIGN).createdAt(now).updatedAt(now)
                        .build(),
                InterviewCategory.builder().icon("Icons.campaign").title("마케팅").description("디지털 마케팅, 콘텐츠 기획 직군")
                        .type(InterviewType.MARKETING).createdAt(now).updatedAt(now).build(),
                InterviewCategory.builder().icon("Icons.business").title("경영지원").description("인사, 재무, 행정 지원 직군")
                        .type(InterviewType.BUSINESS).createdAt(now).updatedAt(now).build());

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

        // 백엔드 개발자
        JobPosition backendDev = JobPosition.builder()
                .positionId("backend_developer")
                .role(JobRole.BACKEND_DEVELOPER)
                .title("백엔드 개발자")
                .titleEn("Backend Developer")
                .description("서버, API, 데이터베이스 설계 및 관리 등 서비스의 핵심 기능을 담당")
                .descriptionEn(
                        "Responsible for server-side logic, API development, database design, and managing the core functions of services")
                .icon("FontAwesomeIcons.server")
                .createdAt(now)
                .updatedAt(now)
                .build();

        // IT 개발 카테고리에 연결
        backendDev.setCategory(categoryMap.get(InterviewType.TECHNICAL));

        // 스킬 연결
        List<String> backendSkillNames = List.of("Java", "Python", "Spring", "Spring Boot", "Django", "SQL", "MySQL",
                "PostgreSQL", "MongoDB", "REST API", "GraphQL", "Docker", "Kubernetes", "AWS");
        backendSkillNames.forEach(skillName -> {
            Skill skill = skillMap.get(skillName);
            if (skill != null) {
                backendDev.getSkills().add(skill);
            }
        });
        positions.add(backendDev);

        // 프론트엔드 개발자
        JobPosition frontendDev = JobPosition.builder()
                .positionId("frontend_developer")
                .role(JobRole.FRONTEND_DEVELOPER)
                .title("프론트엔드 개발자")
                .titleEn("Frontend Developer")
                .description("웹 사이트, 애플리케이션의 사용자 인터페이스 및 사용자 경험 개발")
                .descriptionEn("Developing user interfaces and user experiences for websites and applications")
                .icon("FontAwesomeIcons.display")
                .createdAt(now)
                .updatedAt(now)
                .build();

        // IT 개발 카테고리에 연결
        frontendDev.setCategory(categoryMap.get(InterviewType.TECHNICAL));

        // 스킬 연결
        List<String> frontendSkillNames = List.of("JavaScript", "TypeScript", "React", "Vue.js", "Angular", "HTML5",
                "CSS3", "Tailwind CSS", "Next.js");
        frontendSkillNames.forEach(skillName -> {
            Skill skill = skillMap.get(skillName);
            if (skill != null) {
                frontendDev.getSkills().add(skill);
            }
        });
        positions.add(frontendDev);

        // 디자이너
        JobPosition designer = JobPosition.builder()
                .positionId("ui_ux_designer")
                .role(JobRole.UI_UX_DESIGNER)
                .title("UI/UX 디자이너")
                .titleEn("UI/UX Designer")
                .description("사용자 인터페이스/경험 디자인, 와이어프레임, 프로토타입 제작")
                .descriptionEn("Designing user interfaces/experiences, creating wireframes and prototypes")
                .icon("FontAwesomeIcons.penRuler")
                .createdAt(now)
                .updatedAt(now)
                .build();

        // 디자인 카테고리에 연결
        designer.setCategory(categoryMap.get(InterviewType.DESIGN));

        // 스킬 연결
        List<String> designSkillNames = List.of("Figma", "Sketch", "Adobe XD", "Photoshop", "Illustrator", "UI Design",
                "UX Design", "Wireframing", "Prototyping");
        designSkillNames.forEach(skillName -> {
            Skill skill = skillMap.get(skillName);
            if (skill != null) {
                designer.getSkills().add(skill);
            }
        });
        positions.add(designer);

        // 마케터
        JobPosition marketer = JobPosition.builder()
                .positionId("digital_marketer")
                .role(JobRole.DIGITAL_MARKETER)
                .title("디지털 마케터")
                .titleEn("Digital Marketer")
                .description("온라인 마케팅 전략 수립, 캠페인 기획 및 성과 분석")
                .descriptionEn("Developing online marketing strategies, planning campaigns, and analyzing performance")
                .icon("FontAwesomeIcons.chartLine")
                .createdAt(now)
                .updatedAt(now)
                .build();

        // 마케팅 카테고리에 연결
        marketer.setCategory(categoryMap.get(InterviewType.MARKETING));

        // 스킬 연결
        List<String> marketingSkillNames = List.of("SEO", "Social Media Marketing", "Content Marketing",
                "Google Analytics", "Growth Hacking", "Email Marketing", "Digital Advertising");
        marketingSkillNames.forEach(skillName -> {
            Skill skill = skillMap.get(skillName);
            if (skill != null) {
                marketer.getSkills().add(skill);
            }
        });
        positions.add(marketer);

        // 프로젝트 매니저
        JobPosition projectManager = JobPosition.builder()
                .positionId("project_manager")
                .role(JobRole.PROJECT_MANAGER)
                .title("프로젝트 관리자")
                .titleEn("Project Manager")
                .description("프로젝트 기획, 일정 관리, 리소스 관리 및 이해관계자 소통")
                .descriptionEn("Planning projects, managing schedules, resources, and communicating with stakeholders")
                .icon("FontAwesomeIcons.listCheck")
                .createdAt(now)
                .updatedAt(now)
                .build();

        // 경영지원 카테고리에 연결
        projectManager.setCategory(categoryMap.get(InterviewType.BUSINESS));

        // 스킬 연결
        List<String> pmSkillNames = List.of("Project Management", "Agile", "Scrum", "Leadership", "Jira", "Notion");
        pmSkillNames.forEach(skillName -> {
            Skill skill = skillMap.get(skillName);
            if (skill != null) {
                projectManager.getSkills().add(skill);
            }
        });
        positions.add(projectManager);

        jobPositionRepository.saveAll(positions);
        log.info("{}개의 직무 포지션 데이터가 초기화되었습니다.", positions.size());
    }
}