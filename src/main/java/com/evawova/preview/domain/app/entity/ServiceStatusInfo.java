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
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Convert;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import jakarta.persistence.EntityListeners;

import com.evawova.preview.domain.app.converter.JsonMapConverter;

@Entity
@Table(name = "service_status_info")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class ServiceStatusInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "app_info_id")
    private AppInfo appInfo;
    
    @Enumerated(EnumType.STRING)
    private ServiceStatus serviceStatus = ServiceStatus.NORMAL;
    
    @Column(columnDefinition = "TEXT")
    private String serviceStatusMessage;
    
    @Column(columnDefinition = "TEXT")
    private String emergencyNotice;
    
    private LocalDateTime emergencyNoticeStartAt;
    
    private LocalDateTime emergencyNoticeEndAt;
    
    private boolean emergencyNoticeActive;
    
    @Column(columnDefinition = "TEXT")
    private String maintenanceNotice;
    
    @ElementCollection
    @CollectionTable(name = "app_notices", joinColumns = @JoinColumn(name = "service_status_info_id"))
    @OrderColumn
    private List<String> notices = new ArrayList<>();
    
    @Column(columnDefinition = "json")
    @Convert(converter = JsonMapConverter.class)
    private Map<String, Object> faqContent = new HashMap<>();
    
    @ElementCollection
    @CollectionTable(name = "supported_languages", joinColumns = @JoinColumn(name = "service_status_info_id"))
    private Set<String> supportedLanguages = new HashSet<>();
    
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    @Builder
    public ServiceStatusInfo(Long id, AppInfo appInfo, ServiceStatus serviceStatus, String serviceStatusMessage,
            String emergencyNotice, LocalDateTime emergencyNoticeStartAt, LocalDateTime emergencyNoticeEndAt,
            boolean emergencyNoticeActive, String maintenanceNotice, List<String> notices, Map<String, Object> faqContent,
            Set<String> supportedLanguages, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.appInfo = appInfo;
        this.serviceStatus = serviceStatus;
        this.serviceStatusMessage = serviceStatusMessage;
        this.emergencyNotice = emergencyNotice;
        this.emergencyNoticeStartAt = emergencyNoticeStartAt;
        this.emergencyNoticeEndAt = emergencyNoticeEndAt;
        this.emergencyNoticeActive = emergencyNoticeActive;
        this.maintenanceNotice = maintenanceNotice;
        this.notices = notices;
        this.faqContent = faqContent;
        this.supportedLanguages = supportedLanguages;
    }
    
    // 서비스 상태 관리 메서드
    public void updateServiceStatus(ServiceStatus status, String message) {
        this.serviceStatus = status;
        this.serviceStatusMessage = message;
    }
    
    public void setEmergencyNotice(String notice, LocalDateTime startAt, LocalDateTime endAt) {
        this.emergencyNotice = notice;
        this.emergencyNoticeStartAt = startAt;
        this.emergencyNoticeEndAt = endAt;
        this.emergencyNoticeActive = true;
    }
    
    public void clearEmergencyNotice() {
        this.emergencyNotice = null;
        this.emergencyNoticeStartAt = null;
        this.emergencyNoticeEndAt = null;
        this.emergencyNoticeActive = false;
    }
    
    public void updateEmergencyNoticeStatus() {
        if (this.emergencyNoticeEndAt != null && LocalDateTime.now().isAfter(this.emergencyNoticeEndAt)) {
            this.emergencyNoticeActive = false;
        }
    }
    
    public void setFaqContent(Map<String, Object> faqContent) {
        this.faqContent = faqContent;
    }
    
    // 양방향 관계 설정
    public void setAppInfo(AppInfo appInfo) {
        this.appInfo = appInfo;
        if (appInfo != null) {
            appInfo.setServiceStatusInfo(this);
        }
    }
} 