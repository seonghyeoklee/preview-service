package com.evawova.preview.domain.interview.entity;

import com.evawova.preview.domain.interview.entity.enums.*;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "interview_settings")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class InterviewSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "settings")
    @Comment("인터뷰 세션")
    private InterviewSession session;

    @Column(nullable = false)
    @Comment("인터뷰 타입")
    @Enumerated(EnumType.STRING)
    private InterviewType type;

    @Column(nullable = false)
    @Comment("직무 역할")
    @Enumerated(EnumType.STRING)
    private JobRole jobRole;

    @Column(nullable = false)
    @Comment("면접관 스타일")
    @Enumerated(EnumType.STRING)
    private InterviewerStyle interviewerStyle;

    @Column(nullable = false)
    @Comment("인터뷰 난이도")
    @Enumerated(EnumType.STRING)
    private InterviewDifficulty difficulty;

    @Column(nullable = false)
    @Comment("인터뷰 시간")
    @Enumerated(EnumType.STRING)
    private InterviewDuration duration;

    @Column(nullable = false)
    @Comment("인터뷰 모드")
    @Enumerated(EnumType.STRING)
    private InterviewMode interviewMode;

    @Column(nullable = false)
    @Comment("경력 수준")
    @Enumerated(EnumType.STRING)
    private ExperienceLevel experienceLevel;

    @ElementCollection
    @CollectionTable(name = "interview_technical_skills", joinColumns = @JoinColumn(name = "interview_settings_id"))
    @Column(name = "skill_name")
    @Comment("기술 스택")
    @Builder.Default
    private List<String> technicalSkills = new ArrayList<>();

    @Column(nullable = false)
    @Comment("인터뷰 언어")
    @Enumerated(EnumType.STRING)
    private InterviewLanguage language;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    @Comment("생성 시간")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    @Comment("수정 시간")
    private LocalDateTime updatedAt;

    public void setSession(InterviewSession session) {
        this.session = session;
    }
}