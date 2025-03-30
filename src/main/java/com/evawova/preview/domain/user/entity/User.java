package com.evawova.preview.domain.user.entity;

import com.evawova.preview.domain.common.model.AggregateRoot;
import com.evawova.preview.domain.user.event.UserCreatedEvent;
import com.evawova.preview.domain.user.event.UserPlanChangedEvent;
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

    @Column(unique = true, nullable = false)
    @Comment("소셜 로그인 제공자의 사용자 고유 식별자")
    private String uid;

    @Column(unique = true, nullable = false)
    @Comment("사용자 이메일")
    private String email;

    @Comment("사용자 비밀번호 (소셜 로그인의 경우 임시 비밀번호)")
    private String password;

    @Column(nullable = false)
    @Comment("사용자 표시 이름")
    private String displayName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    @Comment("사용자의 구독 플랜")
    private Plan plan;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @Builder.Default
    @Comment("사용자의 로그인 기록 목록")
    private List<UserLoginLog> loginLogs = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Comment("소셜 로그인 제공자 (GOOGLE, APPLE)")
    private Provider provider;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Comment("사용자 역할 (USER, ADMIN)")
    @Builder.Default
    private Role role = Role.USER;

    @Column(nullable = false)
    @Comment("계정 활성화 상태")
    private boolean active = true;

    @Comment("프로필 사진 URL")
    private String photoUrl;

    @Comment("이메일 인증 여부")
    private boolean isEmailVerified;

    @Comment("마지막 로그인 시간")
    private LocalDateTime lastLoginAt;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    @Comment("계정 생성 시간")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    @Comment("계정 정보 수정 시간")
    private LocalDateTime updatedAt;

    public enum Provider {
        GOOGLE, APPLE
    }

    public enum Role {
        USER, ADMIN
    }

    // 생성 메서드
    public static User createUser(String email, String password, String name, Plan plan) {
        User user = User.builder()
                .email(email)
                .password(password)
                .displayName(name)
                .plan(plan)
                .role(Role.USER)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        // 도메인 이벤트 등록
        user.registerEvent(new UserCreatedEvent(user.id, user.email, user.displayName, plan.getType()));
        
        return user;
    }

    // 플랜 변경 메서드
    public void changePlan(Plan newPlan) {
        Plan oldPlan = this.plan;
        this.plan = newPlan;
        this.updatedAt = LocalDateTime.now();
        
        // 도메인 이벤트 등록
        this.registerEvent(new UserPlanChangedEvent(this.id, this.email, oldPlan.getType(), newPlan.getType()));
    }

    // 소셜 로그인용 생성자
    public static User createSocialUser(String uid, String email, String name, Provider provider, Plan plan) {
        User user = User.builder()
                .uid(uid)
                .email(email)
                .displayName(name)
                .provider(provider)
                .plan(plan)
                .role(Role.USER)
                .active(true)
                .password("SOCIAL_USER_" + System.currentTimeMillis())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        user.registerEvent(new UserCreatedEvent(user.getId(), user.getEmail(), user.getDisplayName(), user.getPlan().getType()));
        return user;
    }

    // 소셜 로그인 정보 업데이트
    public void updateSocialInfo(String name, Provider provider) {
        this.displayName = name;
        this.provider = provider;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateAdditionalInfo(String displayName, String photoUrl, boolean isEmailVerified, LocalDateTime lastLoginAt) {
        this.displayName = displayName;
        this.photoUrl = photoUrl;
        this.isEmailVerified = isEmailVerified;
        this.lastLoginAt = lastLoginAt;
        this.updatedAt = LocalDateTime.now();
    }

    public void withdraw() {
        this.active = false;
        this.email = "withdrawn_" + System.currentTimeMillis() + "@withdrawn.com";
        this.displayName = "탈퇴한 사용자";
        this.password = null;
        this.provider = null;
        this.photoUrl = null;
        this.isEmailVerified = false;
        this.lastLoginAt = null;
    }

    public void setPlan(Plan plan) {
        this.plan = plan;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public Long getId() {
        return this.id;
    }
} 