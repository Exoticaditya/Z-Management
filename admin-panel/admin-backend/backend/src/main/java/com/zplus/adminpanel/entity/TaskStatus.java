package com.zplus.adminpanel.entity;

/**
 * Enum representing task status
 */
public enum TaskStatus {
    TODO("TODO", "Task is pending and not started yet"),
    IN_PROGRESS("IN_PROGRESS", "Task is currently being worked on"),
    IN_REVIEW("IN_REVIEW", "Task is completed and under review"),
    COMPLETED("COMPLETED", "Task has been completed successfully"),
    CANCELLED("CANCELLED", "Task has been cancelled"),
    ON_HOLD("ON_HOLD", "Task is temporarily paused");

    private final String displayName;
    private final String description;

    TaskStatus(String displayName, String description) {
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
