package com.evawova.preview.domain.interview.entity;

import com.evawova.preview.domain.interview.model.JobRole;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Entity
@Table(name = "skills")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("기술 스택 ID")
    private Long id;

    @Column(nullable = false)
    @Comment("기술 스택 한글명")
    private String name;

    @Column(nullable = false)
    @Comment("기술 스택 영문명")
    private String nameEn;

    @Column
    @Comment("기술 스택 아이콘")
    private String icon;

    @Enumerated(EnumType.STRING)
    @Column
    @Comment("주요 관련 직무")
    private JobRole primaryJobRole;

    @Column
    @Comment("인기 기술 여부")
    private Boolean isPopular;

    @Column(nullable = false)
    @Comment("생성 시간")
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @Comment("수정 시간")
    private LocalDateTime updatedAt;

    // Ensure timestamps are set during creation via builder
    public static class SkillBuilder {
        private LocalDateTime createdAt = LocalDateTime.now();
        private LocalDateTime updatedAt = LocalDateTime.now();
        private Boolean isPopular = false; // 기본값 설정
    }
}