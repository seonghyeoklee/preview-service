package com.evawova.preview.domain.user.event;

import com.evawova.preview.domain.common.model.AbstractDomainEvent;
import com.evawova.preview.domain.user.entity.PlanType;
import lombok.Getter;

@Getter
public class PlanCreatedEvent extends AbstractDomainEvent {
    private final Long planId;
    private final PlanType planType;

    public PlanCreatedEvent(Long planId, PlanType planType) {
        super();
        this.planId = planId;
        this.planType = planType;
    }
}