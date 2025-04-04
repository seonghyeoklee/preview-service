package com.evawova.preview.domain.interview.entity;

import com.evawova.preview.domain.interview.model.InterviewType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @Comment("상위 카테고리")
    private InterviewCategory parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @Comment("하위 카테고리 목록")
    private List<InterviewCategory> children = new ArrayList<>();

    @Column(nullable = false)
    @Comment("카테고리 계층 (1: 대분류, 2: 중분류, 3: 소분류)")
    @Builder.Default
    private Integer level = 1;

    @ManyToMany
    @JoinTable(name = "category_skills", joinColumns = @JoinColumn(name = "category_id"), inverseJoinColumns = @JoinColumn(name = "skill_id"))
    @Builder.Default
    @Comment("카테고리 관련 스킬 목록")
    private List<Skill> skills = new ArrayList<>();

    @Column(nullable = false)
    @Comment("생성 시간")
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @Comment("수정 시간")
    private LocalDateTime updatedAt;

    /**
     * 자식 카테고리 추가
     */
    public void addChild(InterviewCategory child) {
        this.children.add(child);
        child.setParent(this);
    }

    /**
     * 부모 카테고리 설정
     */
    protected void setParent(InterviewCategory parent) {
        this.parent = parent;
    }

    /**
     * 스킬 추가
     */
    public void addSkill(Skill skill) {
        this.skills.add(skill);
    }
}