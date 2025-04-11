package com.evawova.preview.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_login_logs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class UserLoginLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("로그인 기록 고유 식별자")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @Comment("로그인한 사용자")
    private User user;

    @Column(nullable = false, name = "ip_address")
    @Comment("로그인 IP 주소")
    private String ipAddress;

    @Column(name = "user_agent")
    @Comment("사용자 에이전트 문자열")
    private String userAgent;

    @Column(nullable = false, name = "device_type")
    @Comment("디바이스 타입 (DESKTOP, MOBILE, TABLET)")
    private String deviceType;

    @Column(nullable = false, name = "browser_info")
    @Comment("브라우저 정보")
    private String browserInfo;

    @Column(nullable = false, name = "os_info")
    @Comment("운영체제 정보")
    private String osInfo;

    @Column(nullable = false, name = "login_at")
    @Comment("로그인 시간")
    private LocalDateTime loginAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "login_status")
    @Comment("로그인 상태 (SUCCESS, FAILURE)")
    private LoginStatus loginStatus;

    @Column(name = "failure_reason")
    @Comment("로그인 실패 사유")
    private String failureReason;

    @CreatedDate
    @Column(nullable = false, updatable = false, name = "created_at")
    @Comment("기록 생성 시간")
    private LocalDateTime createdAt;

    public enum LoginStatus {
        SUCCESS, FAILURE
    }

    @Builder
    public UserLoginLog(User user, String ipAddress, String userAgent,
            String deviceType, String browserInfo, String osInfo,
            boolean successful, String failReason) {
        this.user = user;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.deviceType = deviceType;
        this.browserInfo = browserInfo;
        this.osInfo = osInfo;
        this.loginAt = LocalDateTime.now();
        this.loginStatus = successful ? LoginStatus.SUCCESS : LoginStatus.FAILURE;
        this.failureReason = failReason;
        this.createdAt = LocalDateTime.now();
    }

    public static UserLoginLog createLog(User user, String ipAddress, String userAgent, String deviceType,
            String browserInfo, String osInfo) {
        return UserLoginLog.builder()
                .user(user)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .deviceType(deviceType)
                .browserInfo(browserInfo)
                .osInfo(osInfo)
                .successful(true)
                .loginAt(LocalDateTime.now())
                .build();
    }

    public static UserLoginLog createFailedLog(User user, String ipAddress, String userAgent, String deviceType,
            String browserInfo, String osInfo, String failReason) {
        return UserLoginLog.builder()
                .user(user)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .deviceType(deviceType)
                .browserInfo(browserInfo)
                .osInfo(osInfo)
                .successful(false)
                .failReason(failReason)
                .loginAt(LocalDateTime.now())
                .build();
    }
}