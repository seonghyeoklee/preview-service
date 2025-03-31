package com.evawova.preview.domain.app.dto;

import java.time.LocalDateTime;
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
    private LocalDateTime updatedAt;
    private String appDescription;
    private String logoUrl;
    private String apiVersionInfo;
    
    // 하위 DTO 객체들
    private LegalInfoDto legalInfo;
    private CompanyInfoDto companyInfo;
    private ServiceStatusInfoDto serviceStatusInfo;
    private DeploymentInfoDto deploymentInfo;
    
    public static AppInfoDto fromEntity(AppInfo appInfo) {
        if (appInfo == null) {
            return null;
        }
        
        AppInfoDto dto = AppInfoDto.builder()
                .id(appInfo.getId())
                .appName(appInfo.getAppName())
                .appVersion(appInfo.getAppVersion())
                .updatedAt(appInfo.getUpdatedAt())
                .appDescription(appInfo.getAppDescription())
                .logoUrl(appInfo.getLogoUrl())
                .apiVersionInfo(appInfo.getApiVersionInfo())
                .build();
        
        if (appInfo.getLegalInfo() != null) {
            dto.setLegalInfo(LegalInfoDto.fromEntity(appInfo.getLegalInfo()));
        }
        
        if (appInfo.getCompanyInfo() != null) {
            dto.setCompanyInfo(CompanyInfoDto.fromEntity(appInfo.getCompanyInfo()));
        }
        
        if (appInfo.getServiceStatusInfo() != null) {
            dto.setServiceStatusInfo(ServiceStatusInfoDto.fromEntity(appInfo.getServiceStatusInfo()));
        }
        
        if (appInfo.getDeploymentInfo() != null) {
            dto.setDeploymentInfo(DeploymentInfoDto.fromEntity(appInfo.getDeploymentInfo()));
        }
        
        return dto;
    }
} 