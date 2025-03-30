package com.evawova.preview.config;

import com.evawova.preview.domain.user.service.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class InitService {

    private final UserService userService;

    @PostConstruct
    public void init() {
        log.info("사용자 역할 마이그레이션 시작...");
        userService.migrateUserRoles();
        log.info("사용자 역할 마이그레이션 완료");
    }
} 