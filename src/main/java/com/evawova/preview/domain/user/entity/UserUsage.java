package com.evawova.preview.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_usages")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserUsage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    @Comment("사용 일자")
    private LocalDateTime usageDate;

    @Column(nullable = false)
    @Comment("사용한 토큰 수")
    private Integer tokenUsage;

    @Column(nullable = false)
    @Comment("사용 유형 (예: CHAT, ANALYSIS 등)")
    private String usageType;

    @Column
    @Comment("사용 상세 설명")
    private String description;

    @Builder
    public UserUsage(Long id, User user, Integer tokenUsage, String usageType, String description) {
        this.id = id;
        this.user = user;
        this.usageDate = LocalDateTime.now();
        this.tokenUsage = tokenUsage;
        this.usageType = usageType;
        this.description = description;
    }
} 