package com.evawova.preview.domain.user.event;

import com.evawova.preview.domain.common.model.AbstractDomainEvent;
import com.evawova.preview.domain.user.entity.PlanType;
import lombok.Getter;

@Getter
public class UserCreatedEvent extends AbstractDomainEvent {
    private final Long userId;
    private final String email;
    private final String name;
    private final PlanType planType;

    public UserCreatedEvent(Long userId, String email, String name, PlanType planType) {
        super();
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.planType = planType;
    }
} 