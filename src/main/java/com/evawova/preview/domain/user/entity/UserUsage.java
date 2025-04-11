package com.evawova.preview.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.time.LocalDate;

@Entity
@Table(name = "user_usages")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserUsage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("사용량 기록 고유 식별자")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @Comment("사용자")
    private User user;

    @Column(nullable = false, name = "usage_date")
    @Comment("사용량 집계 날짜")
    private LocalDate usageDate;

    @Column(nullable = false, name = "token_usage")
    @Comment("토큰 사용량")
    private Integer tokenUsage;

    @CreatedDate
    @Column(nullable = false, updatable = false, name = "created_at")
    @Comment("기록 생성 시간")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false, name = "updated_at")
    @Comment("기록 수정 시간")
    private LocalDateTime updatedAt;

    @Column(nullable = false, name = "usage_type")
    @Comment("사용 유형 (예: CHAT, ANALYSIS 등)")
    private String usageType;

    @Column(name = "description")
    @Comment("사용 상세 설명")
    private String description;

    @Builder
    public UserUsage(Long id, User user, Integer tokenUsage, String usageType, String description) {
        this.id = id;
        this.user = user;
        this.usageDate = LocalDateTime.now().toLocalDate();
        this.tokenUsage = tokenUsage;
        this.usageType = usageType;
        this.description = description;
    }
}