package com.evawova.preview.bootstrap.initializer;

import com.evawova.preview.bootstrap.EntityInitializer;
import com.evawova.preview.domain.interview.entity.JobField;
import com.evawova.preview.domain.interview.entity.JobPosition;
import com.evawova.preview.domain.interview.repository.JobFieldRepository;
import com.evawova.preview.domain.interview.repository.JobPositionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * JobPosition 엔티티 초기화 클래스
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JobPositionInitializer implements EntityInitializer {

    private final JobPositionRepository jobPositionRepository;
    private final JobFieldRepository jobFieldRepository;

    @Override
    @Transactional
    public void initialize() {
        if (jobPositionRepository.count() > 0) {
            log.info("이미 JobPosition 데이터가 존재합니다. 초기화를 건너뜁니다.");
            return;
        }

        log.info("JobPosition 데이터 초기화를 시작합니다.");

        // 직군 데이터 로드
        List<JobField> fields = jobFieldRepository.findAll();
        if (fields.isEmpty()) {
            log.error("직군 데이터가 없습니다. JobPosition 초기화를 건너뜁니다.");
            return;
        }

        // 직군 맵 생성 (code -> JobField)
        Map<String, JobField> fieldMap = fields.stream()
                .collect(Collectors.toMap(JobField::getCode, Function.identity()));

        LocalDateTime now = LocalDateTime.now();
        List<JobPosition> positions = new ArrayList<>();

        // 개발 직군 직무 추가
        JobField devField = fieldMap.get(JobField.DEVELOPMENT);
        if (devField != null) {
            positions.add(createJobPosition(
                    JobPosition.BACKEND_DEVELOPER,
                    "백엔드 개발자",
                    "Backend Developer",
                    "서버, API, 데이터베이스 설계 및 개발",
                    "Server, API, database design and development",
                    "Icons.code",
                    devField,
                    1,
                    now));

            positions.add(createJobPosition(
                    JobPosition.FRONTEND_DEVELOPER,
                    "프론트엔드 개발자",
                    "Frontend Developer",
                    "웹/앱 인터페이스 및 사용자 경험 구현",
                    "Web/app interface and user experience implementation",
                    "Icons.web",
                    devField,
                    2,
                    now));

            positions.add(createJobPosition(
                    JobPosition.FULLSTACK_DEVELOPER,
                    "풀스택 개발자",
                    "Fullstack Developer",
                    "프론트엔드와 백엔드 모두 개발",
                    "Develops both frontend and backend",
                    "Icons.all_inclusive",
                    devField,
                    3,
                    now));

            positions.add(createJobPosition(
                    JobPosition.MOBILE_DEVELOPER,
                    "모바일 개발자",
                    "Mobile Developer",
                    "iOS, Android, 크로스 플랫폼 앱 개발",
                    "iOS, Android, cross-platform app development",
                    "Icons.phone_android",
                    devField,
                    4,
                    now));
        }

        // 디자인 직군 직무 추가
        JobField designField = fieldMap.get(JobField.DESIGN);
        if (designField != null) {
            positions.add(createJobPosition(
                    JobPosition.UI_UX_DESIGNER,
                    "UI/UX 디자이너",
                    "UI/UX Designer",
                    "사용자 인터페이스 및 경험 디자인",
                    "User interface and experience design",
                    "Icons.dashboard_customize",
                    designField,
                    1,
                    now));

            positions.add(createJobPosition(
                    JobPosition.PRODUCT_DESIGNER,
                    "제품 디자이너",
                    "Product Designer",
                    "제품 기획부터 디자인까지 전과정 담당",
                    "Responsible for the entire process from product planning to design",
                    "Icons.category",
                    designField,
                    2,
                    now));
        }

        jobPositionRepository.saveAll(positions);
        log.info("JobPosition 데이터 초기화가 완료되었습니다. 총 {}개의 직무가 생성되었습니다.", positions.size());
    }

    private JobPosition createJobPosition(
            String code,
            String name,
            String nameEn,
            String description,
            String descriptionEn,
            String icon,
            JobField jobField,
            int sortOrder,
            LocalDateTime now) {
        JobPosition position = JobPosition.builder()
                .code(code)
                .name(name)
                .nameEn(nameEn)
                .description(description)
                .descriptionEn(descriptionEn)
                .icon(icon)
                .active(true)
                .sortOrder(sortOrder)
                .createdAt(now)
                .updatedAt(now)
                .build();
        position.setJobField(jobField);
        return position;
    }

    @Override
    public String getEntityName() {
        return "JobPosition";
    }

    @Override
    public int getOrder() {
        return 20; // JobField 다음에 실행
    }

    @Override
    public boolean dependsOn(String entityName) {
        return "JobField".equals(entityName);
    }
}