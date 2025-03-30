package com.evawova.preview.domain.config.controller;

import com.evawova.preview.common.response.ApiResponse;
import com.evawova.preview.common.response.ResponseEntityBuilder;
import com.evawova.preview.security.FirebaseUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 사용자 설정 관련 기능을 제공하는 컨트롤러
 */
@RestController
@RequestMapping("/api/v1/config")
@RequiredArgsConstructor
public class ConfigController {
    
    /**
     * 사용자 기본 설정 조회
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserConfig(
            @AuthenticationPrincipal FirebaseUserDetails principal) {
        
        // 예시 데이터 - 실제로는 DB에서 사용자의 설정을 조회해야 함
        Map<String, Object> configs = new HashMap<>();
        configs.put("theme", "dark");
        configs.put("language", "ko");
        configs.put("notifications", true);
        configs.put("autoSave", true);
        
        return ResponseEntityBuilder.success(configs, "사용자 설정 조회 성공");
    }
    
    /**
     * 사용자 기본 설정 업데이트
     */
    @PutMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateUserConfig(
            @AuthenticationPrincipal FirebaseUserDetails principal,
            @RequestBody Map<String, Object> configs) {
        
        // 설정 저장 로직 (예시)
        return ResponseEntityBuilder.success(configs, "사용자 설정 업데이트 성공");
    }
    
    /**
     * 고급 설정 - PRO 사용자만 접근 가능
     */
    @GetMapping("/advanced")
    @PreAuthorize("hasAnyRole('USER_PRO', 'ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAdvancedConfig(
            @AuthenticationPrincipal FirebaseUserDetails principal) {
        
        // 고급 설정 (PRO 사용자 전용)
        Map<String, Object> advancedConfigs = new HashMap<>();
        advancedConfigs.put("customAnalysisParameters", Map.of(
            "depth", 3,
            "precision", "high",
            "algorithm", "advanced"
        ));
        advancedConfigs.put("apiIntegrations", true);
        advancedConfigs.put("customWebhooks", true);
        
        return ResponseEntityBuilder.success(advancedConfigs, "고급 설정 조회 성공");
    }
    
    /**
     * 고급 설정 업데이트 - PRO 사용자만 접근 가능
     */
    @PutMapping("/advanced")
    @PreAuthorize("hasAnyRole('USER_PRO', 'ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateAdvancedConfig(
            @AuthenticationPrincipal FirebaseUserDetails principal,
            @RequestBody Map<String, Object> advancedConfigs) {
        
        // 고급 설정 저장 로직 (예시)
        return ResponseEntityBuilder.success(advancedConfigs, "고급 설정 업데이트 성공");
    }
} 