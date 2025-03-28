package com.evawova.preview.domain.user.event;

import com.evawova.preview.domain.common.model.AbstractDomainEvent;
import com.evawova.preview.domain.user.entity.PlanType;
import lombok.Getter;

@Getter
public class PlanCreatedEvent extends AbstractDomainEvent {
    private final Long planId;
    private final String name;
    private final PlanType planType;

    public PlanCreatedEvent(Long planId, String name, PlanType planType) {
        super();
        this.planId = planId;
        this.name = name;
        this.planType = planType;
    }
} 