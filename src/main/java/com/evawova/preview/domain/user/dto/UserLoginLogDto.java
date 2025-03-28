package com.evawova.preview.domain.user.dto;

import com.evawova.preview.domain.user.entity.UserLoginLog;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserLoginLogDto {
    private Long id;
    private Long userId;
    private String ipAddress;
    private String userAgent;
    private String deviceType;
    private String browserInfo;
    private String osInfo;
    private boolean successful;
    private String failReason;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime loginAt;

    public static UserLoginLogDto fromEntity(UserLoginLog entity) {
        return UserLoginLogDto.builder()
                .id(entity.getId())
                .userId(entity.getUser().getId())
                .ipAddress(entity.getIpAddress())
                .userAgent(entity.getUserAgent())
                .deviceType(entity.getDeviceType())
                .browserInfo(entity.getBrowserInfo())
                .osInfo(entity.getOsInfo())
                .successful(entity.isSuccessful())
                .failReason(entity.getFailReason())
                .loginAt(entity.getLoginAt())
                .build();
    }
} 