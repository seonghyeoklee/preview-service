package com.evawova.preview.domain.user.repository;

import com.evawova.preview.domain.user.entity.UserUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserUsageRepository extends JpaRepository<UserUsage, Long> {
    List<UserUsage> findByUserIdAndUsageDateBetween(Long userId, LocalDateTime startDate, LocalDateTime endDate);
} 