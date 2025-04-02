package com.evawova.preview.domain.admin.controller;

import com.evawova.preview.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 관리자 전용 기능을 제공하는 컨트롤러
 */
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    
    /**
     * 시스템 통계 정보 조회
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSystemStats() {
        // 시스템 통계 정보 (예시)
        Map<String, Object> systemStats = new HashMap<>();
        systemStats.put("activeUsers", 1250);
        systemStats.put("tokensUsedToday", 500000);
        systemStats.put("requestsPerMinute", 42);
        systemStats.put("averageResponseTime", 120);
        systemStats.put("errorRate", 0.02);
        
        return ResponseEntity.ok(ApiResponse.success(systemStats, "시스템 통계 정보 조회 성공"));
    }
    
    /**
     * 시스템 서비스 재시작
     */
    @PostMapping("/system/restart")
    public ResponseEntity<ApiResponse<String>> restartService() {
        // 서비스 재시작 기능 (예시)
        return ResponseEntity.ok(ApiResponse.success("서비스가 성공적으로 재시작되었습니다.", "서비스 재시작 성공"));
    }
    
    /**
     * 사용자 활동 로그 조회
     */
    @GetMapping("/user-activities")
    public ResponseEntity<ApiResponse<List<String>>> getUserActivities() {
        // 최근 사용자 활동 로그 (예시)
        List<String> activities = List.of(
            "사용자 A가 PRO 플랜으로 업그레이드 (10분 전)",
            "사용자 B가 프리미엄 분석 기능 사용 (15분 전)",
            "사용자 C가 회원 가입 (30분 전)",
            "사용자 D가 계정 삭제 (1시간 전)"
        );
        
        return ResponseEntity.ok(ApiResponse.success(activities, "사용자 활동 로그 조회 성공"));
    }
    
    /**
     * 시스템 설정 조회
     */
    @GetMapping("/system/config")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSystemConfig() {
        // 시스템 설정 정보 (예시)
        Map<String, Object> config = new HashMap<>();
        config.put("maintenanceMode", false);
        config.put("debugMode", true);
        config.put("maxConcurrentRequests", 100);
        config.put("tokenRateLimiting", Map.of(
            "enabled", true,
            "requestsPerMinute", 60,
            "burstCapacity", 120
        ));
        
        return ResponseEntity.ok(ApiResponse.success(config, "시스템 설정 조회 성공"));
    }
} 