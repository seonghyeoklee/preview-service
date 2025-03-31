package com.evawova.preview.domain.app.entity;

public enum ServiceStatus {
    NORMAL("정상"),
    MAINTENANCE("점검 중"),
    PARTIAL_DISRUPTION("일부 기능 제한"),
    DISRUPTION("서비스 중단"),
    EMERGENCY("긴급 상황");
    
    private final String description;
    
    ServiceStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
} 