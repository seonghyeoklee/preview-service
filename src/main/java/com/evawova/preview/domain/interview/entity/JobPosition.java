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
    @Comment("���� ������ ID")
    private Long id;

    @Column(nullable = false, unique = true)
    @Comment("���� ������ ���� �ĺ��� (Frontend ��)")
    private String positionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Comment("���� ���� Enum")
    private JobRole role;

    @Column(nullable = false)
    @Comment("���� ����")
    private String title;

    @Column(columnDefinition = "TEXT")
    @Comment("���� ����")
    private String description;

    @Column(nullable = false)
    @Comment("������ �̸� (FontAwesome)")
    private String icon;

    @ElementCollection(fetch = FetchType.LAZY) // Use LAZY fetch for potentially large collections
    @CollectionTable(name = "job_position_skills", joinColumns = @JoinColumn(name = "job_position_id"))
    @Column(name = "skill")
    @Comment("�䱸 ��� ����")
    private List<String> skills;

    @Column(nullable = false)
    @Comment("���� �ð�")
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @Comment("���� �ð�")
    private LocalDateTime updatedAt;

    // Ensure timestamps are set during creation via builder
    public static class JobPositionBuilder {
        private LocalDateTime createdAt = LocalDateTime.now();
        private LocalDateTime updatedAt = LocalDateTime.now();
    }
}