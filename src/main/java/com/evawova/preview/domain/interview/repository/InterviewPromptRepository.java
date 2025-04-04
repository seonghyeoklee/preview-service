package com.evawova.preview.domain.interview.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.evawova.preview.domain.interview.entity.InterviewPrompt;
import com.evawova.preview.domain.interview.model.PromptCategory;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterviewPromptRepository extends JpaRepository<InterviewPrompt, Long> {
    /**
     * 대분류 프롬프트 목록 조회 (level = 1)
     */
    @Query("SELECT p FROM InterviewPrompt p WHERE p.level = 1 ORDER BY p.id")
    List<InterviewPrompt> findMainPrompts();

    /**
     * 특정 부모 프롬프트 하위의 중분류 프롬프트 목록 조회
     */
    @Query("SELECT p FROM InterviewPrompt p WHERE p.parent.id = :parentId ORDER BY p.id")
    List<InterviewPrompt> findSubPromptsByParentId(@Param("parentId") Long parentId);

    /**
     * 카테고리별 활성화된 프롬프트 조회
     */
    List<InterviewPrompt> findByCategoryAndActive(PromptCategory category, boolean active);

    /**
     * 활성화 상태별 프롬프트 조회
     */
    List<InterviewPrompt> findByActive(boolean active);

    /**
     * 자식 프롬프트가 있는지 확인
     */
    boolean existsByParentId(Long parentId);

    /**
     * 특정 계층의 프롬프트 조회
     */
    List<InterviewPrompt> findByLevel(Integer level);

    /**
     * 프롬프트와 그 자식 프롬프트 함께 조회
     */
    @Query("SELECT p FROM InterviewPrompt p LEFT JOIN FETCH p.children WHERE p.id = :id")
    Optional<InterviewPrompt> findByIdWithChildren(@Param("id") Long id);
}