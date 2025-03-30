package com.evawova.preview.domain.quota.controller;

import com.evawova.preview.common.response.ApiResponse;
import com.evawova.preview.common.response.ResponseEntityBuilder;
import com.evawova.preview.security.FirebaseUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 사용자 할당량 관련 기능을 제공하는 컨트롤러
 */
@RestController
@RequestMapping("/api/v1/quota")
@RequiredArgsConstructor
public class QuotaController {
    
    /**
     * 사용자의 현재 할당량 및 사용량 정보 조회
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserQuota(
            @AuthenticationPrincipal FirebaseUserDetails principal) {
        
        // 여기서는 예시 데이터만 반환하지만, 실제로는 사용자의 플랜 정보를 조회하여 적절한 값을 계산해야 함
        Map<String, Object> quotaInfo = new HashMap<>();
        
        // 플랜 타입에 따라 적절한 할당량 정보 제공 (예시)
        String role = principal.getAuthorities().stream()
                .findFirst()
                .map(auth -> auth.getAuthority())
                .orElse("ROLE_USER_FREE");
        
        if (role.equals("ROLE_USER_PRO")) {
            quotaInfo.put("monthlyTokenLimit", 100000);
            quotaInfo.put("usedTokens", 23456);
            quotaInfo.put("remainingTokens", 76544);
        } else if (role.equals("ROLE_USER_STANDARD")) {
            quotaInfo.put("monthlyTokenLimit", 50000);
            quotaInfo.put("usedTokens", 12345);
            quotaInfo.put("remainingTokens", 37655);
        } else {
            quotaInfo.put("monthlyTokenLimit", 10000);
            quotaInfo.put("usedTokens", 1234);
            quotaInfo.put("remainingTokens", 8766);
        }
        
        quotaInfo.put("resetDate", "2023-06-01");
        quotaInfo.put("usagePercentage", 
                ((Integer)quotaInfo.get("usedTokens") * 100.0 / (Integer)quotaInfo.get("monthlyTokenLimit")));
        
        return ResponseEntityBuilder.success(quotaInfo, "할당량 정보 조회 성공");
    }
    
    /**
     * 사용자의 할당량 사용 이력 조회
     */
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getQuotaHistory(
            @AuthenticationPrincipal FirebaseUserDetails principal) {
        
        // 예시 데이터 - 실제로는 DB에서 사용자의 토큰 사용 이력을 조회해야 함
        Map<String, Object> history = new HashMap<>();
        history.put("daily", Map.of(
            "2023-05-26", 234,
            "2023-05-27", 567,
            "2023-05-28", 890,
            "2023-05-29", 321,
            "2023-05-30", 432
        ));
        
        history.put("weekly", Map.of(
            "Week 1", 1200,
            "Week 2", 2300,
            "Week 3", 1800,
            "Week 4", 2100
        ));
        
        return ResponseEntityBuilder.success(history, "할당량 사용 이력 조회 성공");
    }
} 