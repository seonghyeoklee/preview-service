package com.evawova.preview.domain.interview.dto;

import com.evawova.preview.domain.interview.entity.InterviewCategory;
import com.evawova.preview.domain.interview.model.InterviewType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class InterviewCategoryDto {

    private Long id;
    private String icon;
    private String title;
    private String titleEn;
    private String description;
    private String descriptionEn;
    private InterviewType type;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long parentId;
    private String parentTitle;
    private Integer level;

    @Builder.Default
    private List<InterviewCategoryDto> children = new ArrayList<>();

    public static InterviewCategoryDto from(InterviewCategory entity) {
        return InterviewCategoryDto.builder()
                .id(entity.getId())
                .icon(entity.getIcon())
                .title(entity.getTitle())
                .titleEn(entity.getTitleEn())
                .description(entity.getDescription())
                .descriptionEn(entity.getDescriptionEn())
                .type(entity.getType())
                .parentId(entity.getParent() != null ? entity.getParent().getId() : null)
                .parentTitle(entity.getParent() != null ? entity.getParent().getTitle() : null)
                .level(entity.getLevel())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    /**
     * 계층형 구조로 변환 (재귀적으로 자식 카테고리 포함)
     */
    public static InterviewCategoryDto fromWithChildren(InterviewCategory entity) {
        InterviewCategoryDto dto = from(entity);

        if (entity.getChildren() != null && !entity.getChildren().isEmpty()) {
            dto.children.addAll(
                    entity.getChildren().stream()
                            .map(InterviewCategoryDto::fromWithChildren)
                            .collect(Collectors.toList()));
        }

        return dto;
    }
}