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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import jakarta.persistence.EntityListeners;

@Entity
@Table(name = "deployment_info")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class DeploymentInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "app_info_id")
    private AppInfo appInfo;
    
    @Column(columnDefinition = "TEXT")
    private String deploymentInfo;
    
    private String deploymentStatus;
    
    private LocalDateTime lastDeploymentAt;
    
    @Column(columnDefinition = "TEXT")
    private String deploymentNotes;
    
    @Column(columnDefinition = "TEXT")
    private String paymentMethods;
    
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    @Builder
    public DeploymentInfo(Long id, AppInfo appInfo, String deploymentInfo, String deploymentStatus,
            LocalDateTime lastDeploymentAt, String deploymentNotes, String paymentMethods, LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        this.id = id;
        this.appInfo = appInfo;
        this.deploymentInfo = deploymentInfo;
        this.deploymentStatus = deploymentStatus;
        this.lastDeploymentAt = lastDeploymentAt;
        this.deploymentNotes = deploymentNotes;
        this.paymentMethods = paymentMethods;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // 배포 정보 업데이트 메서드
    public void updateDeploymentInfo(String deploymentInfo, String deploymentStatus, String deploymentNotes) {
        this.deploymentInfo = deploymentInfo;
        this.deploymentStatus = deploymentStatus;
        this.deploymentNotes = deploymentNotes;
        this.lastDeploymentAt = LocalDateTime.now();
    }
    
    // 결제 정보 업데이트 메서드
    public void updatePaymentMethods(String paymentMethods) {
        this.paymentMethods = paymentMethods;
    }
    
    // 양방향 관계 설정
    public void setAppInfo(AppInfo appInfo) {
        this.appInfo = appInfo;
        if (appInfo != null) {
            appInfo.setDeploymentInfo(this);
        }
    }
} 