package com.evawova.preview.domain.common.model;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public abstract class AbstractDomainEvent implements DomainEvent {
    private final String eventId;
    private final LocalDateTime occurredOn;

    protected AbstractDomainEvent() {
        this.eventId = UUID.randomUUID().toString();
        this.occurredOn = LocalDateTime.now();
    }
} 