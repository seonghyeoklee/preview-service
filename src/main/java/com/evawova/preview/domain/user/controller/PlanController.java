package com.evawova.preview.domain.user.controller;

import com.evawova.preview.domain.user.dto.PlanDto;
import com.evawova.preview.domain.user.entity.PlanType;
import com.evawova.preview.domain.user.service.PlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/plans")
@RequiredArgsConstructor
@Tag(name = "Plans", description = "플랜 관련 API")
public class PlanController {

    private final PlanService planService;

    @Operation(summary = "모든 플랜 목록 조회", description = "사용 가능한 모든 플랜의 정보를 조회합니다.")
    @GetMapping
    public ResponseEntity<List<PlanDto>> getAllPlans(
        @Parameter(description = "가격 범위의 최소값 (월 기준, 원)", in = ParameterIn.QUERY) 
        @RequestParam(required = false) Integer minPrice,
        
        @Parameter(description = "가격 범위의 최대값 (월 기준, 원)", in = ParameterIn.QUERY) 
        @RequestParam(required = false) Integer maxPrice,
        
        @Parameter(description = "플랜 정렬 방식 (price_asc, price_desc, name_asc, name_desc)", 
                  schema = @Schema(allowableValues = {"price_asc", "price_desc", "name_asc", "name_desc"})) 
        @RequestParam(defaultValue = "price_asc") String sort
    ) {
        // 현재는 실제 필터링/정렬을 구현하지 않고 모든 플랜을 반환
        List<PlanDto> plans = planService.getAllPlans();
        return ResponseEntity.ok(plans);
    }

    @Operation(summary = "플랜 타입으로 조회", description = "지정된 타입의 플랜 정보를 조회합니다.")
    @GetMapping("/{type}")
    public ResponseEntity<PlanDto> getPlanByType(@PathVariable("type") String type) {
        try {
            PlanType planType = PlanType.valueOf(type.toUpperCase());
            PlanDto plan = planService.getPlanByType(planType);
            return ResponseEntity.ok(plan);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "플랜 비교", description = "두 개의 플랜을 비교합니다.")
    @GetMapping("/compare")
    public ResponseEntity<String> comparePlans(
        @Parameter(description = "비교할 첫 번째 플랜 타입 (FREE, STANDARD, PRO)", required = true)
        @RequestParam String plan1,
        
        @Parameter(description = "비교할 두 번째 플랜 타입 (FREE, STANDARD, PRO)", required = true)
        @RequestParam String plan2
    ) {
        try {
            PlanType planType1 = PlanType.valueOf(plan1.toUpperCase());
            PlanType planType2 = PlanType.valueOf(plan2.toUpperCase());
            
            // 실제 구현에서는 두 플랜의 정보를 비교하여 결과를 반환
            PlanDto planDto1 = planService.getPlanByType(planType1);
            PlanDto planDto2 = planService.getPlanByType(planType2);
            
            // 간단한 비교 결과 문자열 반환 (실제로는 더 복잡한 DTO를 반환할 수 있음)
            return ResponseEntity.ok(
                String.format("%s 플랜과 %s 플랜의 비교:\n" +
                    "- 스토리지: %dGB vs %dGB\n" +
                    "- 프로젝트 수: %d개 vs %d개\n" +
                    "- 월 요금: %d원 vs %d원",
                    planDto1.getName(), planDto2.getName(),
                    planDto1.getStorageSizeGB(), planDto2.getStorageSizeGB(),
                    planDto1.getMaxProjectCount(), planDto2.getMaxProjectCount(),
                    planDto1.getMonthlyPrice(), planDto2.getMonthlyPrice()
                )
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("잘못된 플랜 타입이 입력되었습니다.");
        }
    }
} 