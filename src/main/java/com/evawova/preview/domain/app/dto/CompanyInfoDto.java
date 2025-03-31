package com.evawova.preview.domain.app.dto;

import java.time.LocalDateTime;
import java.util.Map;

import com.evawova.preview.domain.app.entity.CompanyInfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyInfoDto {
    private Long id;
    private String companyName;
    private String businessRegistrationNumber;
    private String representativeName;
    private String address;
    private String contactEmail;
    private String contactPhone;
    private String supportHours;
    private String websiteUrl;
    private Map<String, String> socialMediaLinks;
    private LocalDateTime updatedAt;
    
    public static CompanyInfoDto fromEntity(CompanyInfo entity) {
        if (entity == null) {
            return null;
        }
        
        return CompanyInfoDto.builder()
                .id(entity.getId())
                .companyName(entity.getCompanyName())
                .businessRegistrationNumber(entity.getBusinessRegistrationNumber())
                .representativeName(entity.getRepresentativeName())
                .address(entity.getAddress())
                .contactEmail(entity.getContactEmail())
                .contactPhone(entity.getContactPhone())
                .supportHours(entity.getSupportHours())
                .websiteUrl(entity.getWebsiteUrl())
                .socialMediaLinks(entity.getSocialMediaLinks())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
} 