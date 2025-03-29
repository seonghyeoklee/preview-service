package com.evawova.preview.domain.user.controller;

import com.evawova.preview.domain.user.dto.PlanDto;
import com.evawova.preview.domain.user.service.PlanService;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<List<PlanDto>> getAllPlans() {
        return ResponseEntity.ok(planService.getAllPlans());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanDto> getPlanById(@PathVariable Long id) {
        return ResponseEntity.ok(planService.getPlanById(id));
    }

    @GetMapping("/compare")
    public ResponseEntity<Map<String, Object>> comparePlans(
            @RequestParam Long plan1Id,
            @RequestParam Long plan2Id
    ) {
        PlanDto planDto1 = planService.getPlanById(plan1Id);
        PlanDto planDto2 = planService.getPlanById(plan2Id);

        Map<String, Object> comparisonResult = new HashMap<>();
        comparisonResult.put("plan1", planDto1);
        comparisonResult.put("plan2", planDto2);
        comparisonResult.put("comparison", Map.of(
            "monthlyTokenLimit", Map.of(
                "plan1", planDto1.getMonthlyTokenLimit(),
                "plan2", planDto2.getMonthlyTokenLimit(),
                "difference", planDto2.getMonthlyTokenLimit() - planDto1.getMonthlyTokenLimit()
            ),
            "monthlyPrice", Map.of(
                "plan1", planDto1.getMonthlyPrice(),
                "plan2", planDto2.getMonthlyPrice(),
                "difference", planDto2.getMonthlyPrice() - planDto1.getMonthlyPrice()
            ),
            "annualPrice", Map.of(
                "plan1", planDto1.getAnnualPrice(),
                "plan2", planDto2.getAnnualPrice(),
                "difference", planDto2.getAnnualPrice() - planDto1.getAnnualPrice()
            )
        ));

        return ResponseEntity.ok(comparisonResult);
    }
} 