package com.evawova.preview.domain.app.controller;

import java.util.Map;
import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.format.annotation.DateTimeFormat;

import com.evawova.preview.common.response.ApiResponse;
import com.evawova.preview.domain.app.dto.AppInfoDto;
import com.evawova.preview.domain.app.dto.CompanyInfoDto;
import com.evawova.preview.domain.app.dto.ServiceStatusInfoDto;
import com.evawova.preview.domain.app.dto.DeploymentInfoDto;
import com.evawova.preview.domain.app.entity.AppInfo;
import com.evawova.preview.domain.app.entity.ServiceStatus;
import com.evawova.preview.domain.app.service.AppInfoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/app")
@RequiredArgsConstructor
public class AppInfoController {

    private final AppInfoService appInfoService;

    /**
     * 최신 앱 정보 조회
     */
    @GetMapping("/info")
    public ResponseEntity<ApiResponse<AppInfoDto>> getLatestAppInfo() {
        AppInfo appInfo = appInfoService.getLatestAppInfo();
        if (appInfo == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ApiResponse.success(AppInfoDto.fromEntity(appInfo), "앱 정보를 성공적으로 조회했습니다."));
    }

    /**
     * 법적 정보 조회 (이용약관, 개인정보처리방침 등)
     */
    @GetMapping("/legal/{type}")
    public ResponseEntity<ApiResponse<String>> getLegalInfo(@PathVariable String type) {
        AppInfo appInfo = appInfoService.getLatestAppInfo();
        if (appInfo == null || appInfo.getLegalInfo() == null) {
            return ResponseEntity.notFound().build();
        }

        String content = switch (type) {
            case "terms" -> appInfo.getLegalInfo().getTermsOfService();
            case "privacy" -> appInfo.getLegalInfo().getPrivacyPolicy();
            case "license" -> appInfo.getLegalInfo().getLicenseInfo();
            case "copyright" -> appInfo.getLegalInfo().getCopyrightInfo();
            case "cookie" -> appInfo.getLegalInfo().getCookiePolicy();
            case "youth" -> appInfo.getLegalInfo().getYouthProtectionPolicy();
            default -> null;
        };

        if (content == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(ApiResponse.success(content, "법적 정보를 성공적으로 조회했습니다."));
    }

    /**
     * 회사 정보 조회
     */
    @GetMapping("/company")
    public ResponseEntity<ApiResponse<CompanyInfoDto>> getCompanyInfo() {
        AppInfo appInfo = appInfoService.getLatestAppInfo();
        if (appInfo == null || appInfo.getCompanyInfo() == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(ApiResponse.success(CompanyInfoDto.fromEntity(appInfo.getCompanyInfo()),
                "회사 정보를 성공적으로 조회했습니다."));
    }

    /**
     * FAQ 정보 조회
     */
    @GetMapping("/faq")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getFaq() {
        AppInfo appInfo = appInfoService.getLatestAppInfo();
        if (appInfo == null || appInfo.getServiceStatusInfo() == null ||
                appInfo.getServiceStatusInfo().getFaqContent() == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(ApiResponse.success(appInfo.getServiceStatusInfo().getFaqContent(),
                "FAQ 정보를 성공적으로 조회했습니다."));
    }

    /**
     * 배포 정보 조회
     */
    @GetMapping("/deployment")
    public ResponseEntity<ApiResponse<DeploymentInfoDto>> getDeploymentInfo() {
        AppInfo appInfo = appInfoService.getLatestAppInfo();
        if (appInfo == null || appInfo.getDeploymentInfo() == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(ApiResponse.success(DeploymentInfoDto.fromEntity(appInfo.getDeploymentInfo()),
                "배포 정보를 성공적으로 조회했습니다."));
    }

    /**
     * 서비스 상태 업데이트
     */
    @PutMapping("/status")
    public ResponseEntity<ApiResponse<ServiceStatusInfoDto>> updateServiceStatus(
            @RequestParam ServiceStatus status,
            @RequestParam String message) {
        AppInfo appInfo = appInfoService.getLatestAppInfo();
        if (appInfo == null) {
            return ResponseEntity.notFound().build();
        }

        var updatedStatusInfo = appInfoService.updateServiceStatus(appInfo.getId(), status, message);
        return ResponseEntity.ok(ApiResponse.success(ServiceStatusInfoDto.fromEntity(updatedStatusInfo),
                "서비스 상태가 성공적으로 업데이트되었습니다."));
    }

    /**
     * 긴급 공지 설정
     */
    @PostMapping("/emergency-notice")
    public ResponseEntity<ApiResponse<ServiceStatusInfoDto>> setEmergencyNotice(
            @RequestParam String notice,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startAt,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endAt) {
        AppInfo appInfo = appInfoService.getLatestAppInfo();
        if (appInfo == null) {
            return ResponseEntity.notFound().build();
        }

        var updatedStatusInfo = appInfoService.setEmergencyNotice(appInfo.getId(), notice, startAt, endAt);
        return ResponseEntity.ok(ApiResponse.success(ServiceStatusInfoDto.fromEntity(updatedStatusInfo),
                "긴급 공지가 성공적으로 설정되었습니다."));
    }

    /**
     * 긴급 공지 해제
     */
    @DeleteMapping("/emergency-notice")
    public ResponseEntity<ApiResponse<ServiceStatusInfoDto>> clearEmergencyNotice() {
        AppInfo appInfo = appInfoService.getLatestAppInfo();
        if (appInfo == null) {
            return ResponseEntity.notFound().build();
        }

        var updatedStatusInfo = appInfoService.clearEmergencyNotice(appInfo.getId());
        return ResponseEntity.ok(ApiResponse.success(ServiceStatusInfoDto.fromEntity(updatedStatusInfo),
                "긴급 공지가 성공적으로 해제되었습니다."));
    }
}