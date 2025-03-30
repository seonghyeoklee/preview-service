package com.evawova.preview.domain.user.repository;

import com.evawova.preview.domain.user.entity.Subscription;
import com.evawova.preview.domain.user.entity.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Optional<Subscription> findByUserIdAndStatusAndEndDateAfter(
        Long userId, 
        SubscriptionStatus status, 
        LocalDateTime now
    );
} 