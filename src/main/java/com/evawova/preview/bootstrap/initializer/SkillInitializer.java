package com.evawova.preview.bootstrap.initializer;

import com.evawova.preview.bootstrap.EntityInitializer;
import com.evawova.preview.domain.interview.entity.JobField;
import com.evawova.preview.domain.interview.entity.JobPosition;
import com.evawova.preview.domain.interview.entity.JobPositionSkill;
import com.evawova.preview.domain.interview.entity.Skill;
import com.evawova.preview.domain.interview.repository.JobFieldRepository;
import com.evawova.preview.domain.interview.repository.JobPositionRepository;
import com.evawova.preview.domain.interview.repository.JobPositionSkillRepository;
import com.evawova.preview.domain.interview.repository.SkillRepository;

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

@Slf4j
@Component
@RequiredArgsConstructor
public class SkillInitializer implements EntityInitializer {

    private final SkillRepository skillRepository;
    private final JobFieldRepository jobFieldRepository;
    private final JobPositionRepository jobPositionRepository;
    private final JobPositionSkillRepository jobPositionSkillRepository;

    @Override
    @Transactional
    public void initialize() {
        if (skillRepository.count() > 0) {
            log.info("이미 Skill 데이터가 존재합니다. 초기화를 건너뜁니다.");
            return;
        }

        log.info("Skill 데이터 초기화를 시작합니다.");

        // 직군 데이터 로드
        Map<String, JobField> fieldMap = jobFieldRepository.findAll().stream()
                .collect(Collectors.toMap(JobField::getCode, Function.identity()));

        // 직무 데이터 로드
        Map<String, JobPosition> positionMap = jobPositionRepository.findAll().stream()
                .collect(Collectors.toMap(JobPosition::getCode, Function.identity()));

        if (fieldMap.isEmpty() || positionMap.isEmpty()) {
            log.error("직군 또는 직무 데이터가 없습니다. Skill 초기화를 건너뜁니다.");
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        List<Skill> skills = new ArrayList<>();

        // 개발 관련 스킬
        String devField = JobField.DEVELOPMENT;

        // 백엔드 개발 스킬
        skills.add(createSkill(Skill.JAVA, "Java", "Java", "JVM 기반의 객체지향 프로그래밍 언어",
                "Object-oriented programming language based on JVM", "java-icon.svg",
                true, devField, 1, now));

        skills.add(createSkill(Skill.PYTHON, "Python", "Python", "범용 고수준 프로그래밍 언어",
                "General-purpose high-level programming language", "python-icon.svg",
                true, devField, 2, now));

        skills.add(createSkill(Skill.SPRING, "Spring", "Spring", "자바 기반 엔터프라이즈 애플리케이션 프레임워크",
                "Java-based enterprise application framework", "spring-icon.svg",
                true, devField, 3, now));

        skills.add(createSkill(Skill.DJANGO, "Django", "Django", "파이썬 기반 웹 프레임워크",
                "Python-based web framework", "django-icon.svg",
                false, devField, 4, now));

        // 프론트엔드 개발 스킬
        skills.add(createSkill(Skill.JAVASCRIPT, "JavaScript", "JavaScript", "웹 개발을 위한 스크립트 언어",
                "Scripting language for web development", "javascript-icon.svg",
                true, devField, 5, now));

        skills.add(createSkill(Skill.REACT, "React", "React", "사용자 인터페이스 구축을 위한 자바스크립트 라이브러리",
                "JavaScript library for building user interfaces", "react-icon.svg",
                true, devField, 6, now));

        skills.add(createSkill(Skill.ANGULAR, "Angular", "Angular", "타입스크립트 기반 프론트엔드 프레임워크",
                "TypeScript-based front-end framework", "angular-icon.svg",
                false, devField, 7, now));

        skills.add(createSkill(Skill.VUE, "Vue.js", "Vue.js", "사용자 인터페이스 구축을 위한 프로그레시브 프레임워크",
                "Progressive framework for building user interfaces", "vue-icon.svg",
                false, devField, 8, now));

        // 디자인 관련 스킬
        String designField = JobField.DESIGN;

        skills.add(createSkill(Skill.FIGMA, "Figma", "Figma", "협업 기반 인터페이스 디자인 도구",
                "Collaborative interface design tool", "figma-icon.svg",
                true, designField, 1, now));

        skills.add(createSkill(Skill.SKETCH, "Sketch", "Sketch", "벡터 기반 디자인 도구",
                "Vector-based design tool", "sketch-icon.svg",
                false, designField, 2, now));

        // 스킬 저장
        List<Skill> savedSkills = skillRepository.saveAll(skills);
        log.info("Skill 데이터 초기화가 완료되었습니다. 총 {}개의 스킬이 생성되었습니다.", savedSkills.size());

        // 직무와 스킬 연결
        linkSkillsToPositions(positionMap, savedSkills);
    }

    private Skill createSkill(
            String code,
            String name,
            String nameEn,
            String description,
            String descriptionEn,
            String iconUrl,
            boolean isPopular,
            String primaryFieldType,
            int sortOrder,
            LocalDateTime now) {
        return Skill.builder()
                .code(code)
                .name(name)
                .nameEn(nameEn)
                .description(description)
                .descriptionEn(descriptionEn)
                .iconUrl(iconUrl)
                .isPopular(isPopular)
                .primaryFieldType(primaryFieldType)
                .active(true)
                .sortOrder(sortOrder)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    private void linkSkillsToPositions(Map<String, JobPosition> positionMap, List<Skill> skills) {
        Map<String, Skill> skillMap = skills.stream()
                .collect(Collectors.toMap(Skill::getCode, Function.identity()));

        List<JobPositionSkill> mappings = new ArrayList<>();

        // 백엔드 개발자와 스킬 연결
        JobPosition backendDev = positionMap.get(JobPosition.BACKEND_DEVELOPER);
        if (backendDev != null) {
            mappings.add(createSkillMapping(backendDev, skillMap, Skill.JAVA, 10));
            mappings.add(createSkillMapping(backendDev, skillMap, Skill.PYTHON, 8));
            mappings.add(createSkillMapping(backendDev, skillMap, Skill.SPRING, 9));
            mappings.add(createSkillMapping(backendDev, skillMap, Skill.DJANGO, 7));
        }

        // 프론트엔드 개발자와 스킬 연결
        JobPosition frontendDev = positionMap.get(JobPosition.FRONTEND_DEVELOPER);
        if (frontendDev != null) {
            mappings.add(createSkillMapping(frontendDev, skillMap, Skill.JAVASCRIPT, 10));
            mappings.add(createSkillMapping(frontendDev, skillMap, Skill.REACT, 9));
            mappings.add(createSkillMapping(frontendDev, skillMap, Skill.ANGULAR, 8));
            mappings.add(createSkillMapping(frontendDev, skillMap, Skill.VUE, 7));
        }

        // 풀스택 개발자와 스킬 연결
        JobPosition fullstackDev = positionMap.get(JobPosition.FULLSTACK_DEVELOPER);
        if (fullstackDev != null) {
            mappings.add(createSkillMapping(fullstackDev, skillMap, Skill.JAVA, 8));
            mappings.add(createSkillMapping(fullstackDev, skillMap, Skill.JAVASCRIPT, 10));
            mappings.add(createSkillMapping(fullstackDev, skillMap, Skill.REACT, 9));
            mappings.add(createSkillMapping(fullstackDev, skillMap, Skill.SPRING, 7));
        }

        // UI/UX 디자이너와 스킬 연결
        JobPosition uiUxDesigner = positionMap.get(JobPosition.UI_UX_DESIGNER);
        if (uiUxDesigner != null) {
            mappings.add(createSkillMapping(uiUxDesigner, skillMap, Skill.FIGMA, 10));
            mappings.add(createSkillMapping(uiUxDesigner, skillMap, Skill.SKETCH, 8));
        }

        // 매핑 저장
        jobPositionSkillRepository.saveAll(mappings);
        log.info("직무-스킬 매핑 데이터 초기화가 완료되었습니다. 총 {}개의 매핑이 생성되었습니다.", mappings.size());
    }

    private JobPositionSkill createSkillMapping(JobPosition position, Map<String, Skill> skillMap, String skillCode,
            Integer importance) {
        Skill skill = skillMap.get(skillCode);
        if (skill == null) {
            log.warn("스킬을 찾을 수 없습니다: {}", skillCode);
            return null;
        }
        return new JobPositionSkill(position, skill, importance);
    }

    @Override
    public String getEntityName() {
        return "Skill";
    }

    @Override
    public int getOrder() {
        return 30; // JobPosition 다음에 실행
    }

    @Override
    public boolean dependsOn(String entityName) {
        return "JobPosition".equals(entityName);
    }
}