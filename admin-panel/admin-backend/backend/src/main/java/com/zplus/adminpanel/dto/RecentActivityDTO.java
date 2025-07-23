package com.zplus.adminpanel.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Recent Activity Data Transfer Object")
public class RecentActivityDTO {

    @Schema(description = "Activity ID")
    @JsonProperty("id")
    private Long id;

    @Schema(description = "Activity type")
    @JsonProperty("type")
    private String type;

    @Schema(description = "Activity description")
    @JsonProperty("description")
    private String description;

    @Schema(description = "User who performed the activity")
    @JsonProperty("userName")
    private String userName;

    @Schema(description = "User ID who performed the activity")
    @JsonProperty("userId")
    private Long userId;

    @Schema(description = "Activity timestamp")
    @JsonProperty("timestamp")
    private LocalDateTime timestamp;

    @Schema(description = "Activity icon")
    @JsonProperty("icon")
    private String icon;

    @Schema(description = "Activity status")
    @JsonProperty("status")
    private String status;

    @Schema(description = "Related entity ID")
    @JsonProperty("entityId")
    private Long entityId;

    @Schema(description = "Related entity type")
    @JsonProperty("entityType")
    private String entityType;

    // Default constructor
    public RecentActivityDTO() {}

    // Constructor with essential fields
    public RecentActivityDTO(Long id, String type, String description, String userName, 
                           LocalDateTime timestamp, String icon) {
        this.id = id;
        this.type = type;
        this.description = description;
        this.userName = userName;
        this.timestamp = timestamp;
        this.icon = icon;
    }

    // Constructor with all fields
    public RecentActivityDTO(Long id, String type, String description, String userName, 
                           Long userId, LocalDateTime timestamp, String icon, String status,
                           Long entityId, String entityType) {
        this.id = id;
        this.type = type;
        this.description = description;
        this.userName = userName;
        this.userId = userId;
        this.timestamp = timestamp;
        this.icon = icon;
        this.status = status;
        this.entityId = entityId;
        this.entityType = entityType;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    @Override
    public String toString() {
        return "RecentActivityDTO{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", description='" + description + '\'' +
                ", userName='" + userName + '\'' +
                ", userId=" + userId +
                ", timestamp=" + timestamp +
                ", icon='" + icon + '\'' +
                ", status='" + status + '\'' +
                ", entityId=" + entityId +
                ", entityType='" + entityType + '\'' +
                '}';
    }
} 