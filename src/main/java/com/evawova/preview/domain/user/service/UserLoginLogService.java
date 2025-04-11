package com.evawova.preview.domain.user.service;

import com.evawova.preview.domain.user.entity.User;
import com.evawova.preview.domain.user.entity.UserLoginLog;
import com.evawova.preview.domain.user.repository.UserLoginLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserLoginLogService {

    private final UserLoginLogRepository loginLogRepository;

    @Transactional
    public UserLoginLog logSuccessfulLogin(User user, HttpServletRequest request) {
        UserLoginLog loginLog = buildLoginLog(user, request, true, null);
        return loginLogRepository.save(loginLog);
    }

    @Transactional
    public UserLoginLog logFailedLogin(User user, HttpServletRequest request, String failReason) {
        UserLoginLog loginLog = buildLoginLog(user, request, false, failReason);
        return loginLogRepository.save(loginLog);
    }

    private UserLoginLog buildLoginLog(User user, HttpServletRequest request, boolean successful, String failReason) {
        String ipAddress = getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");

        // 디바이스, 브라우저, OS 정보 추출
        DeviceInfo deviceInfo = parseUserAgent(userAgent);

        return UserLoginLog.builder()
                .user(user)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .deviceType(deviceInfo.deviceType)
                .browserInfo(deviceInfo.browserInfo)
                .osInfo(deviceInfo.osInfo)
                .successful(successful)
                .failReason(failReason)
                .build();
    }

    public List<UserLoginLog> getLoginHistory(User user) {
        return loginLogRepository.findByUserOrderByLoginAtDesc(user);
    }

    public List<UserLoginLog> getLoginHistoryByDateRange(User user, LocalDateTime startDateTime,
            LocalDateTime endDateTime) {
        return loginLogRepository.findByUserAndLoginAtBetweenOrderByLoginAtDesc(user, startDateTime, endDateTime);
    }

    // 클라이언트 IP 주소 추출
    private String getClientIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (!StringUtils.hasLength(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (!StringUtils.hasLength(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (!StringUtils.hasLength(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_CLIENT_IP");
        }
        if (!StringUtils.hasLength(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (!StringUtils.hasLength(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }

    // User-Agent 파싱을 위한 간단한 구현 (실제로는 더 정교한 라이브러리 사용 권장)
    private DeviceInfo parseUserAgent(String userAgent) {
        DeviceInfo info = new DeviceInfo();

        if (userAgent == null) {
            return info;
        }

        userAgent = userAgent.toLowerCase();

        // 디바이스 타입 판별
        if (userAgent.contains("mobile") || userAgent.contains("android") || userAgent.contains("iphone")) {
            info.deviceType = "Mobile";
        } else if (userAgent.contains("tablet") || userAgent.contains("ipad")) {
            info.deviceType = "Tablet";
        } else {
            info.deviceType = "Desktop";
        }

        // 브라우저 정보
        if (userAgent.contains("firefox")) {
            info.browserInfo = "Firefox";
        } else if (userAgent.contains("edge") || userAgent.contains("edg")) {
            info.browserInfo = "Edge";
        } else if (userAgent.contains("chrome") && !userAgent.contains("edg")) {
            info.browserInfo = "Chrome";
        } else if (userAgent.contains("safari") && !userAgent.contains("chrome")) {
            info.browserInfo = "Safari";
        } else if (userAgent.contains("opera") || userAgent.contains("opr")) {
            info.browserInfo = "Opera";
        } else {
            info.browserInfo = "Other";
        }

        // OS 정보
        if (userAgent.contains("windows")) {
            info.osInfo = "Windows";
        } else if (userAgent.contains("mac os")) {
            info.osInfo = "macOS";
        } else if (userAgent.contains("linux")) {
            info.osInfo = "Linux";
        } else if (userAgent.contains("android")) {
            info.osInfo = "Android";
        } else if (userAgent.contains("iphone") || userAgent.contains("ipad") || userAgent.contains("ipod")) {
            info.osInfo = "iOS";
        } else {
            info.osInfo = "Other";
        }

        return info;
    }

    private static class DeviceInfo {
        String deviceType = "Unknown";
        String browserInfo = "Unknown";
        String osInfo = "Unknown";
    }
}