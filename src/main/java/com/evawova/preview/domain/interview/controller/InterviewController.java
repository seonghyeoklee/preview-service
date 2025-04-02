package com.evawova.preview.domain.interview.controller;

import com.evawova.preview.domain.interview.dto.InterviewSessionDto;
import com.evawova.preview.domain.interview.dto.InterviewSettings;
import com.evawova.preview.domain.interview.model.*;
import com.evawova.preview.domain.interview.service.InterviewPromptService;
import com.evawova.preview.domain.interview.service.InterviewSessionService;
import com.evawova.preview.security.FirebaseUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 면접 관련 API를 제공하는 컨트롤러
 */
@Tag(name = "Interview", description = "면접 관련 API")
@RestController
@RequestMapping("/api/v1/interview")
@RequiredArgsConstructor
public class InterviewController {

    private final InterviewPromptService interviewPromptService;
    private final InterviewSessionService interviewSessionService;

    /**
     * 면접 프롬프트 생성 API
     */
    @Operation(summary = "면접 프롬프트 생성", description = "설정에 따라 AI 면접 프롬프트를 생성합니다.")
    @PostMapping("/prompt")
    public ResponseEntity<Map<String, String>> generatePrompt(@RequestBody InterviewSettings settings) {
        String prompt = interviewPromptService.generateInterviewPrompt(settings);
        return ResponseEntity.ok(Map.of("prompt", prompt));
    }
    
    /**
     * 면접 설정을 위한 메타데이터 제공 API
     */
    @Operation(summary = "메타데이터 조회", description = "면접 설정에 필요한 Enum 값들을 조회합니다.")
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
     * 인터뷰 세션 시작 API
     */
    @Operation(summary = "인터뷰 세션 시작", description = "새로운 인터뷰 세션을 시작합니다.")
    @PostMapping("/sessions/start")
    public ResponseEntity<InterviewSessionDto> startInterviewSession(
            @AuthenticationPrincipal FirebaseUserDetails firebaseUser,
            @RequestBody InterviewSettings settings) {
        
        InterviewSessionDto sessionDto = interviewSessionService.startInterviewSession(
                firebaseUser.getUser().getId(), settings);
        
        return ResponseEntity.ok(sessionDto);
    }
    
    /**
     * 인터뷰 세션 종료 API
     */
    @Operation(summary = "인터뷰 세션 종료", description = "진행 중인 인터뷰 세션을 종료합니다.")
    @PostMapping("/sessions/{sessionId}/end")
    public ResponseEntity<InterviewSessionDto> endInterviewSession(
            @AuthenticationPrincipal FirebaseUserDetails firebaseUser,
            @Parameter(description = "세션 ID") @PathVariable String sessionId) {
        
        InterviewSessionDto sessionDto = interviewSessionService.endInterviewSession(sessionId);
        return ResponseEntity.ok(sessionDto);
    }
    
    /**
     * 인터뷰 세션 상세 조회 API
     */
    @Operation(summary = "인터뷰 세션 조회", description = "특정 인터뷰 세션의 상세 정보를 조회합니다.")
    @GetMapping("/sessions/{sessionId}")
    public ResponseEntity<InterviewSessionDto> getInterviewSession(
            @AuthenticationPrincipal FirebaseUserDetails firebaseUser,
            @Parameter(description = "세션 ID") @PathVariable String sessionId) {
        
        InterviewSessionDto sessionDto = interviewSessionService.getInterviewSession(sessionId);
        return ResponseEntity.ok(sessionDto);
    }
    
    /**
     * 사용자의 인터뷰 세션 목록 조회 API (페이징)
     */
    @Operation(summary = "인터뷰 세션 목록 조회", description = "사용자의 모든 인터뷰 세션 목록을 페이징하여 조회합니다.")
    @GetMapping("/sessions")
    public ResponseEntity<Page<InterviewSessionDto>> getUserInterviewSessions(
            @AuthenticationPrincipal FirebaseUserDetails firebaseUser,
            Pageable pageable) {
        
        Page<InterviewSessionDto> sessions = interviewSessionService.getUserInterviewSessions(
                firebaseUser.getUser().getId(), pageable);
        
        return ResponseEntity.ok(sessions);
    }
    
    /**
     * 사용자의 최근 인터뷰 세션 조회 API
     */
    @Operation(summary = "최근 인터뷰 세션 조회", description = "사용자의 가장 최근 인터뷰 세션을 조회합니다.")
    @GetMapping("/sessions/latest")
    public ResponseEntity<InterviewSessionDto> getLatestInterviewSession(
            @AuthenticationPrincipal FirebaseUserDetails firebaseUser) {
        
        InterviewSessionDto sessionDto = interviewSessionService.getLatestUserInterviewSession(
                firebaseUser.getUser().getId());
        
        return ResponseEntity.ok(sessionDto);
    }
    
    /**
     * 사용자의 미완료 인터뷰 세션 목록 조회 API
     */
    @Operation(summary = "미완료 인터뷰 세션 조회", description = "사용자의 종료되지 않은 인터뷰 세션 목록을 조회합니다.")
    @GetMapping("/sessions/incomplete")
    public ResponseEntity<List<InterviewSessionDto>> getIncompleteInterviewSessions(
            @AuthenticationPrincipal FirebaseUserDetails firebaseUser) {
        
        List<InterviewSessionDto> sessions = interviewSessionService.getIncompleteInterviewSessions(
                firebaseUser.getUser().getId());
        
        return ResponseEntity.ok(sessions);
    }
} 