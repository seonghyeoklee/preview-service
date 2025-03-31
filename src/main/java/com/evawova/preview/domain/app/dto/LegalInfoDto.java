package com.evawova.preview.domain.app.dto;

import java.time.LocalDateTime;
import java.util.Map;

import com.evawova.preview.domain.app.entity.LegalInfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LegalInfoDto {
    private Long id;
    private String termsOfService;
    private String privacyPolicy;
    private String licenseInfo;
    private String copyrightInfo;
    private String cookiePolicy;
    private String youthProtectionPolicy;
    private Map<String, String> termsOfServiceTranslations;
    private String refundPolicy;
    private String subscriptionInfo;
    private LocalDateTime updatedAt;
    
    public static LegalInfoDto fromEntity(LegalInfo entity) {
        if (entity == null) {
            return null;
        }
        
        return LegalInfoDto.builder()
                .id(entity.getId())
                .termsOfService(entity.getTermsOfService())
                .privacyPolicy(entity.getPrivacyPolicy())
                .licenseInfo(entity.getLicenseInfo())
                .copyrightInfo(entity.getCopyrightInfo())
                .cookiePolicy(entity.getCookiePolicy())
                .youthProtectionPolicy(entity.getYouthProtectionPolicy())
                .termsOfServiceTranslations(entity.getTermsOfServiceTranslations())
                .refundPolicy(entity.getRefundPolicy())
                .subscriptionInfo(entity.getSubscriptionInfo())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
} 