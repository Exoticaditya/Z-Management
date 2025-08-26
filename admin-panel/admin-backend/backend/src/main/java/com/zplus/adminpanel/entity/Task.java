package com.zplus.adminpanel.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * Task entity for managing project tasks and assignments
 */
@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Task title is required")
    @Size(max = 200, message = "Task title cannot exceed 200 characters")
    @Column(name = "title", nullable = false)
    private String title;

    @Size(max = 1000, message = "Task description cannot exceed 1000 characters")
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Task status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TaskStatus status = TaskStatus.TODO;

    @NotNull(message = "Task priority is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    private TaskPriority priority = TaskPriority.MEDIUM;

    @NotBlank(message = "Project ID is required")
    @Size(max = 50, message = "Project ID cannot exceed 50 characters")
    @Column(name = "project_id", nullable = false)
    private String projectId;

    @Size(max = 50, message = "Assigned to cannot exceed 50 characters")
    @Column(name = "assigned_to")
    private String assignedTo; // Employee selfId

    @Size(max = 50, message = "Created by cannot exceed 50 characters")
    @Column(name = "created_by")
    private String createdBy; // Admin/Manager selfId

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(name = "estimated_hours")
    private Integer estimatedHours;

    @Column(name = "actual_hours")
    private Integer actualHours;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    @Column(name = "notes")
    private String notes;

    @Size(max = 255, message = "Tags cannot exceed 255 characters")
    @Column(name = "tags")
    private String tags; // Comma-separated tags

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public Task() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Task(String title, String description, String projectId) {
        this();
        this.title = title;
        this.description = description;
        this.projectId = projectId;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
        if (status == TaskStatus.COMPLETED && this.completedAt == null) {
            this.completedAt = LocalDateTime.now();
        }
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

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
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

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Helper methods
    public boolean isOverdue() {
        return dueDate != null && LocalDateTime.now().isAfter(dueDate) && status != TaskStatus.COMPLETED;
    }

    public boolean isAssigned() {
        return assignedTo != null && !assignedTo.trim().isEmpty();
    }

    public boolean isCompleted() {
        return status == TaskStatus.COMPLETED;
    }

    public int getProgressPercentage() {
        switch (status) {
            case TODO:
                return 0;
            case IN_PROGRESS:
                return 50;
            case IN_REVIEW:
                return 80;
            case COMPLETED:
                return 100;
            default:
                return 0;
        }
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", status=" + status +
                ", priority=" + priority +
                ", projectId='" + projectId + '\'' +
                ", assignedTo='" + assignedTo + '\'' +
                ", dueDate=" + dueDate +
                '}';
    }
}
