package com.evawova.preview.domain.user.service;

import com.evawova.preview.domain.user.dto.PlanDto;
import com.evawova.preview.domain.user.entity.Plan;
import com.evawova.preview.domain.user.entity.PlanType;
import com.evawova.preview.domain.user.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlanService {

    private final PlanRepository planRepository;

    public List<PlanDto> getAllPlans() {
        return planRepository.findAll().stream()
                .map(PlanDto::fromEntity)
                .collect(Collectors.toList());
    }

    public PlanDto getPlanByType(PlanType planType) {
        Plan plan = planRepository.findByType(planType)
                .orElseThrow(() -> new IllegalArgumentException("플랜을 찾을 수 없습니다: " + planType));
        return PlanDto.fromEntity(plan);
    }

    public PlanDto getFreePlan() {
        return getPlanByType(PlanType.FREE);
    }
} 