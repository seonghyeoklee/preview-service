package com.evawova.preview.domain.user.repository;

import com.evawova.preview.domain.user.entity.User;
import com.evawova.preview.domain.user.entity.UserLoginLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface UserLoginLogRepository extends JpaRepository<UserLoginLog, Long> {
    List<UserLoginLog> findByUserOrderByLoginAtDesc(User user);

    Page<UserLoginLog> findByUserOrderByLoginAtDesc(User user, Pageable pageable);

    List<UserLoginLog> findByUserAndLoginAtBetweenOrderByLoginAtDesc(
            User user, LocalDateTime startDateTime, LocalDateTime endDateTime);
}