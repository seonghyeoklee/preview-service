package com.evawova.preview.domain.user.event;

import com.evawova.preview.domain.common.model.AbstractDomainEvent;
import com.evawova.preview.domain.user.entity.Subscription;
import lombok.Getter;

@Getter
public class SubscriptionRenewedEvent extends AbstractDomainEvent {
    private final Long subscriptionId;
    private final Long userId;
    private final Long planId;
    private final String newEndDate;
    private final String paymentAmount;

    public SubscriptionRenewedEvent(Subscription subscription) {
        super();
        this.subscriptionId = subscription.getId();
        this.userId = subscription.getUser().getId();
        this.planId = subscription.getPlan().getId();
        this.newEndDate = subscription.getEndDate().toString();
        this.paymentAmount = subscription.getPaymentAmount().toString();
    }
}