package com.evawova.preview.domain.user.event.handler;

import com.evawova.preview.domain.user.event.UserCreatedEvent;
import com.evawova.preview.domain.user.event.UserPlanChangedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserEventHandler {

    @EventListener
    public void handleUserCreatedEvent(UserCreatedEvent event) {
        log.info("사용자가 생성되었습니다. 사용자 ID: {}, 이메일: {}, 이름: {}, 플랜: {}",
                event.getUserId(), event.getEmail(), event.getName(), event.getPlanType());
        
        // 실제 애플리케이션에서는 여기서 이메일 발송, 통계 업데이트 등의 작업 수행
    }

    @EventListener
    public void handleUserPlanChangedEvent(UserPlanChangedEvent event) {
        log.info("사용자의 플랜이 변경되었습니다. 사용자 ID: {}, 이메일: {}, 이전 플랜: {}, 새 플랜: {}",
                event.getUserId(), event.getEmail(), event.getOldPlanType(), event.getNewPlanType());
        
        // 실제 애플리케이션에서는 여기서 결제 처리, 이메일 발송, 통계 업데이트 등의 작업 수행
    }
} 