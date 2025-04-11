package com.evawova.preview.domain.interview.service;

import com.evawova.preview.domain.interview.dto.InterviewStartRequest;
import com.evawova.preview.domain.interview.dto.InterviewStartResponse;
import com.evawova.preview.domain.interview.entity.InterviewSession;
import com.evawova.preview.domain.interview.entity.InterviewSettings;
import com.evawova.preview.domain.interview.entity.enums.InterviewSessionStatus;
import com.evawova.preview.domain.interview.repository.InterviewSessionRepository;
import com.evawova.preview.domain.subscription.entity.SubscriptionUsage;
import com.evawova.preview.domain.subscription.service.SubscriptionService;
import com.evawova.preview.domain.user.entity.User;
import com.evawova.preview.domain.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InterviewService {

    private final InterviewSessionRepository interviewSessionRepository;
    private final UserService userService;
    private final SubscriptionService subscriptionService;

    @Transactional
    public InterviewStartResponse startInterview(InterviewStartRequest request) {
        // 현재 사용자 조회
        User currentUser = userService.getCurrentUser();

        // 구독 사용량 확인 및 사용
        SubscriptionUsage usage = subscriptionService.useInterview(currentUser);
        if (usage == null) {
            throw new IllegalStateException("사용 가능한 인터뷰 횟수가 없습니다.");
        }

        // 인터뷰 설정 생성
        InterviewSettings settings = InterviewSettings.builder()
                .type(request.getType())
                .jobRole(request.getJobRole())
                .interviewerStyle(request.getInterviewerStyle())
                .difficulty(request.getDifficulty())
                .duration(request.getDuration())
                .interviewMode(request.getInterviewMode())
                .experienceLevel(request.getExperienceLevel())
                .technicalSkills(request.getTechnicalSkills())
                .language(request.getLanguage())
                .build();

        // 인터뷰 세션 생성
        InterviewSession session = InterviewSession.builder()
                .user(currentUser)
                .status(InterviewSessionStatus.IN_PROGRESS)
                .startTime(LocalDateTime.now())
                .expectedEndTime(LocalDateTime.now().plusMinutes(request.getDuration().getMinutes()))
                .build();

        // 관계 설정
        session.setSettings(settings);
        session.setSubscriptionUsage(usage);

        // 저장
        InterviewSession savedSession = interviewSessionRepository.save(session);

        // 응답 생성
        return InterviewStartResponse.builder()
                .sessionId(savedSession.getId())
                .type(settings.getType())
                .jobRole(settings.getJobRole())
                .interviewerStyle(settings.getInterviewerStyle())
                .difficulty(settings.getDifficulty())
                .duration(settings.getDuration())
                .interviewMode(settings.getInterviewMode())
                .experienceLevel(settings.getExperienceLevel())
                .technicalSkills(settings.getTechnicalSkills())
                .language(settings.getLanguage())
                .startTime(savedSession.getStartTime())
                .expectedEndTime(savedSession.getExpectedEndTime())
                .build();
    }

    @Transactional
    public void endInterview(Long interviewId) {
        InterviewSession session = interviewSessionRepository.findById(interviewId)
                .orElseThrow(() -> new EntityNotFoundException("인터뷰 세션을 찾을 수 없습니다."));

        // 권한 체크
        User currentUser = userService.getCurrentUser();
        if (!session.getUser().equals(currentUser)) {
            throw new IllegalStateException("해당 인터뷰를 종료할 권한이 없습니다.");
        }

        // 상태 체크
        if (session.getStatus() != InterviewSessionStatus.IN_PROGRESS) {
            throw new IllegalStateException("진행 중인 인터뷰만 종료할 수 있습니다.");
        }

        session.endSession();
    }

    @Transactional
    public void cancelInterview(Long interviewId) {
        InterviewSession session = interviewSessionRepository.findById(interviewId)
                .orElseThrow(() -> new EntityNotFoundException("인터뷰 세션을 찾을 수 없습니다."));

        // 권한 체크
        User currentUser = userService.getCurrentUser();
        if (!session.getUser().equals(currentUser)) {
            throw new IllegalStateException("해당 인터뷰를 취소할 권한이 없습니다.");
        }

        // 상태 체크
        if (session.getStatus() != InterviewSessionStatus.IN_PROGRESS) {
            throw new IllegalStateException("진행 중인 인터뷰만 취소할 수 있습니다.");
        }

        // 구독 사용량 복구
        subscriptionService.refundInterview(session.getSubscriptionUsage());

        session.cancelSession();
    }
}
