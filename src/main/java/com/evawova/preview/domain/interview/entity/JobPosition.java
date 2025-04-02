package com.evawova.preview.domain.interview.entity;

import com.evawova.preview.domain.interview.model.JobRole;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "job_positions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class JobPosition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("직무 포지션 ID")
    private Long id;

    @Column(nullable = false, unique = true)
    @Comment("직무 포지션 고유 식별자 (Frontend 용)")
    private String positionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Comment("직무 역할 Enum")
    private JobRole role;

    @Column(nullable = false)
    @Comment("직무 제목")
    private String title;

    @Column(columnDefinition = "TEXT")
    @Comment("직무 설명")
    private String description;

    @Column(nullable = false)
    @Comment("아이콘 이름 (FontAwesome)")
    private String icon;

    @ElementCollection(fetch = FetchType.LAZY) // Use LAZY fetch for potentially large collections
    @CollectionTable(name = "job_position_skills", joinColumns = @JoinColumn(name = "job_position_id"))
    @Column(name = "skill")
    @Comment("요구 기술 스택")
    private List<String> skills;

    @Column(nullable = false)
    @Comment("생성 시간")
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @Comment("수정 시간")
    private LocalDateTime updatedAt;

    // Ensure timestamps are set during creation via builder
    public static class JobPositionBuilder {
        private LocalDateTime createdAt = LocalDateTime.now();
        private LocalDateTime updatedAt = LocalDateTime.now();
    }
}