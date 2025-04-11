package com.evawova.preview.domain.user.repository;

import com.evawova.preview.domain.user.entity.Plan;
import com.evawova.preview.domain.user.entity.PlanType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlanRepository extends JpaRepository<Plan, Long> {
    Optional<Plan> findByPlanType(PlanType type);
    boolean existsByPlanType(PlanType type);
} 