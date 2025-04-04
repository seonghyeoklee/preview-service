package com.evawova.preview.domain.interview.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import com.evawova.preview.domain.interview.model.ExperienceLevel;
import com.evawova.preview.domain.interview.model.InterviewDifficulty;
import com.evawova.preview.domain.interview.model.InterviewDuration;
import com.evawova.preview.domain.interview.model.InterviewLanguage;
import com.evawova.preview.domain.interview.model.InterviewMode;
import com.evawova.preview.domain.interview.model.InterviewerStyle;
import com.evawova.preview.domain.interview.model.JobRole;
import com.evawova.preview.domain.interview.model.PromptCategory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "interview_prompts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class InterviewPrompt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("프롬프트 고유 식별자")
    private Long id;

    @Column(nullable = false)
    @Comment("프롬프트 이름")
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Comment("프롬프트 카테고리")
    private PromptCategory category;

    @Column(columnDefinition = "TEXT", nullable = false)
    @Comment("프롬프트 내용")
    private String content;

    @Column(nullable = false)
    @Comment("프롬프트 활성화 여부")
    private boolean active;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @Comment("상위 프롬프트")
    private InterviewPrompt parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @Comment("하위 프롬프트 목록")
    private List<InterviewPrompt> children = new ArrayList<>();

    @Column(nullable = false)
    @Comment("프롬프트 계층 (1: 대분류, 2: 중분류, 3: 소분류)")
    @Builder.Default
    private Integer level = 1;

    @Column(nullable = false, updatable = false)
    @Comment("생성일시")
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @Comment("수정일시")
    private LocalDateTime updatedAt;

    /**
     * 자식 프롬프트 추가
     */
    public void addChild(InterviewPrompt child) {
        this.children.add(child);
        child.setParent(this);
    }

    /**
     * 부모 프롬프트 설정
     */
    protected void setParent(InterviewPrompt parent) {
        this.parent = parent;
    }

    public void update(String name, String content, boolean active) {
        this.name = name;
        this.content = content;
        this.active = active;
        this.updatedAt = LocalDateTime.now();
    }

    public void deactivate() {
        this.active = false;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 프롬프트 내용에서 플레이스홀더를 실제 값으로 대체합니다.
     * 예: "{{면접관_스타일}}" -> "친근한 면접관"
     */
    public String getReplacedContent(InterviewerStyle interviewerStyle, JobRole jobRole,
            ExperienceLevel experienceLevel,
            InterviewDifficulty difficulty, InterviewDuration duration,
            InterviewMode mode, InterviewLanguage language) {
        String replaced = content;

        // 각 Enum에 따라 플레이스홀더 대체
        if (interviewerStyle != null) {
            replaced = replaced.replace("{{면접관_스타일}}", interviewerStyle.getDisplayName());
        }

        if (jobRole != null) {
            replaced = replaced.replace("{{직무}}", jobRole.getDisplayName());
        }

        if (experienceLevel != null) {
            replaced = replaced.replace("{{경력_수준}}", experienceLevel.getDisplayName());
        }

        if (difficulty != null) {
            replaced = replaced.replace("{{난이도}}", difficulty.getDisplayName());
        }

        if (duration != null) {
            replaced = replaced.replace("{{면접_시간}}", duration.getDisplayName());
        }

        if (mode != null) {
            replaced = replaced.replace("{{면접_모드}}", mode.getDisplayName());
        }

        if (language != null) {
            replaced = replaced.replace("{{언어}}", language.getDisplayName());
        }

        return replaced;
    }
}