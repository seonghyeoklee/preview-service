package com.evawova.preview.bootstrap.initializer;

import com.evawova.preview.bootstrap.EntityInitializer;
import com.evawova.preview.domain.interview.entity.JobField;
import com.evawova.preview.domain.interview.repository.JobFieldRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * JobField 엔티티 초기화 클래스
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JobFieldInitializer implements EntityInitializer {

    private final JobFieldRepository jobFieldRepository;

    @Override
    @Transactional
    public void initialize() {
        if (jobFieldRepository.count() > 0) {
            log.info("이미 JobField 데이터가 존재합니다. 초기화를 건너뜁니다.");
            return;
        }

        log.info("JobField 데이터 초기화를 시작합니다.");
        LocalDateTime now = LocalDateTime.now();
        List<JobField> fields = new ArrayList<>();

        fields.add(JobField.builder()
                .code(JobField.DEVELOPMENT)
                .name("개발")
                .nameEn("Development")
                .description("소프트웨어 개발, 시스템 설계, 코딩 관련 직군")
                .descriptionEn("Roles related to software development, system design, and coding")
                .icon("Icons.developer_mode")
                .active(true)
                .sortOrder(1)
                .createdAt(now)
                .updatedAt(now)
                .build());

        fields.add(JobField.builder()
                .code(JobField.DESIGN)
                .name("디자인")
                .nameEn("Design")
                .description("UI/UX 디자인, 그래픽 디자인, 제품 디자인 관련 직군")
                .descriptionEn("Roles related to UI/UX design, graphic design, and product design")
                .icon("Icons.design_services")
                .active(true)
                .sortOrder(2)
                .createdAt(now)
                .updatedAt(now)
                .build());

        fields.add(JobField.builder()
                .code(JobField.MARKETING)
                .name("마케팅")
                .nameEn("Marketing")
                .description("디지털 마케팅, 콘텐츠 마케팅, 브랜드 마케팅 관련 직군")
                .descriptionEn("Roles related to digital marketing, content marketing, and brand marketing")
                .icon("Icons.campaign")
                .active(true)
                .sortOrder(3)
                .createdAt(now)
                .updatedAt(now)
                .build());

        fields.add(JobField.builder()
                .code(JobField.BUSINESS)
                .name("경영지원")
                .nameEn("Business Support")
                .description("인사, 재무, 회계, 법률, 총무 등 경영 지원 관련 직군")
                .descriptionEn("Roles related to HR, finance, accounting, legal, and general affairs")
                .icon("Icons.business_center")
                .active(true)
                .sortOrder(4)
                .createdAt(now)
                .updatedAt(now)
                .build());

        fields.add(JobField.builder()
                .code(JobField.SALES)
                .name("영업/세일즈")
                .nameEn("Sales")
                .description("영업 전략 수립, 고객 관리, 판매 활동 관련 직군")
                .descriptionEn("Roles related to sales strategy, customer management, and sales activities")
                .icon("Icons.point_of_sale")
                .active(true)
                .sortOrder(5)
                .createdAt(now)
                .updatedAt(now)
                .build());

        fields.add(JobField.builder()
                .code(JobField.CUSTOMER_SERVICE)
                .name("고객 지원")
                .nameEn("Customer Service")
                .description("고객 상담, 기술 지원, 고객 만족 관리 관련 직군")
                .descriptionEn(
                        "Roles related to customer consultation, technical support, and customer satisfaction management")
                .icon("Icons.support_agent")
                .active(true)
                .sortOrder(6)
                .createdAt(now)
                .updatedAt(now)
                .build());

        jobFieldRepository.saveAll(fields);
        log.info("JobField 데이터 초기화가 완료되었습니다. 총 {}개의 직군이 생성되었습니다.", fields.size());
    }

    @Override
    public String getEntityName() {
        return "JobField";
    }

    @Override
    public int getOrder() {
        return 10; // 높은 우선순위로 초기화
    }
}