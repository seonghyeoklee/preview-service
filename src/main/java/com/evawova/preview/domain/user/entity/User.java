package com.evawova.preview.domain.user.entity;

import com.evawova.preview.domain.common.model.AggregateRoot;
import com.evawova.preview.domain.user.event.UserCreatedEvent;
import com.evawova.preview.domain.user.event.UserPlanChangedEvent;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends AggregateRoot<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    private Plan plan;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // 생성 메서드
    public static User createUser(String email, String password, String name, Plan plan) {
        User user = new User();
        user.email = email;
        user.password = password;
        user.name = name;
        user.plan = plan;
        
        // 도메인 이벤트 등록
        user.registerEvent(new UserCreatedEvent(user.id, user.email, user.name, plan.getType()));
        
        return user;
    }

    // 플랜 변경 메서드
    public void changePlan(Plan newPlan) {
        Plan oldPlan = this.plan;
        this.plan = newPlan;
        
        // 도메인 이벤트 등록
        this.registerEvent(new UserPlanChangedEvent(this.id, this.email, oldPlan.getType(), newPlan.getType()));
    }

    @Override
    public Long getId() {
        return this.id;
    }
} 