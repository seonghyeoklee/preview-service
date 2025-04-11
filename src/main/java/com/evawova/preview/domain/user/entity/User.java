package com.evawova.preview.domain.user.entity;

import com.evawova.preview.domain.common.model.AggregateRoot;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class User extends AggregateRoot<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("사용자 고유 식별자")
    private Long id;

    @Column(unique = true, nullable = false, name = "uid")
    @Comment("소셜 로그인 제공자의 사용자 고유 식별자")
    private String uid;

    @Column(unique = true, nullable = false, name = "email")
    @Comment("사용자 이메일")
    private String email;

    @Column(name = "password")
    @Comment("사용자 비밀번호 (소셜 로그인의 경우 임시 비밀번호)")
    private String password;

    @Column(nullable = false, name = "display_name")
    @Comment("사용자 표시 이름")
    private String displayName;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @Builder.Default
    @Comment("사용자의 구독 목록")
    private List<Subscription> subscriptions = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @Builder.Default
    @Comment("사용자의 로그인 기록 목록")
    private List<UserLoginLog> loginLogs = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "provider")
    @Comment("소셜 로그인 제공자 (GOOGLE, APPLE)")
    private Provider provider;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "role")
    @Comment("사용자 역할 (USER, ADMIN, ROLE_FREE, ROLE_STANDARD, ROLE_PRO)")
    @Builder.Default
    private Role role = Role.USER_FREE;

    @Column(nullable = false, name = "is_active")
    @Comment("계정 활성화 상태")
    private Boolean isActive;

    @Column(name = "photo_url")
    @Comment("프로필 사진 URL")
    private String photoUrl;

    @Column(name = "is_email_verified")
    @Comment("이메일 인증 여부")
    private Boolean isEmailVerified;

    @Column(name = "last_login_at")
    @Comment("마지막 로그인 시간")
    private LocalDateTime lastLoginAt;

    @CreatedDate
    @Column(nullable = false, updatable = false, name = "created_at")
    @Comment("계정 생성 시간")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false, name = "updated_at")
    @Comment("계정 정보 수정 시간")
    private LocalDateTime updatedAt;

    public enum Provider {
        GOOGLE, APPLE
    }

    public enum Role {
        ADMIN, USER_FREE, USER_STANDARD, USER_PRO
    }

    // 생성 메서드
    public static User createUser(String email, String password, String name) {
        return User.builder()
                .email(email)
                .password(password)
                .displayName(name)
                .role(Role.USER_FREE)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // 소셜 로그인용 생성자
    public static User createSocialUser(String uid, String email, String name, Provider provider) {
        return User.builder()
                .uid(uid)
                .email(email)
                .displayName(name)
                .provider(provider)
                .role(Role.USER_FREE)
                .isActive(true)
                .password("SOCIAL_USER_" + System.currentTimeMillis())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // 소셜 로그인 정보 업데이트
    public void updateSocialInfo(String name, Provider provider) {
        this.displayName = name;
        this.provider = provider;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateAdditionalInfo(String displayName, String photoUrl, boolean isEmailVerified,
            LocalDateTime lastLoginAt) {
        this.displayName = displayName;
        this.photoUrl = photoUrl;
        this.isEmailVerified = isEmailVerified;
        this.lastLoginAt = lastLoginAt;
        this.updatedAt = LocalDateTime.now();
    }

    public void withdraw() {
        this.isActive = false;
        this.email = "withdrawn_" + System.currentTimeMillis() + "@withdrawn.com";
        this.displayName = "탈퇴한 사용자";
        this.password = null;
        this.provider = null;
        this.photoUrl = null;
        this.isEmailVerified = false;
        this.lastLoginAt = null;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Subscription getActiveSubscription() {
        return subscriptions.stream()
                .filter(Subscription::isActive)
                .findFirst()
                .orElse(null);
    }

    public Plan getCurrentPlan() {
        Subscription activeSubscription = getActiveSubscription();
        return activeSubscription != null ? activeSubscription.getPlan() : null;
    }

    @Override
    public Long getId() {
        return this.id;
    }
}