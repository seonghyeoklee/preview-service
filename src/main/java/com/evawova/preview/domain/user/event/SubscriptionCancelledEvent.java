package com.evawova.preview.domain.user.event;

import com.evawova.preview.domain.common.model.AbstractDomainEvent;
import com.evawova.preview.domain.user.entity.Subscription;
import lombok.Getter;

@Getter
public class SubscriptionCancelledEvent extends AbstractDomainEvent {
    private final Long subscriptionId;
    private final Long userId;
    private final Long planId;
    private final String reason;

    public SubscriptionCancelledEvent(Subscription subscription, String reason) {
        super();
        this.subscriptionId = subscription.getId();
        this.userId = subscription.getUser().getId();
        this.planId = subscription.getPlan().getId();
        this.reason = reason;
    }
}