package com.zplus.adminpanel.entity;

/**
 * Enum representing the status of contact inquiries
 */
public enum ContactStatus {
    NEW("New", "New inquiry received"),
    PENDING("Pending", "Inquiry pending review"),
    IN_PROGRESS("In Progress", "Inquiry is being processed"),
    CONTACTED("Contacted", "Customer has been contacted"),
    QUALIFIED("Qualified", "Inquiry qualified as potential business"),
    NOT_INTERESTED("Not Interested", "Customer is not interested"),
    RESOLVED("Resolved", "Inquiry has been resolved"),
    CONVERTED("Converted", "Inquiry converted to business"),
    CLOSED("Closed", "Inquiry closed");

    private final String displayName;
    private final String description;

    ContactStatus(String displayName, String description) {
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