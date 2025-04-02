package com.evawova.preview.config;

import com.evawova.preview.domain.interview.entity.InterviewCategory;
import com.evawova.preview.domain.interview.entity.InterviewPrompt;
import com.evawova.preview.domain.interview.entity.JobPosition;
import com.evawova.preview.domain.interview.model.*;
import com.evawova.preview.domain.interview.repository.InterviewCategoryRepository;
import com.evawova.preview.domain.interview.repository.InterviewPromptRepository;
import com.evawova.preview.domain.interview.repository.JobPositionRepository;
import com.evawova.preview.domain.user.service.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InitService {

    private final UserService userService;
    private final InterviewPromptRepository interviewPromptRepository;
    private final InterviewCategoryRepository interviewCategoryRepository;
    private final JobPositionRepository jobPositionRepository;

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

        // 직무 정보
        for (JobRole role : JobRole.values()) {
            String content = "{{직무}} 포지션에 대한 면접을 진행합니다. ";

            // 직무별 내용 추가 - switch문 사용
            switch (role) {
                case FRONTEND_DEVELOPER:
                    content += "JavaScript, React, Vue.js 등의 프론트엔드 기술과 UI/UX에 대한 이해도를 평가해주세요. "
                            + "특히 컴포넌트 설계, 상태 관리, 최적화 기법, 반응형 디자인, 웹 접근성, 브라우저 호환성 등의 지식을 확인하고, "
                            + "HTML/CSS 기술력과 함께 최신 웹 표준 및 ECMAScript 기능에 대한 이해도를 점검해주세요. "
                            + "프로젝트 경험과 문제 해결 능력, 코드 품질에 대한 관점도 중요하게 평가해주세요.";
                    break;
                case BACKEND_DEVELOPER:
                    content += "서버 아키텍처, 데이터베이스 설계, API 개발 경험에 중점을 두고 평가해주세요. "
                            + "RESTful API, GraphQL 설계 원칙, 마이크로서비스 아키텍처, 서버 확장성, 성능 최적화 경험을 확인하고, "
                            + "SQL/NoSQL 데이터베이스 지식, ORM 활용, 트랜잭션 관리, 쿼리 최적화에 대한 이해도를 점검해주세요. "
                            + "또한 보안 모범 사례, 인증/인가 메커니즘, 로깅/모니터링 구현 경험, 비동기 처리, 캐싱 전략도 함께 평가해주세요.";
                    break;
                case FULLSTACK_DEVELOPER:
                    content += "프론트엔드와 백엔드 양쪽의 기술적 역량과 전체 시스템 아키텍처에 대한 이해도를 평가해주세요. "
                            + "프론트엔드 프레임워크(React, Vue, Angular 등)와 백엔드 기술(Node.js, Spring, Django 등)의 균형 있는 지식을 확인하고, "
                            + "풀스택 개발 환경 구축 경험, API 설계와 통합 경험, 데이터베이스 모델링 능력을 점검해주세요. "
                            + "클라이언트-서버 아키텍처의 전체 흐름 이해도, 성능 최적화 관점, 보안 지식, DevOps 경험도 함께 평가해주세요.";
                    break;
                case MOBILE_DEVELOPER:
                    content += "모바일 앱 개발 기술과 사용자 경험, 성능 최적화 능력을 평가해주세요. "
                            + "네이티브 개발(iOS/Android) 또는 크로스 플랫폼 프레임워크(React Native, Flutter) 활용 경험을 확인하고, "
                            + "앱 아키텍처 패턴(MVC, MVVM 등), 상태 관리, 오프라인 지원, 애니메이션 구현 능력을 점검해주세요. "
                            + "앱 배포 프로세스, 메모리 관리, 배터리 효율성, 푸시 알림 통합, 모바일 보안 지식, 다양한 디바이스 대응 경험도 함께 평가해주세요.";
                    break;
                case DEVOPS_DEVELOPER:
                    content += "CI/CD 파이프라인, 클라우드 인프라, 시스템 모니터링 경험에 중점을 두고 평가해주세요. "
                            + "Docker, Kubernetes 등 컨테이너 기술과 AWS, Azure, GCP 등 클라우드 서비스 활용 경험을 확인하고, "
                            + "인프라를 코드로 관리하는 능력(IaC), Jenkins, GitLab CI 등 CI/CD 도구 구성 경험을 점검해주세요. "
                            + "자동화 스크립팅 능력, 로깅/모니터링 시스템 구축, 트러블슈팅, 성능 튜닝, 장애 대응 프로세스, 보안 인프라 지식도 함께 평가해주세요.";
                    break;
                case DATA_SCIENTIST:
                    content += "데이터 분석 기술, 머신러닝 모델 개발, 통계적 사고 능력을 평가해주세요. "
                            + "Python, R 등 데이터 분석 도구 활용과 SQL, Pandas, NumPy 등 데이터 처리 기술 경험을 확인하고, "
                            + "통계 분석 방법론, 가설 검정, A/B 테스트 설계 능력을 점검해주세요. "
                            + "데이터 시각화 기술, 비지니스 문제를 데이터 문제로 전환하는 능력, 머신러닝 알고리즘 이해도, 모델 평가 방법, 빅데이터 처리 경험도 함께 평가해주세요.";
                    break;
                case AI_ENGINEER:
                    content += "AI/ML 모델 개발, 딥러닝 프레임워크 활용 경험, 알고리즘 최적화 능력을 평가해주세요. "
                            + "TensorFlow, PyTorch 등 딥러닝 프레임워크 활용과 컴퓨터 비전, NLP, 강화학습 등 AI 하위 분야 전문성을 확인하고, "
                            + "모델 아키텍처 설계, 하이퍼파라미터 튜닝, 분산 학습 구현 경험을 점검해주세요. "
                            + "실제 프로덕션 환경에서의 AI 모델 배포 경험, 모델 최적화 기법, 데이터 파이프라인 구축, 윤리적 AI 개발 관점도 함께 평가해주세요.";
                    break;
                case SECURITY_ENGINEER:
                    content += "보안 취약점 분석, 보안 시스템 설계, 침해 대응 경험에 중점을 두고 평가해주세요. "
                            + "취약점 스캐닝, 침투 테스트, 코드 보안 감사 경험과 네트워크 보안, 웹 애플리케이션 보안 지식을 확인하고, "
                            + "보안 아키텍처 설계, 인증/인가 메커니즘 구현, 암호화 프로토콜 활용 능력을 점검해주세요. "
                            + "보안 사고 대응 계획 수립, 포렌식 분석, 위협 인텔리전스 활용, 규정 준수(GDPR, PCI DSS 등), 보안 자동화 툴 개발 경험도 함께 평가해주세요.";
                    break;
                case QA_ENGINEER:
                    content += "테스트 계획 수립, 자동화 테스트 구현, 품질 보증 프로세스에 대한 경험을 평가해주세요. "
                            + "테스트 방법론(BDD, TDD 등), 테스트 케이스 설계, 테스트 우선순위 결정 능력을 확인하고, "
                            + "Selenium, Cypress, JUnit 등 테스트 자동화 도구 활용 경험, CI/CD 환경에서의 테스트 통합 경험을 점검해주세요. "
                            + "성능/부하 테스트, 보안 테스트, API 테스트, 모바일 테스트 경험과 버그 리포팅 프로세스, 결함 추적, 품질 메트릭 관리 능력도 함께 평가해주세요.";
                    break;
                case UI_UX_DESIGNER:
                    content += "디자인 철학, 포트폴리오, 사용자 경험에 중점을 두고 평가해주세요. "
                            + "사용자 중심 디자인 방법론, 와이어프레임/프로토타입 제작 경험, 인터랙션 디자인 능력을 확인하고, "
                            + "Figma, Sketch, Adobe XD 등 디자인 툴 활용 스킬과 디자인 시스템 구축 경험을 점검해주세요. "
                            + "정보 구조 설계, 사용성 테스트 진행 경험, 접근성 고려 사항, 반응형/적응형 디자인 지식, 시각적 계층 구조 이해도도 함께 평가해주세요.";
                    break;
                case GRAPHIC_DESIGNER:
                    content += "디자인 철학, 포트폴리오, 사용자 경험에 중점을 두고 평가해주세요. "
                            + "시각적 디자인 원칙, 타이포그래피, 색채 이론, 레이아웃 설계 능력을 확인하고, "
                            + "Adobe Creative Suite(Photoshop, Illustrator, InDesign) 등 도구 활용 스킬과 브랜드 가이드라인 적용 경험을 점검해주세요. "
                            + "디자인 트렌드 인식, 다양한 매체(인쇄물, 디지털, 소셜 미디어)에 맞는 디자인 경험, 스토리텔링 능력, 이미지 편집 기술도 함께 평가해주세요.";
                    break;
                case PRODUCT_DESIGNER:
                    content += "디자인 철학, 포트폴리오, 사용자 경험에 중점을 두고 평가해주세요. "
                            + "제품 디자인 프로세스, 사용자 리서치 방법론, 아이디어 스케치에서 최종 디자인까지의 워크플로우를 확인하고, "
                            + "3D 모델링 소프트웨어 활용 능력, 프로토타이핑 경험, 제조 공정에 대한 이해도를 점검해주세요. "
                            + "사용자 중심 접근법, 재료 지식, 지속가능성 고려 사항, 인체공학적 디자인 원칙, 제품 혁신 사례도 함께 평가해주세요.";
                    break;
                case BRAND_DESIGNER:
                    content += "디자인 철학, 포트폴리오, 사용자 경험에 중점을 두고 평가해주세요. "
                            + "브랜드 아이덴티티 개발, 로고 디자인, 브랜드 가이드라인 수립 경험을 확인하고, "
                            + "시각적 스토리텔링 능력, 브랜드 자산 관리, 다양한 채널에서의 일관된 브랜드 표현 능력을 점검해주세요. "
                            + "시장 트렌드 분석, 경쟁사 브랜드 분석, 타겟 오디언스 이해도, 브랜드 포지셔닝 전략, 브랜드 메시지 개발 경험도 함께 평가해주세요.";
                    break;
                case DIGITAL_MARKETER:
                    content += "마케팅 전략, 캠페인 경험, 성과 측정 능력에 중점을 두고 평가해주세요. "
                            + "SEO/SEM, 소셜 미디어 마케팅, 이메일 마케팅, 콘텐츠 마케팅 전략 수립 경험을 확인하고, "
                            + "Google Analytics, Google Ads, Facebook Business Manager 등 디지털 마케팅 도구 활용 능력을 점검해주세요. "
                            + "데이터 기반 의사결정, A/B 테스트 설계, 전환율 최적화, 디지털 광고 예산 관리, 퍼포먼스 마케팅 KPI 설정 및 분석 경험도 함께 평가해주세요.";
                    break;
                case CONTENT_MARKETER:
                    content += "마케팅 전략, 캠페인 경험, 성과 측정 능력에 중점을 두고 평가해주세요. "
                            + "콘텐츠 전략 수립, 콘텐츠 캘린더 관리, 다양한 포맷(블로그, 비디오, 팟캐스트, 인포그래픽) 콘텐츠 제작 경험을 확인하고, "
                            + "타겟 오디언스에 맞는 스토리텔링 능력, SEO 최적화 글쓰기, 콘텐츠 배포 전략을 점검해주세요. "
                            + "콘텐츠 성과 측정, 편집 일정 관리, 브랜드 톤앤보이스 유지, 트렌드 리서치, 인플루언서 협업 경험도 함께 평가해주세요.";
                    break;
                case BRAND_MARKETER:
                    content += "마케팅 전략, 캠페인 경험, 성과 측정 능력에 중점을 두고 평가해주세요. "
                            + "브랜드 전략 수립, 브랜드 포지셔닝, 브랜드 메시징 프레임워크 개발 경험을 확인하고, "
                            + "브랜드 인지도 캠페인 기획, 브랜드 가치 전달, 고객 충성도 프로그램 개발 능력을 점검해주세요. "
                            + "시장 및 경쟁사 분석, 브랜드 건강도 측정, 통합 마케팅 커뮤니케이션 계획, 브랜드 파트너십 관리, 위기 관리 전략도 함께 평가해주세요.";
                    break;
                case GROWTH_HACKER:
                    content += "마케팅 전략, 캠페인 경험, 성과 측정 능력에 중점을 두고 평가해주세요. "
                            + "사용자 획득 전략, 활성화 모델, 리텐션 최적화, 바이럴 루프 설계 경험을 확인하고, "
                            + "실험 문화 구축, A/B 테스트 설계 및 분석, 사용자 행동 데이터 분석 능력을 점검해주세요. "
                            + "고객 생애 가치 모델링, 퍼널 최적화, 그로스 해킹 기법, 제품-마케팅 연계 전략, 확장 가능한 채널 발굴 경험도 함께 평가해주세요.";
                    break;
                case HR_MANAGER:
                    content += "인재 채용, 조직 문화 관리, 임직원 케어 경험에 중점을 두고 평가해주세요. "
                            + "인재 채용 및 온보딩 프로세스 설계, 직원 평가 및 성과 관리 시스템 운영 경험을 확인하고, "
                            + "조직 문화 구축 및 유지, 임직원 교육 프로그램 개발, 리더십 개발 계획 수립 능력을 점검해주세요. "
                            + "인사 관련 법규 이해도, 보상 및 복리후생 체계 설계, 직원 관계 관리, 다양성 및 포용성 이니셔티브, 인재 유지 전략도 함께 평가해주세요.";
                    break;
                case FINANCE_MANAGER:
                    content += "재무 분석, 예산 관리, 리스크 관리 능력에 중점을 두고 평가해주세요. "
                            + "재무제표 분석, 현금흐름 관리, 재무 모델링 및 예측 경험을 확인하고, "
                            + "예산 계획 수립 및 실행, 비용 통제, 자본 배분 의사결정 능력을 점검해주세요. "
                            + "재무 리스크 평가, 투자 분석, 세무 계획 및 준수, 재무 보고 체계 개선, 핵심 성과 지표(KPI) 설정 및 모니터링 경험도 함께 평가해주세요.";
                    break;
                case BUSINESS_DEVELOPMENT:
                    content += "사업 전략, 파트너십 구축, 시장 분석 능력에 중점을 두고 평가해주세요. "
                            + "신규 사업 기회 발굴, 시장 조사 및 경쟁 분석, 사업 타당성 평가 경험을 확인하고, "
                            + "전략적 파트너십 구축 및 관리, 계약 협상, 제휴 관계 유지 능력을 점검해주세요. "
                            + "수익 모델 개발, 사업 확장 전략 수립, 성장 기회 우선순위화, 클라이언트 관계 구축, 판매 전략 이해도도 함께 평가해주세요.";
                    break;
                case PROJECT_MANAGER:
                    content += "프로젝트 계획, 자원 관리, 이해관계자 커뮤니케이션 경험을 평가해주세요. "
                            + "프로젝트 범위 정의, 일정 계획, WBS(작업 분할 구조) 작성 경험을 확인하고, "
                            + "팀 리소스 할당 및 관리, 리스크 식별 및 완화 전략, 프로젝트 예산 통제 능력을 점검해주세요. "
                            + "애자일/워터폴 등 프로젝트 방법론 적용, 이해관계자 관리, 프로젝트 보고 및 대시보드 활용, 문제 해결 및 결정, 타임라인 준수 경험도 함께 평가해주세요.";
                    break;
                default:
                    content += "관련 업무 경험과 전문성에 중점을 두고 평가해주세요. "
                            + "해당 분야의 핵심 기술 및 도구 활용 능력, 프로젝트 수행 경험, 문제 해결 방법론을 확인하고, "
                            + "업계 트렌드 이해도, 전문 지식 습득 및 적용 능력, 관련 자격증이나 교육 이수 내역을 점검해주세요. "
                            + "팀워크 및 협업 능력, 의사소통 스킬, 업무 우선순위 설정, 자기주도적 학습 태도, 직무 관련 윤리적 판단력도 함께 평가해주세요.";
                    break;
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

    private void initializeJobPositions() {
        if (jobPositionRepository.count() > 0) {
            log.info("직무 포지션 데이터가 이미 존재합니다. 초기화를 건너뜁니다.");
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        List<JobPosition> positions = List.of(
                JobPosition.builder().positionId("backend_developer").role(JobRole.BACKEND_DEVELOPER).title("백엔드 개발자")
                        .description("서버, API, 데이터베이스 설계 및 관리 등 서비스의 핵심 기능을 담당").icon("FontAwesomeIcons.server")
                        .skills(List.of("Java", "Python", "Node.js", "Spring", "Django", "Express", "MySQL", "MongoDB"))
                        .createdAt(now).updatedAt(now).build(),
                JobPosition.builder().positionId("frontend_developer").role(JobRole.FRONTEND_DEVELOPER)
                        .title("프론트엔드 개발자").description("웹 사이트, 애플리케이션의 사용자 인터페이스 및 사용자 경험 개발")
                        .icon("FontAwesomeIcons.display")
                        .skills(List.of("HTML", "CSS", "JavaScript", "React", "Vue", "Angular", "TypeScript"))
                        .createdAt(now).updatedAt(now).build(),
                JobPosition.builder().positionId("fullstack_developer").role(JobRole.FULLSTACK_DEVELOPER)
                        .title("풀스택 개발자").description("프론트엔드와 백엔드 개발을 모두 수행하는 종합적인 개발 역할")
                        .icon("FontAwesomeIcons.layerGroup")
                        .skills(List.of("JavaScript", "TypeScript", "React", "Node.js", "Database", "REST API"))
                        .createdAt(now).updatedAt(now).build(),
                JobPosition.builder().positionId("mobile_developer").role(JobRole.MOBILE_DEVELOPER).title("모바일 개발자")
                        .description("iOS, Android 플랫폼 앱 개발 및 배포, 유지보수를 담당").icon("FontAwesomeIcons.mobileScreen")
                        .skills(List.of("Android", "iOS", "Swift", "Kotlin", "React Native", "Flutter")).createdAt(now)
                        .updatedAt(now).build(),
                JobPosition.builder().positionId("devops_developer").role(JobRole.DEVOPS_DEVELOPER).title("DevOps 엔지니어")
                        .description("개발 및 운영 프로세스 자동화, 인프라 관리, CI/CD 파이프라인 구축").icon("FontAwesomeIcons.gears")
                        .skills(List.of("Docker", "Kubernetes", "AWS", "Jenkins", "Git", "CI/CD")).createdAt(now)
                        .updatedAt(now).build(),
                JobPosition.builder().positionId("data_scientist").role(JobRole.DATA_SCIENTIST).title("데이터 엔지니어")
                        .description("데이터 파이프라인 구축, 데이터 인프라 관리 및 최적화").icon("FontAwesomeIcons.database")
                        .skills(List.of("SQL", "Hadoop", "Spark", "ETL", "Data Warehousing", "Python")).createdAt(now)
                        .updatedAt(now).build(),
                JobPosition.builder().positionId("ai_engineer").role(JobRole.AI_ENGINEER).title("AI/ML 엔지니어")
                        .description("머신러닝 모델 개발, 학습, 배포 및 성능 최적화").icon("FontAwesomeIcons.robot")
                        .skills(List.of("Python", "TensorFlow", "PyTorch", "Scikit-learn", "Deep Learning"))
                        .createdAt(now).updatedAt(now).build(),
                JobPosition.builder().positionId("security_engineer").role(JobRole.SECURITY_ENGINEER).title("보안 엔지니어")
                        .description("시스템, 네트워크, 애플리케이션의 보안 취약점 분석 및 대응").icon("FontAwesomeIcons.shieldHalved")
                        .skills(List.of("Network Security", "Penetration Testing", "Security Protocols")).createdAt(now)
                        .updatedAt(now).build(),
                JobPosition.builder().positionId("qa_engineer").role(JobRole.QA_ENGINEER).title("QA 엔지니어")
                        .description("소프트웨어 품질 보증, 테스트 자동화, 버그 추적 및 보고").icon("FontAwesomeIcons.clipboardCheck")
                        .skills(List.of("Manual Testing", "Automated Testing", "Test Cases", "Bug Tracking"))
                        .createdAt(now).updatedAt(now).build(),
                JobPosition.builder().positionId("ui_ux_designer").role(JobRole.UI_UX_DESIGNER).title("UI/UX 디자이너")
                        .description("사용자 인터페이스/경험 디자인, 와이어프레임, 프로토타입 제작").icon("FontAwesomeIcons.penRuler")
                        .skills(List.of("Figma", "Sketch", "Adobe XD", "User Research", "Prototyping")).createdAt(now)
                        .updatedAt(now).build(),
                JobPosition.builder().positionId("graphic_designer").role(JobRole.GRAPHIC_DESIGNER).title("그래픽 디자이너")
                        .description("브랜드 아이덴티티, 마케팅 자료, 웹/앱 그래픽 디자인").icon("FontAwesomeIcons.palette")
                        .skills(List.of("Photoshop", "Illustrator", "Typography", "Layout Design")).createdAt(now)
                        .updatedAt(now).build(),
                JobPosition.builder().positionId("product_designer").role(JobRole.PRODUCT_DESIGNER).title("제품 디자이너")
                        .description("제품 기획, 디자인, 프로토타입 제작 및 사용자 테스트").icon("FontAwesomeIcons.objectGroup")
                        .skills(List.of("Product Thinking", "Design Systems", "User Testing", "Prototyping"))
                        .createdAt(now).updatedAt(now).build(),
                JobPosition.builder().positionId("digital_marketer").role(JobRole.DIGITAL_MARKETER).title("디지털 마케터")
                        .description("온라인 마케팅 전략 수립, 캠페인 기획 및 성과 분석").icon("FontAwesomeIcons.chartLine")
                        .skills(List.of("SEO", "SEM", "Social Media Marketing", "Analytics")).createdAt(now)
                        .updatedAt(now).build(),
                JobPosition.builder().positionId("growth_hacker").role(JobRole.GROWTH_HACKER).title("그로스 해커")
                        .description("제품/서비스 성장을 위한 데이터 기반 전략 수립 및 실행").icon("FontAwesomeIcons.rocket")
                        .skills(List.of("A/B Testing", "User Acquisition", "Retention Strategy", "Analytics"))
                        .createdAt(now).updatedAt(now).build(),
                JobPosition.builder().positionId("hr_manager").role(JobRole.HR_MANAGER).title("인사 담당자")
                        .description("채용, 교육, 성과 관리 등 인적 자원 관리 업무 수행").icon("FontAwesomeIcons.userGroup")
                        .skills(List.of("Recruiting", "HR Policies", "Employee Relations", "Training")).createdAt(now)
                        .updatedAt(now).build(),
                JobPosition.builder().positionId("project_manager").role(JobRole.PROJECT_MANAGER).title("프로젝트 관리자")
                        .description("프로젝트 기획, 일정 관리, 리소스 관리 및 이해관계자 소통").icon("FontAwesomeIcons.listCheck")
                        .skills(List.of("Project Planning", "Agile", "Scrum", "Risk Management",
                                "Stakeholder Communication"))
                        .createdAt(now).updatedAt(now).build());

        jobPositionRepository.saveAll(positions);
        log.info("{}개의 직무 포지션 데이터가 초기화되었습니다.", positions.size());
    }
}