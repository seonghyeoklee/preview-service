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
    @Comment("���ͺ� ī�װ� ID")
    private Long id;

    @Column(nullable = false)
    @Comment("������ �̸� (Frontend �ĺ���)")
    private String icon;

    @Column(nullable = false)
    @Comment("ī�װ� ����")
    private String title;

    @Comment("ī�װ� ����")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Comment("���ͺ� Ÿ�� Enum")
    private InterviewType type;

    @Column(nullable = false)
    @Comment("���� �ð�")
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @Comment("���� �ð�")
    private LocalDateTime updatedAt;

    // Ensure timestamps are set during creation via builder
    public static class InterviewCategoryBuilder {
        private LocalDateTime createdAt = LocalDateTime.now();
        private LocalDateTime updatedAt = LocalDateTime.now();
    }
}