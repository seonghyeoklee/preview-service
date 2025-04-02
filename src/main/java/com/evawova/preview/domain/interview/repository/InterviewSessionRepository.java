package com.evawova.preview.domain.interview.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.evawova.preview.domain.interview.entity.InterviewSession;

import java.util.List;
import java.util.Optional;

/**
 * 인터뷰 세션 저장소
 */
@Repository
public interface InterviewSessionRepository extends JpaRepository<InterviewSession, Long> {
    
    /**
     * 세션 ID로 인터뷰 세션 조회
     */
    Optional<InterviewSession> findBySessionId(String sessionId);
    
    /**
     * 사용자의 모든 인터뷰 세션 조회 (페이징)
     */
    Page<InterviewSession> findByUserId(Long userId, Pageable pageable);
    
    /**
     * 사용자의 모든 인터뷰 세션 조회
     */
    List<InterviewSession> findByUserIdOrderByStartedAtDesc(Long userId);
    
    /**
     * 사용자의 최근 인터뷰 세션 조회
     */
    Optional<InterviewSession> findTopByUserIdOrderByStartedAtDesc(Long userId);
    
    /**
     * 미완료 인터뷰 세션 조회
     */
    List<InterviewSession> findByUserIdAndEndedAtIsNull(Long userId);
} 