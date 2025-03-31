package com.evawova.preview.domain.app.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.evawova.preview.domain.app.entity.ServiceStatus;
import com.evawova.preview.domain.app.entity.ServiceStatusInfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceStatusInfoDto {
    private Long id;
    private ServiceStatus serviceStatus;
    private String serviceStatusMessage;
    private String emergencyNotice;
    private LocalDateTime emergencyNoticeStartAt;
    private LocalDateTime emergencyNoticeEndAt;
    private boolean emergencyNoticeActive;
    private String maintenanceNotice;
    private List<String> notices;
    private Map<String, Object> faqContent;
    private Set<String> supportedLanguages;
    private LocalDateTime updatedAt;
    
    public static ServiceStatusInfoDto fromEntity(ServiceStatusInfo entity) {
        if (entity == null) {
            return null;
        }
        
        return ServiceStatusInfoDto.builder()
                .id(entity.getId())
                .serviceStatus(entity.getServiceStatus())
                .serviceStatusMessage(entity.getServiceStatusMessage())
                .emergencyNotice(entity.getEmergencyNotice())
                .emergencyNoticeStartAt(entity.getEmergencyNoticeStartAt())
                .emergencyNoticeEndAt(entity.getEmergencyNoticeEndAt())
                .emergencyNoticeActive(entity.isEmergencyNoticeActive())
                .maintenanceNotice(entity.getMaintenanceNotice())
                .notices(entity.getNotices())
                .faqContent(entity.getFaqContent())
                .supportedLanguages(entity.getSupportedLanguages())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
} 