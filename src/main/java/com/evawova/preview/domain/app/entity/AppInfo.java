package com.evawova.preview.domain.app.entity;

import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Table;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import jakarta.persistence.EntityListeners;

@Entity
@Table(name = "app_info")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class AppInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // 기본 앱 정보
    @Column(nullable = false)
    private String appName;
    
    @Column(nullable = false, name = "app_version")
    private String appVersion;
    
    @Column(length = 1000)
    private String appDescription;
    
    private String logoUrl;
    
    private String apiVersionInfo;
    
    // 연관 관계
    @OneToOne(mappedBy = "appInfo", cascade = CascadeType.ALL, orphanRemoval = true)
    private LegalInfo legalInfo;
    
    @OneToOne(mappedBy = "appInfo", cascade = CascadeType.ALL, orphanRemoval = true)
    private CompanyInfo companyInfo;
    
    @OneToOne(mappedBy = "appInfo", cascade = CascadeType.ALL, orphanRemoval = true)
    private ServiceStatusInfo serviceStatusInfo;
    
    @OneToOne(mappedBy = "appInfo", cascade = CascadeType.ALL, orphanRemoval = true)
    private DeploymentInfo deploymentInfo;
    
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    @Builder
    public AppInfo(Long id, String appName, String appVersion, String appDescription, String logoUrl) {
        this.id = id;
        this.appName = appName;
        this.appVersion = appVersion;
        this.appDescription = appDescription;
        this.logoUrl = logoUrl;
    }
    
    // 정보 업데이트 메서드
    public void updateAppInfo(String appName, String appVersion, String appDescription, String logoUrl) {
        this.appName = appName;
        this.appVersion = appVersion;
        this.appDescription = appDescription;
        this.logoUrl = logoUrl;
    }
    
    // 연관 관계 설정 메서드
    public void setLegalInfo(LegalInfo legalInfo) {
        this.legalInfo = legalInfo;
    }
    
    public void setCompanyInfo(CompanyInfo companyInfo) {
        this.companyInfo = companyInfo;
    }
    
    public void setServiceStatusInfo(ServiceStatusInfo serviceStatusInfo) {
        this.serviceStatusInfo = serviceStatusInfo;
    }
    
    public void setDeploymentInfo(DeploymentInfo deploymentInfo) {
        this.deploymentInfo = deploymentInfo;
    }
}