package com.evawova.preview.domain.interview.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "interview_sessions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class InterviewSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "settings_id")
    @Comment("인터뷰 설정")
    private InterviewSettings settings;

    @Column(nullable = false)
    @Comment("세션 상태")
    @Enumerated(EnumType.STRING)
    private InterviewSessionStatus status;

    @Column(nullable = false)
    @Comment("시작 시간")
    private LocalDateTime startedAt;

    @Column
    @Comment("종료 시간")
    private LocalDateTime endedAt;

    @Column(nullable = false)
    @Comment("총 대화 수")
    private int totalMessages;

    @Column(nullable = false)
    @Comment("총 토큰 수")
    private int totalTokens;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<InterviewMessage> messages = new ArrayList<>();

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    @Comment("생성 시간")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    @Comment("수정 시간")
    private LocalDateTime updatedAt;

    public enum InterviewSessionStatus {
        IN_PROGRESS, // 진행 중
        COMPLETED, // 완료
        CANCELLED // 취소
    }

    public void setSettings(InterviewSettings settings) {
        this.settings = settings;
        settings.setSession(this);
    }

    public void addMessage(InterviewMessage message) {
        this.messages.add(message);
        message.setSession(this);
        this.totalMessages++;
    }

    public void endSession() {
        this.status = InterviewSessionStatus.COMPLETED;
        this.endedAt = LocalDateTime.now();
    }

    public void cancelSession() {
        this.status = InterviewSessionStatus.CANCELLED;
        this.endedAt = LocalDateTime.now();
    }
}