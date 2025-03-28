package com.evawova.preview.domain.common.model;

import java.time.LocalDateTime;

public interface DomainEvent {
    LocalDateTime getOccurredOn();
} 