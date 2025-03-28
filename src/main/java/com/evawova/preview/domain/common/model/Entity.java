package com.evawova.preview.domain.common.model;

import lombok.Getter;

import java.util.Objects;

@Getter
public abstract class Entity<ID> {
    
    public abstract ID getId();
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Entity<?> entity = (Entity<?>) o;
        return Objects.equals(getId(), entity.getId());
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
} 