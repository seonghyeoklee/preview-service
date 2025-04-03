package com.evawova.preview.domain.interview.entity;

import com.evawova.preview.domain.interview.model.InterviewType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Entity
@Table(name = "interview_categories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE) // Ensure builder is the primary way to construct
@Builder
public class InterviewCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("인터뷰 카테고리 ID")
    private Long id;

    @Column(nullable = false)
    @Comment("아이콘 이름 (Frontend 식별자)")
    private String icon;

    @Column(nullable = false)
    @Comment("카테고리 제목 (한글)")
    private String title;

    @Column
    @Comment("카테고리 제목 (영문)")
    private String titleEn;

    @Comment("카테고리 설명 (한글)")
    private String description;

    @Comment("카테고리 설명 (영문)")
    private String descriptionEn;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Comment("인터뷰 타입 Enum")
    private InterviewType type;

    @Column(nullable = false)
    @Comment("생성 시간")
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @Comment("수정 시간")
    private LocalDateTime updatedAt;
}