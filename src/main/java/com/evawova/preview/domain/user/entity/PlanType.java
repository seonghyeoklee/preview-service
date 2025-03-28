package com.evawova.preview.domain.user.entity;

public enum PlanType {
    FREE("Free"),
    STANDARD("Standard"),
    PRO("Pro");

    private final String displayName;

    PlanType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
} 