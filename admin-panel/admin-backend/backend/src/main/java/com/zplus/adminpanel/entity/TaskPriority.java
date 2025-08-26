package com.zplus.adminpanel.entity;

/**
 * Enum representing task priority levels
 */
public enum TaskPriority {
    LOW("LOW", "Low priority task", 1),
    MEDIUM("MEDIUM", "Medium priority task", 2),
    HIGH("HIGH", "High priority task", 3),
    URGENT("URGENT", "Urgent task requiring immediate attention", 4),
    CRITICAL("CRITICAL", "Critical task that blocks other work", 5);

    private final String displayName;
    private final String description;
    private final int level;

    TaskPriority(String displayName, String description, int level) {
        this.displayName = displayName;
        this.description = description;
        this.level = level;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public int getLevel() {
        return level;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
