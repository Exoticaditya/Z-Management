package com.zplus.adminpanel.service;

import com.zplus.adminpanel.dto.TaskRequest;
import com.zplus.adminpanel.dto.TaskResponse;
import com.zplus.adminpanel.entity.Task;
import com.zplus.adminpanel.entity.TaskStatus;
import com.zplus.adminpanel.entity.TaskPriority;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface TaskService {
    
    // CRUD operations
    TaskResponse createTask(TaskRequest taskRequest, String createdBy);
    
    TaskResponse updateTask(Long taskId, TaskRequest taskRequest);
    
    TaskResponse getTaskById(Long taskId);
    
    List<TaskResponse> getAllTasks();
    
    void deleteTask(Long taskId);
    
    // Assignment operations
    TaskResponse assignTask(Long taskId, String assignedTo);
    
    TaskResponse unassignTask(Long taskId);
    
    // Status operations
    TaskResponse updateTaskStatus(Long taskId, TaskStatus status);
    
    TaskResponse markTaskComplete(Long taskId);
    
    TaskResponse markTaskInProgress(Long taskId);
    
    // Query operations
    List<TaskResponse> getTasksByAssignee(String assignedTo);
    
    List<TaskResponse> getTasksByProject(String projectId);
    
    List<TaskResponse> getTasksByStatus(TaskStatus status);
    
    List<TaskResponse> getTasksByPriority(TaskPriority priority);
    
    List<TaskResponse> getOverdueTasks();
    
    List<TaskResponse> getTasksDueSoon();
    
    List<TaskResponse> getUnassignedTasks();
    
    List<TaskResponse> searchTasks(String searchTerm);
    
    // Filter operations
    List<TaskResponse> getTasksByCriteria(String assignedTo, TaskStatus status, String projectId, TaskPriority priority);
    
    // Statistics operations
    Map<String, Object> getTaskStatistics();
    
    Map<String, Object> getUserTaskStatistics(String assignedTo);
    
    Map<String, Object> getProjectTaskStatistics(String projectId);
    
    // Utility operations
    List<TaskResponse> getMyTasks(String userSelfId);
    
    List<TaskResponse> getMyTasksByStatus(String userSelfId, TaskStatus status);
    
    TaskResponse addTaskNote(Long taskId, String note);
    
    TaskResponse updateTaskProgress(Long taskId, Integer actualHours);
}
