package com.evawova.preview.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_login_logs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserLoginLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String ipAddress;

    @Column(nullable = false)
    private String userAgent;

    @Column
    private String deviceType;

    @Column
    private String browserInfo;

    @Column
    private String osInfo;

    @Column(nullable = false)
    private boolean successful;

    @Column
    private String failReason;

    @Column(nullable = false)
    private LocalDateTime loginAt;

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
    }
} 