package com.evawova.preview.domain.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.evawova.preview.domain.app.entity.AppInfo;

@Repository
public interface AppInfoRepository extends JpaRepository<AppInfo, Long> {
    // 가장 최신 버전의 앱 정보 조회
    AppInfo findTopByOrderByUpdatedAtDesc();
    
    // 앱 버전으로 조회
    AppInfo findByAppVersion(String appVersion);
} 