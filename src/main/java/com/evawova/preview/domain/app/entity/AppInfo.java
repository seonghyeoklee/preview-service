package com.evawova.preview.domain.app.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import com.evawova.preview.domain.app.converter.JsonMapConverter;

@Entity
@Table(name = "app_info")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AppInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // 기본 앱 정보
    @Column(nullable = false)
    private String appName;
    
    @Column(nullable = false, name = "app_version")
    private String appVersion;
    
    @Column(nullable = false)
    private LocalDateTime lastUpdatedAt;
    
    @Column(length = 1000)
    private String appDescription;
    
    private String logoUrl;
    
    // 법적 정보
    @Column(columnDefinition = "TEXT")
    private String termsOfService;
    
    @Column(columnDefinition = "TEXT")
    private String privacyPolicy;
    
    @Column(columnDefinition = "TEXT")
    private String licenseInfo;
    
    private String copyrightInfo;
    
    @Column(columnDefinition = "TEXT")
    private String cookiePolicy;
    
    @Column(columnDefinition = "TEXT")
    private String youthProtectionPolicy;
    
    // 회사/운영자 정보
    private String companyName;
    private String businessRegistrationNumber;
    private String representativeName;
    private String address;
    
    @Column(nullable = false)
    private String contactEmail;
    
    private String contactPhone;
    private String supportHours;
    private String websiteUrl;
    
    @ElementCollection
    @CollectionTable(name = "social_media_links", joinColumns = @JoinColumn(name = "app_info_id"))
    private Map<String, String> socialMediaLinks = new HashMap<>();
    
    // 서비스 정보
    @ElementCollection
    @CollectionTable(name = "app_notices", joinColumns = @JoinColumn(name = "app_info_id"))
    @OrderColumn
    private List<String> notices = new ArrayList<>();
    
    @Column(columnDefinition = "json")
    @Convert(converter = JsonMapConverter.class)
    private Map<String, Object> faqContent = new HashMap<>();
    
    @ElementCollection
    @CollectionTable(name = "supported_languages", joinColumns = @JoinColumn(name = "app_info_id"))
    private Set<String> supportedLanguages = new HashSet<>();
    
    private String serviceStatusMessage;
    
    @Column(columnDefinition = "TEXT")
    private String maintenanceNotice;
    
    private String apiVersionInfo;
    
    // 배포 정보
    @Column(columnDefinition = "TEXT")
    private String deploymentInfo;
    
    private String deploymentStatus;
    
    private LocalDateTime lastDeploymentAt;
    
    @Column(columnDefinition = "TEXT")
    private String deploymentNotes;
    
    // 결제 및 환불 정보
    @Column(columnDefinition = "TEXT")
    private String paymentMethods;
    
    @Column(columnDefinition = "TEXT")
    private String refundPolicy;
    
    @Column(columnDefinition = "TEXT")
    private String subscriptionInfo;
    
    @ElementCollection
    @CollectionTable(name = "terms_of_service_translations")
    private Map<String, String> termsOfServiceTranslations = new HashMap<>();
    
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    @Builder
    public AppInfo(String appName, String appVersion, String appDescription, 
                String logoUrl, String termsOfService, String privacyPolicy,
                String companyName, String contactEmail) {
        this.appName = appName;
        this.appVersion = appVersion;
        this.lastUpdatedAt = LocalDateTime.now();
        this.appDescription = appDescription;
        this.logoUrl = logoUrl;
        this.termsOfService = termsOfService;
        this.privacyPolicy = privacyPolicy;
        this.companyName = companyName;
        this.contactEmail = contactEmail;
        this.deploymentStatus = "PENDING"; // 초기 상태는 PENDING
    }
    
    // 정보 업데이트 메서드들
    public void updateAppInfo(String appName, String appVersion, String appDescription, String logoUrl) {
        this.appName = appName;
        this.appVersion = appVersion;
        this.appDescription = appDescription;
        this.logoUrl = logoUrl;
        this.lastUpdatedAt = LocalDateTime.now();
    }
    
    public void updateLegalInfo(String termsOfService, String privacyPolicy, 
                              String licenseInfo, String copyrightInfo) {
        this.termsOfService = termsOfService;
        this.privacyPolicy = privacyPolicy;
        this.licenseInfo = licenseInfo;
        this.copyrightInfo = copyrightInfo;
        this.lastUpdatedAt = LocalDateTime.now();
    }
    
    public void updateCompanyInfo(String companyName, String businessRegistrationNumber,
                                String representativeName, String address, 
                                String contactEmail, String contactPhone) {
        this.companyName = companyName;
        this.businessRegistrationNumber = businessRegistrationNumber;
        this.representativeName = representativeName;
        this.address = address;
        this.contactEmail = contactEmail;
        this.contactPhone = contactPhone;
        this.lastUpdatedAt = LocalDateTime.now();
    }

    public void setFaqContent(Map<String, Object> faqContent) {
        this.faqContent = faqContent;
    }

    public void updateDeploymentInfo(String deploymentInfo, String deploymentStatus, 
                                   String deploymentNotes) {
        this.deploymentInfo = deploymentInfo;
        this.deploymentStatus = deploymentStatus;
        this.deploymentNotes = deploymentNotes;
        this.lastDeploymentAt = LocalDateTime.now();
        this.lastUpdatedAt = LocalDateTime.now();
    }
}