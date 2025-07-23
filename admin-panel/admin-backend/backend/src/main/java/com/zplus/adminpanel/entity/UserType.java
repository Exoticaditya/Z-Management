package com.zplus.adminpanel.entity;

/**
 * Enum representing different user types in the system
 */
public enum UserType {
    ADMIN("ADMIN", "Full system control and management"),
    EMPLOYEE("EMPLOYEE", "Task management and project work"),
    CLIENT("CLIENT", "Progress reports and project updates");

    private final String displayName;
    private final String description;

    UserType(String displayName, String description) {
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
