package com.evawova.preview.infrastructure.events;

import com.evawova.preview.domain.common.model.DomainEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DomainEventPublisher {
    
    private final ApplicationEventPublisher applicationEventPublisher;
    
    public void publish(DomainEvent event) {
        applicationEventPublisher.publishEvent(event);
    }
} 