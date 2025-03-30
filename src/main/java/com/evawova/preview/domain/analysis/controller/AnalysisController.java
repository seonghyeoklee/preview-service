package com.evawova.preview.domain.analysis.controller;

import com.evawova.preview.common.response.ApiResponse;
import com.evawova.preview.common.response.ResponseEntityBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 분석 관련 기능을 제공하는 컨트롤러
 */
@RestController
@RequestMapping("/api/v1/analysis")
@RequiredArgsConstructor
public class AnalysisController {
    
    /**
     * 기본 분석 기능 - 모든 인증된 사용자 접근 가능
     */
    @GetMapping("/basic")
    public ResponseEntity<ApiResponse<String>> getBasicAnalysis() {
        return ResponseEntityBuilder.success("기본 분석 결과입니다.", "기본 분석 성공");
    }
    
    /**
     * 고급 분석 기능 - STANDARD 이상 플랜 사용자만 접근 가능
     */
    @GetMapping("/advanced")
    @PreAuthorize("hasAnyRole('USER_STANDARD', 'USER_PRO', 'ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAdvancedAnalysis() {
        Map<String, Object> analysisResult = Map.of(
            "accuracy", 92.5,
            "speed", "fast",
            "recommendations", Arrays.asList("추천1", "추천2", "추천3")
        );
        
        return ResponseEntityBuilder.success(analysisResult, "고급 분석 결과 조회 성공");
    }
    
    /**
     * 프리미엄 분석 기능 - PRO 플랜 사용자만 접근 가능
     */
    @GetMapping("/premium")
    @PreAuthorize("hasAnyRole('USER_PRO', 'ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPremiumAnalysis() {
        Map<String, Object> analysisResult = new HashMap<>();
        analysisResult.put("accuracy", 98.9);
        analysisResult.put("speed", "ultra-fast");
        analysisResult.put("recommendations", Arrays.asList("프리미엄 추천1", "프리미엄 추천2", "프리미엄 추천3", "프리미엄 추천4"));
        analysisResult.put("detailedMetrics", Map.of(
            "precision", 0.95,
            "recall", 0.97,
            "f1Score", 0.96
        ));
        
        return ResponseEntityBuilder.success(analysisResult, "프리미엄 분석 결과 조회 성공");
    }
} 