package com.evawova.preview.controller;

import com.evawova.preview.common.response.ApiResponse;
import com.evawova.preview.domain.interview.entity.Skill;
import com.evawova.preview.domain.interview.repository.SkillRepository;
import com.evawova.preview.service.ImageVerificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * 스킬 관련 진단 API 컨트롤러
 * - 개발자와 관리자를 위한 진단 도구
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/diagnostic/skills")
@RequiredArgsConstructor
public class SkillDiagnosticController {

    private final SkillRepository skillRepository;
    private final ImageVerificationService imageVerificationService;

    /**
     * 모든 스킬 아이콘 URL의 유효성을 검사
     * 
     * @return 각 스킬의 이미지 URL 유효성 여부
     */
    @GetMapping("/icons/verify")
    public ApiResponse<Map<String, Object>> verifyAllSkillIcons() {
        List<Skill> skills = skillRepository.findAll();
        log.info("스킬 아이콘 URL 검증 시작: {} 개의 스킬", skills.size());

        // 각 스킬 아이콘 URL에 대해 비동기 검증 수행
        Map<String, CompletableFuture<Boolean>> validationFutures = new HashMap<>();

        for (Skill skill : skills) {
            if (skill.getIcon() != null && !skill.getIcon().isEmpty()) {
                CompletableFuture<Boolean> future = imageVerificationService.verifyImageUrlAsync(skill.getIcon());
                validationFutures.put(skill.getName(), future);
            }
        }

        // 결과 수집
        Map<String, Object> results = new HashMap<>();
        Map<String, Boolean> validationResults = new HashMap<>();
        List<Map<String, String>> invalidIcons = validationFutures.entrySet().stream()
                .filter(entry -> {
                    try {
                        return !entry.getValue().get(); // 유효하지 않은 URL만 필터링
                    } catch (InterruptedException | ExecutionException e) {
                        log.error("이미지 URL 검증 처리 중 오류: {}", e.getMessage());
                        return true; // 오류 발생 시 유효하지 않은 것으로 처리
                    }
                })
                .map(entry -> {
                    Skill skill = skills.stream()
                            .filter(s -> s.getName().equals(entry.getKey()))
                            .findFirst()
                            .orElse(null);

                    Map<String, String> invalidIcon = new HashMap<>();
                    invalidIcon.put("name", entry.getKey());
                    invalidIcon.put("url", skill != null ? skill.getIcon() : "알 수 없음");
                    return invalidIcon;
                })
                .collect(Collectors.toList());

        // 각 URL의 유효성 결과
        validationFutures.forEach((skillName, future) -> {
            try {
                validationResults.put(skillName, future.get());
            } catch (InterruptedException | ExecutionException e) {
                validationResults.put(skillName, false);
                log.error("스킬 '{}' 이미지 URL 검증 결과 처리 중 오류: {}", skillName, e.getMessage());
            }
        });

        // 결과 요약
        results.put("totalSkills", skills.size());
        results.put("totalChecked", validationResults.size());
        results.put("validCount", validationResults.values().stream().filter(Boolean::valueOf).count());
        results.put("invalidCount", invalidIcons.size());
        results.put("invalidIcons", invalidIcons);
        results.put("validationResults", validationResults);

        log.info("스킬 아이콘 URL 검증 완료: 전체 {}, 유효 {}, 유효하지 않음 {}",
                validationResults.size(),
                validationResults.values().stream().filter(Boolean::valueOf).count(),
                invalidIcons.size());

        return ApiResponse.success(results);
    }
}