package com.evawova.preview.domain.app.entity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Table;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import jakarta.persistence.EntityListeners;

@Entity
@Table(name = "legal_info")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class LegalInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "app_info_id")
    private AppInfo appInfo;
    
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
    
    @ElementCollection
    @CollectionTable(name = "terms_of_service_translations", joinColumns = @JoinColumn(name = "legal_info_id"))
    private Map<String, String> termsOfServiceTranslations = new HashMap<>();
    
    @Column(columnDefinition = "TEXT")
    private String refundPolicy;
    
    @Column(columnDefinition = "TEXT")
    private String subscriptionInfo;
    
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    @Builder
    public LegalInfo(Long id, AppInfo appInfo, String termsOfService, String privacyPolicy, 
                   String licenseInfo, String copyrightInfo, String cookiePolicy, String youthProtectionPolicy,
                   Map<String, String> termsOfServiceTranslations, String refundPolicy, String subscriptionInfo,
                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.appInfo = appInfo;
        this.termsOfService = termsOfService;
        this.privacyPolicy = privacyPolicy;
        this.licenseInfo = licenseInfo;
        this.copyrightInfo = copyrightInfo;
        this.cookiePolicy = cookiePolicy;
        this.youthProtectionPolicy = youthProtectionPolicy;
        this.termsOfServiceTranslations = termsOfServiceTranslations;
        this.refundPolicy = refundPolicy;
        this.subscriptionInfo = subscriptionInfo;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // 정보 업데이트 메서드
    public void updateLegalInfo(String termsOfService, String privacyPolicy, 
                              String licenseInfo, String copyrightInfo) {
        this.termsOfService = termsOfService;
        this.privacyPolicy = privacyPolicy;
        this.licenseInfo = licenseInfo;
        this.copyrightInfo = copyrightInfo;
    }
    
    public void updatePolicies(String cookiePolicy, String youthProtectionPolicy, 
                             String refundPolicy, String subscriptionInfo) {
        this.cookiePolicy = cookiePolicy;
        this.youthProtectionPolicy = youthProtectionPolicy;
        this.refundPolicy = refundPolicy;
        this.subscriptionInfo = subscriptionInfo;
    }
    
    // 양방향 관계 설정
    public void setAppInfo(AppInfo appInfo) {
        this.appInfo = appInfo;
        if (appInfo != null) {
            appInfo.setLegalInfo(this);
        }
    }
} 