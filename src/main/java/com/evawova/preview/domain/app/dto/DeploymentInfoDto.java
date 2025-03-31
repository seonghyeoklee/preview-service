package com.evawova.preview.domain.app.dto;

import java.time.LocalDateTime;

import com.evawova.preview.domain.app.entity.DeploymentInfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeploymentInfoDto {
    private Long id;
    private String deploymentInfo;
    private String deploymentStatus;
    private LocalDateTime lastDeploymentAt;
    private String deploymentNotes;
    private String paymentMethods;
    private LocalDateTime updatedAt;
    
    public static DeploymentInfoDto fromEntity(DeploymentInfo entity) {
        if (entity == null) {
            return null;
        }
        
        return DeploymentInfoDto.builder()
                .id(entity.getId())
                .deploymentInfo(entity.getDeploymentInfo())
                .deploymentStatus(entity.getDeploymentStatus())
                .lastDeploymentAt(entity.getLastDeploymentAt())
                .deploymentNotes(entity.getDeploymentNotes())
                .paymentMethods(entity.getPaymentMethods())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
} 