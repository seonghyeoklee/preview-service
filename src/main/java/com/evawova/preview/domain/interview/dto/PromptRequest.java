package com.evawova.preview.domain.interview.dto;

import com.evawova.preview.domain.interview.model.PromptCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "면접 프롬프트 생성 요청")
public class PromptRequest {

    @Schema(description = "프롬프트 이름")
    private String name;

    @Schema(description = "프롬프트 카테고리")
    private PromptCategory category;

    @Schema(description = "프롬프트 내용")
    private String content;

    @Schema(description = "프롬프트 활성화 여부", defaultValue = "true")
    private boolean active;

    @Schema(description = "상위 프롬프트 ID (대분류 프롬프트인 경우 null)")
    private Long parentId;

    @Schema(description = "프롬프트 계층 (1: 대분류, 2: 중분류, 3: 소분류)", defaultValue = "1")
    private Integer level;

    /**
     * PromptRequest를 InterviewPromptDto로 변환
     */
    public InterviewPromptDto toDto() {
        return InterviewPromptDto.builder()
                .name(this.name)
                .category(this.category)
                .content(this.content)
                .active(this.active)
                .parentId(this.parentId)
                .level(this.level)
                .build();
    }
}