package com.evawova.preview.domain.interview.service;

import com.evawova.preview.domain.interview.dto.InterviewSessionDto;
import com.evawova.preview.domain.interview.dto.InterviewSettings;
import com.evawova.preview.domain.interview.entity.InterviewSession;
import com.evawova.preview.domain.interview.repository.InterviewSessionRepository;
import com.evawova.preview.domain.user.entity.User;
import com.evawova.preview.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 인터뷰 세션 서비스
 */
@Service
@RequiredArgsConstructor
public class InterviewSessionService {

    private final InterviewSessionRepository interviewSessionRepository;
    private final UserRepository userRepository;
    private final InterviewPromptService interviewPromptService;

    /**
     * 인터뷰 세션 시작
     * 
     * @param userId 사용자 ID
     * @param settings 인터뷰 설정
     * @return 생성된 인터뷰 세션 DTO
     */
    @Transactional
    public InterviewSessionDto startInterviewSession(Long userId, InterviewSettings settings) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다: " + userId));
        
        // 프롬프트 생성
        String prompt = interviewPromptService.generateInterviewPrompt(settings);
        
        // 세션 생성 및 저장
        InterviewSession session = InterviewSession.create(user, settings, prompt);
        interviewSessionRepository.save(session);
        
        return InterviewSessionDto.fromEntity(session);
    }

    /**
     * 인터뷰 세션 종료
     * 
     * @param sessionId 세션 ID
     * @return 업데이트된 인터뷰 세션 DTO
     */
    @Transactional
    public InterviewSessionDto endInterviewSession(String sessionId) {
        InterviewSession session = interviewSessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("인터뷰 세션을 찾을 수 없습니다: " + sessionId));
        
        session.end();
        interviewSessionRepository.save(session);
        
        return InterviewSessionDto.fromEntity(session);
    }

    /**
     * 인터뷰 세션 조회
     * 
     * @param sessionId 세션 ID
     * @return 인터뷰 세션 DTO
     */
    @Transactional(readOnly = true)
    public InterviewSessionDto getInterviewSession(String sessionId) {
        InterviewSession session = interviewSessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("인터뷰 세션을 찾을 수 없습니다: " + sessionId));
        
        return InterviewSessionDto.fromEntity(session);
    }

    /**
     * 사용자의 인터뷰 세션 목록 조회 (페이징)
     * 
     * @param userId 사용자 ID
     * @param pageable 페이징 정보
     * @return 인터뷰 세션 DTO 페이지
     */
    @Transactional(readOnly = true)
    public Page<InterviewSessionDto> getUserInterviewSessions(Long userId, Pageable pageable) {
        return interviewSessionRepository.findByUserId(userId, pageable)
                .map(InterviewSessionDto::fromEntity);
    }

    /**
     * 사용자의 인터뷰 세션 목록 조회
     * 
     * @param userId 사용자 ID
     * @return 인터뷰 세션 DTO 리스트
     */
    @Transactional(readOnly = true)
    public List<InterviewSessionDto> getUserInterviewSessionList(Long userId) {
        return interviewSessionRepository.findByUserIdOrderByStartedAtDesc(userId)
                .stream()
                .map(InterviewSessionDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 사용자의 최근 인터뷰 세션 조회
     * 
     * @param userId 사용자 ID
     * @return 인터뷰 세션 DTO (optional)
     */
    @Transactional(readOnly = true)
    public InterviewSessionDto getLatestUserInterviewSession(Long userId) {
        InterviewSession session = interviewSessionRepository.findTopByUserIdOrderByStartedAtDesc(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자의 인터뷰 세션을 찾을 수 없습니다: " + userId));
        
        return InterviewSessionDto.fromEntity(session);
    }

    /**
     * 미완료 인터뷰 세션 조회
     * 
     * @param userId 사용자 ID
     * @return 미완료 인터뷰 세션 DTO 리스트
     */
    @Transactional(readOnly = true)
    public List<InterviewSessionDto> getIncompleteInterviewSessions(Long userId) {
        return interviewSessionRepository.findByUserIdAndEndedAtIsNull(userId)
                .stream()
                .map(InterviewSessionDto::fromEntity)
                .collect(Collectors.toList());
    }
} 