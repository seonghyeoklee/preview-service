package com.evawova.preview.domain.interview.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "interview_messages")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class InterviewMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    @Comment("인터뷰 세션")
    private InterviewSession session;

    @Column(nullable = false)
    @Comment("메시지 타입")
    @Enumerated(EnumType.STRING)
    private MessageType type;

    @Column(nullable = false, columnDefinition = "TEXT")
    @Comment("메시지 내용")
    private String content;

    @Column(nullable = false)
    @Comment("토큰 수")
    private int tokenCount;

    @Column(nullable = false)
    @Comment("메시지 순서")
    private int messageOrder;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    @Comment("생성 시간")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    @Comment("수정 시간")
    private LocalDateTime updatedAt;

    public enum MessageType {
        USER, // 사용자 메시지
        AI // AI 메시지
    }

    public void setSession(InterviewSession session) {
        this.session = session;
    }
}