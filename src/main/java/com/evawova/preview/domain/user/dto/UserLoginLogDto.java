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
    private String userEmail;
    private String ipAddress;
    private String deviceType;
    private String browserInfo;
    private String osInfo;
    private boolean successful;
    private String failReason;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime loginAt;

    public static UserLoginLogDto fromEntity(UserLoginLog loginLog) {
        return UserLoginLogDto.builder()
                .id(loginLog.getId())
                .userId(loginLog.getUser() != null ? loginLog.getUser().getId() : null)
                .userEmail(loginLog.getUser() != null ? loginLog.getUser().getEmail() : null)
                .ipAddress(loginLog.getIpAddress())
                .deviceType(loginLog.getDeviceType())
                .browserInfo(loginLog.getBrowserInfo())
                .osInfo(loginLog.getOsInfo())
                .successful(loginLog.isSuccessful())
                .failReason(loginLog.getFailReason())
                .loginAt(loginLog.getLoginAt())
                .build();
    }
} 