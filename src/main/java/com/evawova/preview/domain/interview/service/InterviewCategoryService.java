package com.evawova.preview.domain.interview.service;

import com.evawova.preview.domain.interview.dto.InterviewCategoryDto;
import com.evawova.preview.domain.interview.dto.SubCategoryWithSkillsDto;
import com.evawova.preview.domain.interview.entity.InterviewCategory;
import com.evawova.preview.domain.interview.repository.InterviewCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InterviewCategoryService {

    private final InterviewCategoryRepository interviewCategoryRepository;

    /**
     * 모든 카테고리 조회 (계층형 구조로 변환)
     */
    public List<InterviewCategoryDto> getAllCategories() {
        // 대분류(최상위) 카테고리만 가져오고 각 카테고리마다 하위 카테고리를 포함하여 반환
        return interviewCategoryRepository.findMainCategoriesWithChildren().stream()
                .map(InterviewCategoryDto::fromWithChildren)
                .collect(Collectors.toList());
    }

    /**
     * 대분류 카테고리 조회
     */
    public List<InterviewCategoryDto> getMainCategories() {
        return interviewCategoryRepository.findMainCategories().stream()
                .map(InterviewCategoryDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 특정 부모 카테고리의 하위 카테고리(중분류) 조회
     */
    public List<InterviewCategoryDto> getSubCategories(Long parentId) {
        return interviewCategoryRepository.findSubCategoriesByParentId(parentId).stream()
                .map(InterviewCategoryDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 카테고리와 연관된 스킬 정보 함께 조회
     */
    public SubCategoryWithSkillsDto getCategoryWithSkills(Long categoryId) {
        InterviewCategory category = interviewCategoryRepository.findByIdWithSkills(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("해당 카테고리를 찾을 수 없습니다: " + categoryId));

        return SubCategoryWithSkillsDto.from(category);
    }

    /**
     * 계층형 구조로 카테고리 조회 (대분류만)
     * 대분류 카테고리와 그 하위의 모든 카테고리를 계층형으로 함께 조회
     */
    public List<InterviewCategoryDto> getHierarchicalCategories() {
        // 최상위 카테고리(대분류)만 가져오고, 각 대분류마다 하위 카테고리를 재귀적으로 포함
        return interviewCategoryRepository.findMainCategoriesWithChildren().stream()
                .map(InterviewCategoryDto::fromWithChildren)
                .collect(Collectors.toList());
    }

    /**
     * 특정 카테고리와 그 하위 계층 조회
     */
    public InterviewCategoryDto getCategoryHierarchy(Long categoryId) {
        InterviewCategory category = interviewCategoryRepository.findByIdWithChildren(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("해당 카테고리를 찾을 수 없습니다: " + categoryId));

        return InterviewCategoryDto.fromWithChildren(category);
    }

    // TODO: Add methods for creating, updating, deleting categories if needed.
}