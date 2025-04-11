package com.evawova.preview.domain.user.event;

import com.evawova.preview.domain.common.model.AbstractDomainEvent;
import com.evawova.preview.domain.user.entity.Subscription;
import lombok.Getter;

@Getter
public class SubscriptionCreatedEvent extends AbstractDomainEvent {
    private final Long subscriptionId;
    private final Long userId;
    private final Long planId;
    private final Subscription.SubscriptionCycle subscriptionCycle;
    private final String paymentAmount;

    public SubscriptionCreatedEvent(Subscription subscription) {
        super();
        this.subscriptionId = subscription.getId();
        this.userId = subscription.getUser().getId();
        this.planId = subscription.getPlan().getId();
        this.subscriptionCycle = subscription.getSubscriptionCycle();
        this.paymentAmount = subscription.getPaymentAmount().toString();
    }
}