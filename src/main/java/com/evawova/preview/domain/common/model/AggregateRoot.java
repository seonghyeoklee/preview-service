package com.evawova.preview.domain.common.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public abstract class AggregateRoot<ID> extends Entity<ID> {

    private transient final List<DomainEvent> domainEvents = new ArrayList<>();

    protected void registerEvent(DomainEvent event) {
        this.domainEvents.add(event);
    }

    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    public void clearEvents() {
        this.domainEvents.clear();
    }
} 