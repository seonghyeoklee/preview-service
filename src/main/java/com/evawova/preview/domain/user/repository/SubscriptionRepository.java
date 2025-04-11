package com.evawova.preview.domain.user.repository;

import com.evawova.preview.domain.user.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    List<Subscription> findByUserId(Long userId);

    Optional<Subscription> findActiveByUserId(Long userId);

    List<Subscription> findByPlanId(Long planId);
}