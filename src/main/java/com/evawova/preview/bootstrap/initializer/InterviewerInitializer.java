package com.evawova.preview.bootstrap.initializer;

import com.evawova.preview.bootstrap.EntityInitializer;
import com.evawova.preview.domain.interview.entity.Interviewer;
import com.evawova.preview.domain.interview.entity.Interviewer.InterviewerPersonality;
import com.evawova.preview.domain.interview.entity.Interviewer.QuestionStyle;
import com.evawova.preview.domain.interview.entity.Interviewer.FeedbackStyle;
import com.evawova.preview.domain.interview.repository.InterviewerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 면접관 초기 데이터를 생성하는 Initializer
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InterviewerInitializer implements EntityInitializer {

    private final InterviewerRepository interviewerRepository;

    @Override
    @Transactional
    public void initialize() {
        // 이미 데이터가 있는지 확인
        if (interviewerRepository.count() > 0) {
            log.info("이미 면접관 데이터가 존재합니다. 초기화를 건너뜁니다.");
            return;
        }

        log.info("면접관 초기 데이터를 생성합니다...");
        LocalDateTime now = LocalDateTime.now();
        List<Interviewer> interviewers = new ArrayList<>();

        // 친절한 면접관
        interviewers.add(Interviewer.builder()
                .code("friendly")
                .name("김친절")
                .nameEn("Kim Friendly")
                .description("지원자를 편안하게 해주며 대화형 면접을 진행하는 친절한 면접관입니다.")
                .descriptionEn(
                        "A friendly interviewer who makes candidates comfortable and conducts conversational interviews.")
                .personality(InterviewerPersonality.FRIENDLY)
                .questionStyle(QuestionStyle.OPEN_ENDED)
                .feedbackStyle(FeedbackStyle.ENCOURAGING)
                .profileImageUrl("/images/interviewers/friendly.png")
                .active(true)
                .sortOrder(1)
                .createdAt(now)
                .updatedAt(now)
                .build());

        // 엄격한 면접관
        interviewers.add(Interviewer.builder()
                .code("strict")
                .name("박엄격")
                .nameEn("Park Strict")
                .description("정확한 답변을 요구하고 꼼꼼하게 검증하는 엄격한 면접관입니다.")
                .descriptionEn("A strict interviewer who demands precise answers and thoroughly verifies them.")
                .personality(InterviewerPersonality.STRICT)
                .questionStyle(QuestionStyle.DIRECT)
                .feedbackStyle(FeedbackStyle.CRITICAL)
                .profileImageUrl("/images/interviewers/strict.png")
                .active(true)
                .sortOrder(2)
                .createdAt(now)
                .updatedAt(now)
                .build());

        // 기술적인 면접관
        interviewers.add(Interviewer.builder()
                .code("technical")
                .name("이기술")
                .nameEn("Lee Technical")
                .description("심층적인 기술 지식을 검증하는 기술 중심 면접관입니다.")
                .descriptionEn("A technically-focused interviewer who verifies in-depth technical knowledge.")
                .personality(InterviewerPersonality.TECHNICAL)
                .questionStyle(QuestionStyle.TECHNICAL)
                .feedbackStyle(FeedbackStyle.CONSTRUCTIVE)
                .profileImageUrl("/images/interviewers/technical.png")
                .active(true)
                .sortOrder(3)
                .createdAt(now)
                .updatedAt(now)
                .build());

        // 균형 잡힌 면접관
        interviewers.add(Interviewer.builder()
                .code("balanced")
                .name("최균형")
                .nameEn("Choi Balanced")
                .description("기술과 인성을 균형있게 평가하는 균형 잡힌 면접관입니다.")
                .descriptionEn("A balanced interviewer who evaluates both technical skills and personality traits.")
                .personality(InterviewerPersonality.BALANCED)
                .questionStyle(QuestionStyle.MIXED)
                .feedbackStyle(FeedbackStyle.BALANCED)
                .profileImageUrl("/images/interviewers/balanced.png")
                .active(true)
                .sortOrder(4)
                .createdAt(now)
                .updatedAt(now)
                .build());

        // 상황 중심 면접관
        interviewers.add(Interviewer.builder()
                .code("situational")
                .name("정상황")
                .nameEn("Jung Situational")
                .description("실제 업무 상황을 가정한 질문으로 문제 해결 능력을 확인하는 면접관입니다.")
                .descriptionEn(
                        "An interviewer who checks problem-solving skills through questions based on real work situations.")
                .personality(InterviewerPersonality.PRAGMATIC)
                .questionStyle(QuestionStyle.SITUATIONAL)
                .feedbackStyle(FeedbackStyle.PRACTICAL)
                .profileImageUrl("/images/interviewers/situational.png")
                .active(true)
                .sortOrder(5)
                .createdAt(now)
                .updatedAt(now)
                .build());

        // 창의적인 면접관
        interviewers.add(Interviewer.builder()
                .code("creative")
                .name("한창의")
                .nameEn("Han Creative")
                .description("창의적인 사고와 문제 해결 능력을 테스트하는 면접관입니다.")
                .descriptionEn("An interviewer who tests creative thinking and problem-solving abilities.")
                .personality(InterviewerPersonality.CREATIVE)
                .questionStyle(QuestionStyle.HYPOTHETICAL)
                .feedbackStyle(FeedbackStyle.INSIGHTFUL)
                .profileImageUrl("/images/interviewers/creative.png")
                .active(true)
                .sortOrder(6)
                .createdAt(now)
                .updatedAt(now)
                .build());

        // 저장
        interviewerRepository.saveAll(interviewers);
        log.info("{}개의 면접관 초기 데이터가 생성되었습니다.", interviewers.size());
    }

    @Override
    public String getEntityName() {
        return "Interviewer";
    }

    @Override
    public int getOrder() {
        return 7; // 다른 기본 엔티티 이후에 초기화
    }
}