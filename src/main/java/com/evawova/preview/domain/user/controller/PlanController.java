package com.evawova.preview.domain.user.controller;

import com.evawova.preview.common.exception.ApiException;
import com.evawova.preview.common.response.ApiResponse;
import com.evawova.preview.common.response.ResponseEntityBuilder;
import com.evawova.preview.domain.user.dto.PlanDto;
import com.evawova.preview.domain.user.entity.PlanType;
import com.evawova.preview.domain.user.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/plans")
@RequiredArgsConstructor
public class PlanController {

    private final PlanService planService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<PlanDto>>> getAllPlans(
        @RequestParam(value = "minPrice", required = false) Integer minPrice,
        @RequestParam(value = "maxPrice", required = false) Integer maxPrice,
        @RequestParam(value = "sort", defaultValue = "price_asc") String sort
    ) {
        // 현재는 실제 필터링/정렬을 구현하지 않고 모든 플랜을 반환
        List<PlanDto> plans = planService.getAllPlans();
        return ResponseEntityBuilder.success(plans, "플랜 목록을 성공적으로 조회했습니다.");
    }

    @GetMapping("/{type}")
    public ResponseEntity<ApiResponse<PlanDto>> getPlanByType(@PathVariable("type") String type) {
        try {
            PlanType planType = PlanType.valueOf(type.toUpperCase());
            PlanDto plan = planService.getPlanByType(planType);
            return ResponseEntityBuilder.success(plan, "플랜 정보를 성공적으로 조회했습니다.");
        } catch (IllegalArgumentException e) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "유효하지 않은 플랜 타입입니다: " + type);
        }
    }

    @GetMapping("/compare")
    public ResponseEntity<ApiResponse<Map<String, Object>>> comparePlans(
        @RequestParam(value = "plan1") String plan1,
        @RequestParam(value = "plan2") String plan2
    ) {
        try {
            PlanType planType1 = PlanType.valueOf(plan1.toUpperCase());
            PlanType planType2 = PlanType.valueOf(plan2.toUpperCase());
            
            // 실제 구현에서는 두 플랜의 정보를 비교하여 결과를 반환
            PlanDto planDto1 = planService.getPlanByType(planType1);
            PlanDto planDto2 = planService.getPlanByType(planType2);
            
            // 비교 결과를 맵으로 구성
            Map<String, Object> comparisonResult = new HashMap<>();
            comparisonResult.put("plan1", planDto1);
            comparisonResult.put("plan2", planDto2);
            comparisonResult.put("comparison", Map.of(
                "storage", Map.of(
                    "plan1", planDto1.getStorageSizeGB(),
                    "plan2", planDto2.getStorageSizeGB(),
                    "difference", planDto2.getStorageSizeGB() - planDto1.getStorageSizeGB()
                ),
                "projects", Map.of(
                    "plan1", planDto1.getMaxProjectCount(),
                    "plan2", planDto2.getMaxProjectCount(),
                    "difference", planDto2.getMaxProjectCount() - planDto1.getMaxProjectCount()
                ),
                "monthlyPrice", Map.of(
                    "plan1", planDto1.getMonthlyPrice(),
                    "plan2", planDto2.getMonthlyPrice(),
                    "difference", planDto2.getMonthlyPrice() - planDto1.getMonthlyPrice()
                ),
                "features", Map.of(
                    "teamCollaboration", Map.of(
                        "plan1", planDto1.getTeamCollaboration(),
                        "plan2", planDto2.getTeamCollaboration()
                    ),
                    "prioritySupport", Map.of(
                        "plan1", planDto1.getPrioritySupport(),
                        "plan2", planDto2.getPrioritySupport()
                    ),
                    "customDomain", Map.of(
                        "plan1", planDto1.getCustomDomain(),
                        "plan2", planDto2.getCustomDomain()
                    )
                )
            ));
            
            return ResponseEntityBuilder.success(comparisonResult, 
                    String.format("%s 플랜과 %s 플랜의 비교가 완료되었습니다.", planDto1.getName(), planDto2.getName()));
            
        } catch (IllegalArgumentException e) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "유효하지 않은 플랜 타입이 입력되었습니다.");
        }
    }
} 