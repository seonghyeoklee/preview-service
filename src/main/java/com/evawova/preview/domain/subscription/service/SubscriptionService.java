package com.evawova.preview.domain.subscription.service;

import com.evawova.preview.domain.subscription.entity.SubscriptionUsage;
import com.evawova.preview.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    @Transactional
    public SubscriptionUsage useInterview(User user) {
        // TODO: 구독 사용량 확인 및 사용 로직 구현
        return null;
    }

    @Transactional
    public void refundInterview(SubscriptionUsage usage) {
        // TODO: 구독 사용량 복구 로직 구현
    }
}