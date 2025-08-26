package com.zplus.adminpanel.service.impl;

import com.zplus.adminpanel.dto.TaskRequest;
import com.zplus.adminpanel.dto.TaskResponse;
import com.zplus.adminpanel.entity.Task;
import com.zplus.adminpanel.entity.TaskStatus;
import com.zplus.adminpanel.entity.TaskPriority;
import com.zplus.adminpanel.repository.TaskRepository;
import com.zplus.adminpanel.service.TaskService;
import com.zplus.adminpanel.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    private static final Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);

    @Autowired
    private TaskRepository taskRepository;

    @Override
    public TaskResponse createTask(TaskRequest taskRequest, String createdBy) {
        logger.info("Creating new task: {} for project: {}", taskRequest.getTitle(), taskRequest.getProjectId());
        
        Task task = new Task();
        task.setTitle(taskRequest.getTitle());
        task.setDescription(taskRequest.getDescription());
        task.setStatus(taskRequest.getStatus() != null ? taskRequest.getStatus() : TaskStatus.TODO);
        task.setPriority(taskRequest.getPriority() != null ? taskRequest.getPriority() : TaskPriority.MEDIUM);
        task.setProjectId(taskRequest.getProjectId());
        task.setAssignedTo(taskRequest.getAssignedTo());
        task.setCreatedBy(createdBy);
        task.setDueDate(taskRequest.getDueDate());
        task.setEstimatedHours(taskRequest.getEstimatedHours());
        task.setActualHours(taskRequest.getActualHours());
        task.setNotes(taskRequest.getNotes());
        task.setTags(taskRequest.getTags());

        Task savedTask = taskRepository.save(task);
        logger.info("Task created successfully with ID: {}", savedTask.getId());
        
        return convertToResponse(savedTask);
    }

    @Override
    public TaskResponse updateTask(Long taskId, TaskRequest taskRequest) {
        logger.info("Updating task with ID: {}", taskId);
        
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));

        task.setTitle(taskRequest.getTitle());
        task.setDescription(taskRequest.getDescription());
        task.setStatus(taskRequest.getStatus());
        task.setPriority(taskRequest.getPriority());
        task.setProjectId(taskRequest.getProjectId());
        task.setAssignedTo(taskRequest.getAssignedTo());
        task.setDueDate(taskRequest.getDueDate());
        task.setEstimatedHours(taskRequest.getEstimatedHours());
        task.setActualHours(taskRequest.getActualHours());
        task.setNotes(taskRequest.getNotes());
        task.setTags(taskRequest.getTags());

        Task updatedTask = taskRepository.save(task);
        logger.info("Task updated successfully: {}", taskId);
        
        return convertToResponse(updatedTask);
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponse getTaskById(Long taskId) {
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));
        return convertToResponse(task);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getAllTasks() {
        return taskRepository.findAll().stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    @Override
    public void deleteTask(Long taskId) {
        logger.info("Deleting task with ID: {}", taskId);
        
        if (!taskRepository.existsById(taskId)) {
            throw new ResourceNotFoundException("Task not found with ID: " + taskId);
        }
        
        taskRepository.deleteById(taskId);
        logger.info("Task deleted successfully: {}", taskId);
    }

    @Override
    public TaskResponse assignTask(Long taskId, String assignedTo) {
        logger.info("Assigning task {} to user: {}", taskId, assignedTo);
        
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));

        task.setAssignedTo(assignedTo);
        
        // If task was TODO and now assigned, move to IN_PROGRESS
        if (task.getStatus() == TaskStatus.TODO) {
            task.setStatus(TaskStatus.IN_PROGRESS);
        }

        Task updatedTask = taskRepository.save(task);
        logger.info("Task assigned successfully: {} to {}", taskId, assignedTo);
        
        return convertToResponse(updatedTask);
    }

    @Override
    public TaskResponse unassignTask(Long taskId) {
        logger.info("Unassigning task with ID: {}", taskId);
        
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));

        task.setAssignedTo(null);
        
        // If task was IN_PROGRESS and now unassigned, move back to TODO
        if (task.getStatus() == TaskStatus.IN_PROGRESS) {
            task.setStatus(TaskStatus.TODO);
        }

        Task updatedTask = taskRepository.save(task);
        logger.info("Task unassigned successfully: {}", taskId);
        
        return convertToResponse(updatedTask);
    }

    @Override
    public TaskResponse updateTaskStatus(Long taskId, TaskStatus status) {
        logger.info("Updating task {} status to: {}", taskId, status);
        
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));

        task.setStatus(status);
        Task updatedTask = taskRepository.save(task);
        logger.info("Task status updated successfully: {} to {}", taskId, status);
        
        return convertToResponse(updatedTask);
    }

    @Override
    public TaskResponse markTaskComplete(Long taskId) {
        return updateTaskStatus(taskId, TaskStatus.COMPLETED);
    }

    @Override
    public TaskResponse markTaskInProgress(Long taskId) {
        return updateTaskStatus(taskId, TaskStatus.IN_PROGRESS);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksByAssignee(String assignedTo) {
        return taskRepository.findByAssignedTo(assignedTo).stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksByProject(String projectId) {
        return taskRepository.findByProjectId(projectId).stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksByStatus(TaskStatus status) {
        return taskRepository.findByStatus(status).stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksByPriority(TaskPriority priority) {
        return taskRepository.findByPriority(priority).stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getOverdueTasks() {
        return taskRepository.findOverdueTasks(LocalDateTime.now()).stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksDueSoon() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tomorrow = now.plusDays(1);
        return taskRepository.findTasksDueSoon(now, tomorrow).stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getUnassignedTasks() {
        return taskRepository.findByAssignedToIsNull().stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> searchTasks(String searchTerm) {
        return taskRepository.findTasksBySearchTerm(searchTerm).stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksByCriteria(String assignedTo, TaskStatus status, String projectId, TaskPriority priority) {
        return taskRepository.findTasksByCriteria(assignedTo, status, projectId, priority).stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getTaskStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        List<Task> allTasks = taskRepository.findAll();
        stats.put("totalTasks", allTasks.size());
        
        // Count by status
        Map<TaskStatus, Long> statusCounts = allTasks.stream()
            .collect(Collectors.groupingBy(Task::getStatus, Collectors.counting()));
        stats.put("statusCounts", statusCounts);
        
        // Count by priority
        Map<TaskPriority, Long> priorityCounts = allTasks.stream()
            .collect(Collectors.groupingBy(Task::getPriority, Collectors.counting()));
        stats.put("priorityCounts", priorityCounts);
        
        // Overdue tasks
        long overdueCount = taskRepository.findOverdueTasks(LocalDateTime.now()).size();
        stats.put("overdueCount", overdueCount);
        
        // Due soon tasks
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tomorrow = now.plusDays(1);
        long dueSoonCount = taskRepository.findTasksDueSoon(now, tomorrow).size();
        stats.put("dueSoonCount", dueSoonCount);
        
        // Unassigned tasks
        long unassignedCount = taskRepository.findByAssignedToIsNull().size();
        stats.put("unassignedCount", unassignedCount);
        
        return stats;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getUserTaskStatistics(String assignedTo) {
        Object[] stats = taskRepository.getTaskStatistics(assignedTo, LocalDateTime.now());
        
        Map<String, Object> result = new HashMap<>();
        result.put("assignedTo", assignedTo);
        result.put("completedTasks", stats[0]);
        result.put("pendingTasks", stats[1]);
        result.put("overdueTasks", stats[2]);
        result.put("timestamp", LocalDateTime.now());
        
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getProjectTaskStatistics(String projectId) {
        Object[] stats = taskRepository.getProjectTaskStatistics(projectId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("projectId", projectId);
        result.put("completedTasks", stats[0]);
        result.put("pendingTasks", stats[1]);
        result.put("totalTasks", stats[2]);
        result.put("timestamp", LocalDateTime.now());
        
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getMyTasks(String userSelfId) {
        return getTasksByAssignee(userSelfId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getMyTasksByStatus(String userSelfId, TaskStatus status) {
        return taskRepository.findByAssignedToAndStatus(userSelfId, status).stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    @Override
    public TaskResponse addTaskNote(Long taskId, String note) {
        logger.info("Adding note to task: {}", taskId);
        
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));

        String existingNotes = task.getNotes();
        String newNotes = existingNotes == null || existingNotes.isEmpty() ? 
            note : existingNotes + "\n---\n" + note;
        
        task.setNotes(newNotes);
        Task updatedTask = taskRepository.save(task);
        
        return convertToResponse(updatedTask);
    }

    @Override
    public TaskResponse updateTaskProgress(Long taskId, Integer actualHours) {
        logger.info("Updating progress for task: {} to {} hours", taskId, actualHours);
        
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));

        task.setActualHours(actualHours);
        Task updatedTask = taskRepository.save(task);
        
        return convertToResponse(updatedTask);
    }

    // Helper method to convert Task entity to TaskResponse DTO
    private TaskResponse convertToResponse(Task task) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setStatus(task.getStatus());
        response.setPriority(task.getPriority());
        response.setProjectId(task.getProjectId());
        response.setAssignedTo(task.getAssignedTo());
        response.setCreatedBy(task.getCreatedBy());
        response.setDueDate(task.getDueDate());
        response.setEstimatedHours(task.getEstimatedHours());
        response.setActualHours(task.getActualHours());
        response.setNotes(task.getNotes());
        response.setTags(task.getTags());
        response.setCompletedAt(task.getCompletedAt());
        response.setCreatedAt(task.getCreatedAt());
        response.setUpdatedAt(task.getUpdatedAt());
        response.setOverdue(task.isOverdue());
        response.setAssigned(task.isAssigned());
        response.setCompleted(task.isCompleted());
        response.setProgressPercentage(task.getProgressPercentage());
        
        return response;
    }
}
