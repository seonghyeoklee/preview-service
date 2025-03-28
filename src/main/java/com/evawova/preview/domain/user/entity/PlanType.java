package com.evawova.preview.domain.user.entity;

public enum PlanType {
    FREE("Free"),
    PREMIUM("Premium"),
    ENTERPRISE("Enterprise");

    private final String displayName;

    PlanType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
} 