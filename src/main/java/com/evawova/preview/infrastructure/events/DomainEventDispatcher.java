package com.evawova.preview.infrastructure.events;

import com.evawova.preview.domain.common.model.AggregateRoot;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Configuration
@EnableAsync
@RequiredArgsConstructor
public class DomainEventDispatcher {

    private final DomainEventPublisher eventPublisher;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public <T> void dispatch(AggregateRoot<T> aggregateRoot) {
        aggregateRoot.getDomainEvents().forEach(eventPublisher::publish);
        aggregateRoot.clearEvents();
    }
} 