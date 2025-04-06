package com.evawova.preview.bootstrap;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 애플리케이션 부트스트랩 시 데이터 초기화를 담당하는 클래스
 * "local" 프로필이 활성화된 경우에만 실행됩니다.
 */
@Slf4j
@Component
@Profile("local")
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final Environment environment;
    private final EntityInitializerRegistry initializerRegistry;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("데이터 초기화를 시작합니다. 활성 프로필: {}", String.join(", ", environment.getActiveProfiles()));

        // 모든 등록된 엔티티 초기화 도구를 순차적으로 실행
        initializerRegistry.getAllInitializers().forEach(initializer -> {
            try {
                log.info("{}를 초기화합니다...", initializer.getEntityName());
                initializer.initialize();
                log.info("{}를 성공적으로 초기화했습니다.", initializer.getEntityName());
            } catch (Exception e) {
                log.error("{}를 초기화하는 중 오류가 발생했습니다: {}", initializer.getEntityName(), e.getMessage(), e);
            }
        });

        log.info("데이터 초기화가 완료되었습니다.");
    }
}