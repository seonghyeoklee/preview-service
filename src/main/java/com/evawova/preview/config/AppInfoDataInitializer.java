package com.evawova.preview.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.evawova.preview.domain.app.entity.AppInfo;
import com.evawova.preview.domain.app.repository.AppInfoRepository;
import com.evawova.preview.domain.app.service.AppInfoService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class AppInfoDataInitializer {

    private final AppInfoService appInfoService;
    private final AppInfoRepository appInfoRepository;
    
    /**
     * 개발 환경에서만 실행되는 샘플 데이터 초기화
     */
    @Bean
    @Profile({"local", "dev"})
    public CommandLineRunner initializeAppInfo() {
        return args -> {
            // 기존 데이터가 없으면 샘플 데이터 생성
            if (appInfoRepository.count() == 0) {
                log.info("초기 AppInfo 데이터를 생성합니다...");
                List<AppInfo> appInfos = appInfoService.createSampleAppInfos();
                log.info("AppInfo 데이터 {}개가 생성되었습니다.", appInfos.size());
                
                // 생성된 데이터 로그 출력
                for (AppInfo appInfo : appInfos) {
                    log.info("- AppInfo 생성: {} (버전: {})", appInfo.getAppName(), appInfo.getAppVersion());
                }
            } else {
                log.info("이미 AppInfo 데이터가 존재합니다. 초기화를 건너뜁니다.");
            }
        };
    }
} 