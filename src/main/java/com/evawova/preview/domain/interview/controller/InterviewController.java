package com.evawova.preview.domain.interview.controller;

import com.evawova.preview.domain.interview.dto.InterviewSessionDto;
import com.evawova.preview.domain.interview.dto.InterviewSettings;
import com.evawova.preview.domain.interview.model.*;
import com.evawova.preview.domain.interview.service.InterviewPromptService;
import com.evawova.preview.domain.interview.service.InterviewSessionService;
import com.evawova.preview.security.FirebaseUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 면접 관련 API를 제공하는 컨트롤러
 */
@RestController
@RequestMapping("/api/v1/interview")
@RequiredArgsConstructor
public class InterviewController {

    private final InterviewPromptService interviewPromptService;
    private final InterviewSessionService interviewSessionService;

    /**
     * 면접 프롬프트 생성 API
     * 
     * @param settings 면접 설정 정보
     * @return 생성된 프롬프트
     */
    @PostMapping("/prompt")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<String, String>> generatePrompt(@RequestBody InterviewSettings settings) {
        String prompt = interviewPromptService.generateInterviewPrompt(settings);
        return ResponseEntity.ok(Map.of("prompt", prompt));
    }
    
    /**
     * 면접 설정을 위한 메타데이터 제공 API
     * (Enum 값들을 클라이언트에게 제공)
     * 
     * @return 메타데이터
     */
    @GetMapping("/metadata")
    public ResponseEntity<Map<String, Object>> getMetadata() {
        return ResponseEntity.ok(Map.of(
            "interviewTypes", InterviewType.values(),
            "jobRoles", JobRole.values(),
            "interviewerStyles", InterviewerStyle.values(),
            "difficulties", InterviewDifficulty.values(),
            "durations", InterviewDuration.values(),
            "modes", InterviewMode.values(),
            "experienceLevels", ExperienceLevel.values(),
            "languages", InterviewLanguage.values()
        ));
    }
    
    /**
     * 레거시 호환을 위한 면접 프롬프트 생성 API
     */
    @PostMapping("/legacy-prompt")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<String, String>> generateLegacyPrompt(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) String interviewStyle,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) String duration,
            @RequestParam(required = false) String interviewMode,
            @RequestParam(required = false) String experienceLevel,
            @RequestParam(required = false) List<String> technicalSkills,
            @RequestParam(required = false, defaultValue = "ko") String language) {
        
        InterviewSettings settings = InterviewSettings.fromLegacy(
                type, position, interviewStyle, difficulty, duration, 
                interviewMode, experienceLevel, technicalSkills, language);
        
        String prompt = interviewPromptService.generateInterviewPrompt(settings);
        return ResponseEntity.ok(Map.of("prompt", prompt));
    }
    
    /**
     * 인터뷰 세션 시작 API
     * 
     * @param firebaseUser 현재 로그인한 Firebase 사용자
     * @param settings 인터뷰 설정
     * @return 생성된 인터뷰 세션 정보
     */
    @PostMapping("/sessions/start")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<InterviewSessionDto> startInterviewSession(
            @AuthenticationPrincipal FirebaseUserDetails firebaseUser,
            @RequestBody InterviewSettings settings) {
        
        InterviewSessionDto sessionDto = interviewSessionService.startInterviewSession(
                firebaseUser.getUser().getId(), settings);
        
        return ResponseEntity.ok(sessionDto);
    }
    
    /**
     * 인터뷰 세션 종료 API
     * 
     * @param sessionId 세션 ID
     * @return 업데이트된 인터뷰 세션 정보
     */
    @PostMapping("/sessions/{sessionId}/end")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<InterviewSessionDto> endInterviewSession(
            @PathVariable String sessionId) {
        
        InterviewSessionDto sessionDto = interviewSessionService.endInterviewSession(sessionId);
        return ResponseEntity.ok(sessionDto);
    }
    
    /**
     * 인터뷰 세션 상세 조회 API
     * 
     * @param sessionId 세션 ID
     * @return 인터뷰 세션 정보
     */
    @GetMapping("/sessions/{sessionId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<InterviewSessionDto> getInterviewSession(
            @PathVariable String sessionId) {
        
        InterviewSessionDto sessionDto = interviewSessionService.getInterviewSession(sessionId);
        return ResponseEntity.ok(sessionDto);
    }
    
    /**
     * 사용자의 인터뷰 세션 목록 조회 API (페이징)
     * 
     * @param firebaseUser 현재 로그인한 Firebase 사용자
     * @param pageable 페이징 정보
     * @return 인터뷰 세션 목록
     */
    @GetMapping("/sessions")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<InterviewSessionDto>> getUserInterviewSessions(
            @AuthenticationPrincipal FirebaseUserDetails firebaseUser,
            Pageable pageable) {
        
        Page<InterviewSessionDto> sessions = interviewSessionService.getUserInterviewSessions(
                firebaseUser.getUser().getId(), pageable);
        
        return ResponseEntity.ok(sessions);
    }
    
    /**
     * 사용자의 최근 인터뷰 세션 조회 API
     * 
     * @param firebaseUser 현재 로그인한 Firebase 사용자
     * @return 최근 인터뷰 세션 정보
     */
    @GetMapping("/sessions/latest")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<InterviewSessionDto> getLatestInterviewSession(
            @AuthenticationPrincipal FirebaseUserDetails firebaseUser) {
        
        InterviewSessionDto sessionDto = interviewSessionService.getLatestUserInterviewSession(
                firebaseUser.getUser().getId());
        
        return ResponseEntity.ok(sessionDto);
    }
    
    /**
     * 사용자의 미완료 인터뷰 세션 목록 조회 API
     * 
     * @param firebaseUser 현재 로그인한 Firebase 사용자
     * @return 미완료 인터뷰 세션 목록
     */
    @GetMapping("/sessions/incomplete")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<InterviewSessionDto>> getIncompleteInterviewSessions(
            @AuthenticationPrincipal FirebaseUserDetails firebaseUser) {
        
        List<InterviewSessionDto> sessions = interviewSessionService.getIncompleteInterviewSessions(
                firebaseUser.getUser().getId());
        
        return ResponseEntity.ok(sessions);
    }
} 