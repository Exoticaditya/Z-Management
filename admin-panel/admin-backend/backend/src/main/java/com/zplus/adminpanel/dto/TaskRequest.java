package com.zplus.adminpanel.dto;

import com.zplus.adminpanel.entity.TaskStatus;
import com.zplus.adminpanel.entity.TaskPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * DTO for creating and updating tasks
 */
public class TaskRequest {

    @NotBlank(message = "Task title is required")
    @Size(max = 200, message = "Task title cannot exceed 200 characters")
    private String title;

    @Size(max = 1000, message = "Task description cannot exceed 1000 characters")
    private String description;

    @NotNull(message = "Task status is required")
    private TaskStatus status;

    @NotNull(message = "Task priority is required")
    private TaskPriority priority;

    @NotBlank(message = "Project ID is required")
    @Size(max = 50, message = "Project ID cannot exceed 50 characters")
    private String projectId;

    @Size(max = 50, message = "Assigned to cannot exceed 50 characters")
    private String assignedTo;

    private LocalDateTime dueDate;

    private Integer estimatedHours;

    private Integer actualHours;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;

    @Size(max = 255, message = "Tags cannot exceed 255 characters")
    private String tags;

    // Constructors
    public TaskRequest() {}

    public TaskRequest(String title, String description, String projectId) {
        this.title = title;
        this.description = description;
        this.projectId = projectId;
        this.status = TaskStatus.TODO;
        this.priority = TaskPriority.MEDIUM;
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public Integer getEstimatedHours() {
        return estimatedHours;
    }

    public void setEstimatedHours(Integer estimatedHours) {
        this.estimatedHours = estimatedHours;
    }

    public Integer getActualHours() {
        return actualHours;
    }

    public void setActualHours(Integer actualHours) {
        this.actualHours = actualHours;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "TaskRequest{" +
                "title='" + title + '\'' +
                ", projectId='" + projectId + '\'' +
                ", status=" + status +
                ", priority=" + priority +
                ", assignedTo='" + assignedTo + '\'' +
                '}';
    }
}
