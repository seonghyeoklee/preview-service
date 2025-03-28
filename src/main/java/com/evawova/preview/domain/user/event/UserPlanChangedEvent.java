package com.evawova.preview.domain.user.event;

import com.evawova.preview.domain.common.model.AbstractDomainEvent;
import com.evawova.preview.domain.user.entity.PlanType;
import lombok.Getter;

@Getter
public class UserPlanChangedEvent extends AbstractDomainEvent {
    private final Long userId;
    private final String email;
    private final PlanType oldPlanType;
    private final PlanType newPlanType;

    public UserPlanChangedEvent(Long userId, String email, PlanType oldPlanType, PlanType newPlanType) {
        super();
        this.userId = userId;
        this.email = email;
        this.oldPlanType = oldPlanType;
        this.newPlanType = newPlanType;
    }
} 