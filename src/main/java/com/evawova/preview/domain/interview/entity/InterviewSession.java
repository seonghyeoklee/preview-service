package com.evawova.preview.domain.interview.entity;

import com.evawova.preview.domain.interview.entity.enums.InterviewSessionStatus;
import com.evawova.preview.domain.user.entity.User;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InterviewSessionStatus status;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime expectedEndTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    private Integer totalConversationCount;

    @Column(nullable = false)
    private Integer totalTokenCount;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "settings_id", nullable = false)
    private InterviewSettings settings;

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

    public void setSettings(InterviewSettings settings) {
        this.settings = settings;
        settings.setSession(this);
    }

    public void addMessage(InterviewMessage message) {
        this.messages.add(message);
        message.setSession(this);
        this.totalConversationCount++;
    }

    public void endSession() {
        this.status = InterviewSessionStatus.COMPLETED;
        this.endTime = LocalDateTime.now();
    }

    public void cancelSession() {
        this.status = InterviewSessionStatus.CANCELLED;
        this.endTime = LocalDateTime.now();
    }
}