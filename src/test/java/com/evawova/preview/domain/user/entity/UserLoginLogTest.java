package com.evawova.preview.domain.user.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserLoginLogTest {

    private User user;
    private Plan freePlan;

    @BeforeEach
    void setUp() {
        freePlan = Plan.createPlan(
            "Free",
            PlanType.FREE,
            0,
            0,
            10000,
            true
        );
        
        user = User.createSocialUser(
            "123456789",
            "test@example.com",
            "Test User",
            User.Provider.GOOGLE,
            freePlan
        );
    }

    @Test
    @DisplayName("성공적인 로그인 기록을 생성할 수 있다")
    void createLog() {
        // given
        String ipAddress = "127.0.0.1";
        String userAgent = "Mozilla/5.0";
        String deviceType = "DESKTOP";
        String browserInfo = "Chrome";
        String osInfo = "Windows";

        // when
        UserLoginLog log = UserLoginLog.createLog(user, ipAddress, userAgent, deviceType, browserInfo, osInfo);

        // then
        assertThat(log.getUser()).isEqualTo(user);
        assertThat(log.getIpAddress()).isEqualTo(ipAddress);
        assertThat(log.getUserAgent()).isEqualTo(userAgent);
        assertThat(log.getDeviceType()).isEqualTo(deviceType);
        assertThat(log.getBrowserInfo()).isEqualTo(browserInfo);
        assertThat(log.getOsInfo()).isEqualTo(osInfo);
        assertThat(log.isSuccessful()).isTrue();
        assertThat(log.getFailReason()).isNull();
    }

    @Test
    @DisplayName("실패한 로그인 기록을 생성할 수 있다")
    void createFailedLog() {
        // given
        String ipAddress = "127.0.0.1";
        String userAgent = "Mozilla/5.0";
        String deviceType = "DESKTOP";
        String browserInfo = "Chrome";
        String osInfo = "Windows";
        String failReason = "Invalid credentials";

        // when
        UserLoginLog log = UserLoginLog.createFailedLog(user, ipAddress, userAgent, deviceType, browserInfo, osInfo, failReason);

        // then
        assertThat(log.getUser()).isEqualTo(user);
        assertThat(log.getIpAddress()).isEqualTo(ipAddress);
        assertThat(log.getUserAgent()).isEqualTo(userAgent);
        assertThat(log.getDeviceType()).isEqualTo(deviceType);
        assertThat(log.getBrowserInfo()).isEqualTo(browserInfo);
        assertThat(log.getOsInfo()).isEqualTo(osInfo);
        assertThat(log.isSuccessful()).isFalse();
        assertThat(log.getFailReason()).isEqualTo(failReason);
    }
} 