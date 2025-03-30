package com.evawova.preview.domain.app.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.evawova.preview.domain.app.entity.AppInfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppInfoDto {
    private Long id;
    private String appName;
    private String appVersion;
    private LocalDateTime lastUpdatedAt;
    private String appDescription;
    private String logoUrl;
    
    // 법적 정보
    private String termsOfService;
    private String privacyPolicy;
    private String licenseInfo;
    private String copyrightInfo;
    
    // 회사 정보
    private String companyName;
    private String businessRegistrationNumber;
    private String representativeName;
    private String address;
    private String contactEmail;
    private String contactPhone;
    private String supportHours;
    private String websiteUrl;
    
    // 추가 정보
    private Map<String, String> socialMediaLinks;
    private List<String> notices;
    private Map<String, Object> faqContent;
    private Set<String> supportedLanguages;
    private Map<String, String> termsOfServiceTranslations;
    
    public static AppInfoDto fromEntity(AppInfo appInfo) {
        return AppInfoDto.builder()
                .id(appInfo.getId())
                .appName(appInfo.getAppName())
                .appVersion(appInfo.getAppVersion())
                .lastUpdatedAt(appInfo.getLastUpdatedAt())
                .appDescription(appInfo.getAppDescription())
                .logoUrl(appInfo.getLogoUrl())
                .termsOfService(appInfo.getTermsOfService())
                .privacyPolicy(appInfo.getPrivacyPolicy())
                .licenseInfo(appInfo.getLicenseInfo())
                .copyrightInfo(appInfo.getCopyrightInfo())
                .companyName(appInfo.getCompanyName())
                .businessRegistrationNumber(appInfo.getBusinessRegistrationNumber())
                .representativeName(appInfo.getRepresentativeName())
                .address(appInfo.getAddress())
                .contactEmail(appInfo.getContactEmail())
                .contactPhone(appInfo.getContactPhone())
                .supportHours(appInfo.getSupportHours())
                .websiteUrl(appInfo.getWebsiteUrl())
                .socialMediaLinks(appInfo.getSocialMediaLinks())
                .notices(appInfo.getNotices())
                .faqContent(appInfo.getFaqContent())
                .supportedLanguages(appInfo.getSupportedLanguages())
                .termsOfServiceTranslations(appInfo.getTermsOfServiceTranslations())
                .build();
    }
} 