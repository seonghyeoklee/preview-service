package com.evawova.preview.domain.common.model;

import com.evawova.preview.common.entity.BaseTimeEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 도메인 애그리거트 루트 추상 클래스
 * 도메인 이벤트를 발행할 수 있는 엔티티의 기본 클래스
 * @param <ID> 엔티티 식별자 타입
 */
public abstract class AggregateRoot<ID> extends BaseTimeEntity {
    
    private transient final List<DomainEvent> domainEvents = new ArrayList<>();
    
    /**
     * 엔티티 ID 반환
     * @return 엔티티 ID
     */
    public abstract ID getId();
    
    /**
     * 도메인 이벤트 등록
     * @param event 도메인 이벤트
     */
    protected void registerEvent(DomainEvent event) {
        domainEvents.add(event);
    }
    
    /**
     * 등록된 모든 도메인 이벤트 반환
     * @return 등록된 모든 도메인 이벤트
     */
    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }
    
    /**
     * 등록된 도메인 이벤트 초기화
     * 이벤트 처리 후 호출해야 함
     */
    public void clearEvents() {
        domainEvents.clear();
    }
} 