package com.evawova.preview.domain.interview.repository;

import com.evawova.preview.domain.interview.entity.InterviewCategory;
import com.evawova.preview.domain.interview.model.InterviewType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InterviewCategoryRepository extends JpaRepository<InterviewCategory, Long> {
    /**
     * 대분류 카테고리 목록 조회 (level = 1)
     */
    @Query("SELECT c FROM InterviewCategory c WHERE c.level = 1 ORDER BY c.id")
    List<InterviewCategory> findMainCategories();

    /**
     * 특정 부모 카테고리 하위의 중분류 카테고리 목록 조회
     */
    @Query("SELECT c FROM InterviewCategory c WHERE c.parent.id = :parentId ORDER BY c.id")
    List<InterviewCategory> findSubCategoriesByParentId(@Param("parentId") Long parentId);

    /**
     * Type으로 카테고리 조회
     */
    Optional<InterviewCategory> findByType(InterviewType type);

    /**
     * 자식 카테고리가 있는지 확인
     */
    boolean existsByParentId(Long parentId);

    /**
     * 카테고리와 연관된 스킬 정보 함께 조회
     */
    @Query("SELECT c FROM InterviewCategory c LEFT JOIN FETCH c.skills WHERE c.id = :id")
    Optional<InterviewCategory> findByIdWithSkills(@Param("id") Long id);

    /**
     * 카테고리와 그 하위 카테고리를 모두 함께 조회
     */
    @Query("SELECT DISTINCT c FROM InterviewCategory c LEFT JOIN FETCH c.children WHERE c.id = :id")
    Optional<InterviewCategory> findByIdWithChildren(@Param("id") Long id);

    /**
     * 대분류 카테고리와 모든 하위 카테고리를 함께 조회
     */
    @Query("SELECT DISTINCT c FROM InterviewCategory c LEFT JOIN FETCH c.children WHERE c.level = 1")
    List<InterviewCategory> findMainCategoriesWithChildren();
}