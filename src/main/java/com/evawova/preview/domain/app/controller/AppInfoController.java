package com.evawova.preview.domain.app.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.evawova.preview.common.response.ApiResponse;
import com.evawova.preview.common.response.ResponseEntityBuilder;
import com.evawova.preview.domain.app.dto.AppInfoDto;
import com.evawova.preview.domain.app.entity.AppInfo;
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
        return ResponseEntityBuilder.success(AppInfoDto.fromEntity(appInfo), "앱 정보를 성공적으로 조회했습니다.");
    }
    
    /**
     * 법적 정보 조회 (이용약관, 개인정보처리방침 등)
     */
    @GetMapping("/legal/{type}")
    public ResponseEntity<ApiResponse<String>> getLegalInfo(@PathVariable String type) {
        AppInfo appInfo = appInfoService.getLatestAppInfo();
        if (appInfo == null) {
            return ResponseEntity.notFound().build();
        }
        
        String content = switch (type) {
            case "terms" -> appInfo.getTermsOfService();
            case "privacy" -> appInfo.getPrivacyPolicy();
            case "license" -> appInfo.getLicenseInfo();
            case "copyright" -> appInfo.getCopyrightInfo();
            case "cookie" -> appInfo.getCookiePolicy();
            case "youth" -> appInfo.getYouthProtectionPolicy();
            default -> null;
        };
        
        if (content == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntityBuilder.success(content, "법적 정보를 성공적으로 조회했습니다.");
    }
    
    /**
     * 회사 정보 조회
     */
    @GetMapping("/company")
    public ResponseEntity<ApiResponse<AppInfoDto>> getCompanyInfo() {
        AppInfo appInfo = appInfoService.getLatestAppInfo();
        if (appInfo == null) {
            return ResponseEntity.notFound().build();
        }
        
        AppInfoDto companyInfo = AppInfoDto.builder()
                .companyName(appInfo.getCompanyName())
                .businessRegistrationNumber(appInfo.getBusinessRegistrationNumber())
                .representativeName(appInfo.getRepresentativeName())
                .address(appInfo.getAddress())
                .contactEmail(appInfo.getContactEmail())
                .contactPhone(appInfo.getContactPhone())
                .supportHours(appInfo.getSupportHours())
                .websiteUrl(appInfo.getWebsiteUrl())
                .socialMediaLinks(appInfo.getSocialMediaLinks())
                .build();
        
        return ResponseEntityBuilder.success(companyInfo, "회사 정보를 성공적으로 조회했습니다.");
    }
    
    /**
     * FAQ 정보 조회
     */
    @GetMapping("/faq")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getFaq() {
        AppInfo appInfo = appInfoService.getLatestAppInfo();
        if (appInfo == null || appInfo.getFaqContent() == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntityBuilder.success(appInfo.getFaqContent(), "FAQ 정보를 성공적으로 조회했습니다.");
    }
} 