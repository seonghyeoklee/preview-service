package com.evawova.preview;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@EnableJpaAuditing
@Slf4j
@RequiredArgsConstructor
public class PreviewServiceApplication {

    private final Environment environment;

    public static void main(String[] args) {
        SpringApplication.run(PreviewServiceApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void logApplicationStartup() {
        String[] activeProfiles = environment.getActiveProfiles();
        String activeProfile = activeProfiles.length > 0 ? activeProfiles[0] : "default";
        String datasourceUrl = environment.getProperty("spring.datasource.url");
        String datasourceDriver = environment.getProperty("spring.datasource.driver-class-name");

        log.info("=======================================================");
        log.info("Application is running with profile: {}", activeProfile);
        log.info("Database URL: {}", datasourceUrl);
        log.info("Database Driver: {}", datasourceDriver);
        log.info("=======================================================");
    }
}
