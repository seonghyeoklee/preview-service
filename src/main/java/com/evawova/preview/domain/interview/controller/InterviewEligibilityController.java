package com.evawova.preview.domain.interview.controller;

import com.evawova.preview.common.response.ApiResponse;
import com.evawova.preview.domain.interview.service.InterviewEligibilityService;
import com.evawova.preview.domain.interview.service.InterviewEligibilityService.InterviewEligibilityResult;
import com.evawova.preview.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 면접 자격 검증 컨트롤러
 * - 사용자의 면접 진행 가능 여부를 확인하는 API 제공
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/interview/eligibility")
@RequiredArgsConstructor
public class InterviewEligibilityController {

    private final InterviewEligibilityService interviewEligibilityService;

    /**
     * 현재 로그인한 사용자의 면접 진행 가능 여부 확인
     * 
     * @param user 인증된 사용자 정보
     * @return 면접 진행 가능 여부와 메시지
     */
    @GetMapping("/check")
    public ApiResponse<Map<String, Object>> checkEligibility(@AuthenticationPrincipal User user) {
        if (user == null) {
            return ApiResponse.error("인증된 사용자 정보가 없습니다. 로그인이 필요합니다.");
        }

        log.info("사용자 {} 면접 자격 검증 시작", user.getId());
        InterviewEligibilityResult result = interviewEligibilityService.checkEligibility(user);

        Map<String, Object> response = new HashMap<>();
        response.put("eligible", result.isEligible());
        response.put("message", result.getMessage());
        response.put("timestamp", result.getTimestamp());
        response.put("userId", user.getId());
        response.put("plan", user.getPlan() != null ? user.getPlan().getType().name() : "NONE");

        if (result.isEligible()) {
            log.info("사용자 {} 면접 자격 검증 통과", user.getId());
        } else {
            log.warn("사용자 {} 면접 자격 검증 실패: {}", user.getId(), result.getMessage());
        }

        return ApiResponse.success(response);
    }
}