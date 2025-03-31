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
@Table(name = "company_info")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class CompanyInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "app_info_id")
    private AppInfo appInfo;
    
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
    @CollectionTable(name = "social_media_links", joinColumns = @JoinColumn(name = "company_info_id"))
    private Map<String, String> socialMediaLinks = new HashMap<>();
    
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    @Builder
    public CompanyInfo(Long id, AppInfo appInfo, String companyName, String businessRegistrationNumber,
            String representativeName, String address, String contactEmail, String contactPhone, String supportHours,
            String websiteUrl, Map<String, String> socialMediaLinks, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.appInfo = appInfo;
        this.companyName = companyName;
        this.businessRegistrationNumber = businessRegistrationNumber;
        this.representativeName = representativeName;
        this.address = address;
        this.contactEmail = contactEmail;
        this.contactPhone = contactPhone;
    }
    
    // 정보 업데이트 메서드
    public void updateCompanyInfo(String companyName, String businessRegistrationNumber,
                                String representativeName, String address,
                                String contactEmail, String contactPhone) {
        this.companyName = companyName;
        this.businessRegistrationNumber = businessRegistrationNumber;
        this.representativeName = representativeName;
        this.address = address;
        this.contactEmail = contactEmail;
        this.contactPhone = contactPhone;
    }
    
    public void updateContactInfo(String supportHours, String websiteUrl) {
        this.supportHours = supportHours;
        this.websiteUrl = websiteUrl;
    }
    
    // 양방향 관계 설정
    public void setAppInfo(AppInfo appInfo) {
        this.appInfo = appInfo;
        if (appInfo != null) {
            appInfo.setCompanyInfo(this);
        }
    }
} 