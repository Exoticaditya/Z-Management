package com.zplus.adminpanel.entity;

/**
 * Enum representing registration status
 */
public enum RegistrationStatus {
    PENDING("PENDING", "Registration is pending admin approval"),
    APPROVED("APPROVED", "Registration has been approved"),
    REJECTED("REJECTED", "Registration has been rejected"),
    SUSPENDED("SUSPENDED", "User account is temporarily suspended"),
    DEACTIVATED("DEACTIVATED", "User account has been deactivated");

    private final String displayName;
    private final String description;

    RegistrationStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
