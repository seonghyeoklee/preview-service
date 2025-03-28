package com.evawova.preview.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_login_logs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class UserLoginLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("로그인 기록 고유 식별자")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @Comment("로그인한 사용자")
    private User user;

    @Column(nullable = false)
    @Comment("로그인 IP 주소")
    private String ipAddress;

    @Column(nullable = false)
    @Comment("사용자 에이전트 문자열")
    private String userAgent;

    @Column(nullable = false)
    @Comment("디바이스 타입 (DESKTOP, MOBILE, TABLET)")
    private String deviceType;

    @Column(nullable = false)
    @Comment("브라우저 정보")
    private String browserInfo;

    @Column(nullable = false)
    @Comment("운영체제 정보")
    private String osInfo;

    @Column(nullable = false)
    @Comment("로그인 성공 여부")
    private boolean successful;

    @Comment("로그인 실패 사유")
    private String failReason;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    @Comment("로그인 시도 시간")
    private LocalDateTime loginAt;

    @LastModifiedDate
    @Column(nullable = false)
    @Comment("기록 수정 시간")
    private LocalDateTime updatedAt;

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
        this.successful = successful;
        this.failReason = failReason;
        this.loginAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static UserLoginLog createLog(User user, String ipAddress, String userAgent, String deviceType, String browserInfo, String osInfo) {
        UserLoginLog log = new UserLoginLog();
        log.user = user;
        log.ipAddress = ipAddress;
        log.userAgent = userAgent;
        log.deviceType = deviceType;
        log.browserInfo = browserInfo;
        log.osInfo = osInfo;
        log.successful = true;
        log.loginAt = LocalDateTime.now();
        log.updatedAt = LocalDateTime.now();
        return log;
    }

    public static UserLoginLog createFailedLog(User user, String ipAddress, String userAgent, String deviceType, String browserInfo, String osInfo, String failReason) {
        UserLoginLog log = new UserLoginLog();
        log.user = user;
        log.ipAddress = ipAddress;
        log.userAgent = userAgent;
        log.deviceType = deviceType;
        log.browserInfo = browserInfo;
        log.osInfo = osInfo;
        log.successful = false;
        log.failReason = failReason;
        log.loginAt = LocalDateTime.now();
        log.updatedAt = LocalDateTime.now();
        return log;
    }
} 