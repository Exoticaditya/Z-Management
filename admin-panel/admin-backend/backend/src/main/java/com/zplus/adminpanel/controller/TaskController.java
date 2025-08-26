package com.zplus.adminpanel.controller;

import com.zplus.adminpanel.dto.TaskRequest;
import com.zplus.adminpanel.dto.TaskResponse;
import com.zplus.adminpanel.entity.TaskStatus;
import com.zplus.adminpanel.entity.TaskPriority;
import com.zplus.adminpanel.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TaskController {

    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    @Autowired
    private TaskService taskService;

    /**
     * Create a new task
     */
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @Valid @RequestBody TaskRequest taskRequest,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        logger.info("Creating new task: {} by user: {}", taskRequest.getTitle(), userDetails.getUsername());
        
        TaskResponse createdTask = taskService.createTask(taskRequest, userDetails.getUsername());
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }

    /**
     * Update an existing task
     */
    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long taskId,
            @Valid @RequestBody TaskRequest taskRequest) {
        
        logger.info("Updating task: {}", taskId);
        
        TaskResponse updatedTask = taskService.updateTask(taskId, taskRequest);
        return ResponseEntity.ok(updatedTask);
    }

    /**
     * Get task by ID
     */
    @GetMapping("/{taskId}")
    public ResponseEntity<TaskResponse> getTask(@PathVariable Long taskId) {
        TaskResponse task = taskService.getTaskById(taskId);
        return ResponseEntity.ok(task);
    }

    /**
     * Get all tasks
     */
    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAllTasks() {
        List<TaskResponse> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }

    /**
     * Delete a task
     */
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId) {
        logger.info("Deleting task: {}", taskId);
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Assign task to a user
     */
    @PutMapping("/{taskId}/assign")
    public ResponseEntity<TaskResponse> assignTask(
            @PathVariable Long taskId,
            @RequestParam String assignedTo) {
        
        logger.info("Assigning task {} to {}", taskId, assignedTo);
        
        TaskResponse assignedTask = taskService.assignTask(taskId, assignedTo);
        return ResponseEntity.ok(assignedTask);
    }

    /**
     * Unassign task
     */
    @PutMapping("/{taskId}/unassign")
    public ResponseEntity<TaskResponse> unassignTask(@PathVariable Long taskId) {
        logger.info("Unassigning task: {}", taskId);
        
        TaskResponse unassignedTask = taskService.unassignTask(taskId);
        return ResponseEntity.ok(unassignedTask);
    }

    /**
     * Update task status
     */
    @PutMapping("/{taskId}/status")
    public ResponseEntity<TaskResponse> updateTaskStatus(
            @PathVariable Long taskId,
            @RequestParam TaskStatus status) {
        
        logger.info("Updating task {} status to {}", taskId, status);
        
        TaskResponse updatedTask = taskService.updateTaskStatus(taskId, status);
        return ResponseEntity.ok(updatedTask);
    }

    /**
     * Mark task as complete
     */
    @PutMapping("/{taskId}/complete")
    public ResponseEntity<TaskResponse> markTaskComplete(@PathVariable Long taskId) {
        TaskResponse completedTask = taskService.markTaskComplete(taskId);
        return ResponseEntity.ok(completedTask);
    }

    /**
     * Mark task as in progress
     */
    @PutMapping("/{taskId}/in-progress")
    public ResponseEntity<TaskResponse> markTaskInProgress(@PathVariable Long taskId) {
        TaskResponse inProgressTask = taskService.markTaskInProgress(taskId);
        return ResponseEntity.ok(inProgressTask);
    }

    /**
     * Get tasks by assignee
     */
    @GetMapping("/assigned-to/{assignedTo}")
    public ResponseEntity<List<TaskResponse>> getTasksByAssignee(@PathVariable String assignedTo) {
        List<TaskResponse> tasks = taskService.getTasksByAssignee(assignedTo);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Get tasks by project
     */
    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<TaskResponse>> getTasksByProject(@PathVariable String projectId) {
        List<TaskResponse> tasks = taskService.getTasksByProject(projectId);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Get tasks by status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<TaskResponse>> getTasksByStatus(@PathVariable TaskStatus status) {
        List<TaskResponse> tasks = taskService.getTasksByStatus(status);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Get tasks by priority
     */
    @GetMapping("/priority/{priority}")
    public ResponseEntity<List<TaskResponse>> getTasksByPriority(@PathVariable TaskPriority priority) {
        List<TaskResponse> tasks = taskService.getTasksByPriority(priority);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Get overdue tasks
     */
    @GetMapping("/overdue")
    public ResponseEntity<List<TaskResponse>> getOverdueTasks() {
        List<TaskResponse> tasks = taskService.getOverdueTasks();
        return ResponseEntity.ok(tasks);
    }

    /**
     * Get tasks due soon
     */
    @GetMapping("/due-soon")
    public ResponseEntity<List<TaskResponse>> getTasksDueSoon() {
        List<TaskResponse> tasks = taskService.getTasksDueSoon();
        return ResponseEntity.ok(tasks);
    }

    /**
     * Get unassigned tasks
     */
    @GetMapping("/unassigned")
    public ResponseEntity<List<TaskResponse>> getUnassignedTasks() {
        List<TaskResponse> tasks = taskService.getUnassignedTasks();
        return ResponseEntity.ok(tasks);
    }

    /**
     * Search tasks
     */
    @GetMapping("/search")
    public ResponseEntity<List<TaskResponse>> searchTasks(@RequestParam String q) {
        List<TaskResponse> tasks = taskService.searchTasks(q);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Get tasks by criteria
     */
    @GetMapping("/filter")
    public ResponseEntity<List<TaskResponse>> getTasksByCriteria(
            @RequestParam(required = false) String assignedTo,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) String projectId,
            @RequestParam(required = false) TaskPriority priority) {
        
        List<TaskResponse> tasks = taskService.getTasksByCriteria(assignedTo, status, projectId, priority);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Get task statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getTaskStatistics() {
        Map<String, Object> stats = taskService.getTaskStatistics();
        return ResponseEntity.ok(stats);
    }

    /**
     * Get user task statistics
     */
    @GetMapping("/statistics/user/{assignedTo}")
    public ResponseEntity<Map<String, Object>> getUserTaskStatistics(@PathVariable String assignedTo) {
        Map<String, Object> stats = taskService.getUserTaskStatistics(assignedTo);
        return ResponseEntity.ok(stats);
    }

    /**
     * Get project task statistics
     */
    @GetMapping("/statistics/project/{projectId}")
    public ResponseEntity<Map<String, Object>> getProjectTaskStatistics(@PathVariable String projectId) {
        Map<String, Object> stats = taskService.getProjectTaskStatistics(projectId);
        return ResponseEntity.ok(stats);
    }

    /**
     * Get current user's tasks
     */
    @GetMapping("/my-tasks")
    public ResponseEntity<List<TaskResponse>> getMyTasks(@AuthenticationPrincipal UserDetails userDetails) {
        List<TaskResponse> tasks = taskService.getMyTasks(userDetails.getUsername());
        return ResponseEntity.ok(tasks);
    }

    /**
     * Get current user's tasks by status
     */
    @GetMapping("/my-tasks/status/{status}")
    public ResponseEntity<List<TaskResponse>> getMyTasksByStatus(
            @PathVariable TaskStatus status,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        List<TaskResponse> tasks = taskService.getMyTasksByStatus(userDetails.getUsername(), status);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Add note to task
     */
    @PutMapping("/{taskId}/notes")
    public ResponseEntity<TaskResponse> addTaskNote(
            @PathVariable Long taskId,
            @RequestParam String note) {
        
        TaskResponse updatedTask = taskService.addTaskNote(taskId, note);
        return ResponseEntity.ok(updatedTask);
    }

    /**
     * Update task progress
     */
    @PutMapping("/{taskId}/progress")
    public ResponseEntity<TaskResponse> updateTaskProgress(
            @PathVariable Long taskId,
            @RequestParam Integer actualHours) {
        
        TaskResponse updatedTask = taskService.updateTaskProgress(taskId, actualHours);
        return ResponseEntity.ok(updatedTask);
    }

    /**
     * Get task statuses enum values
     */
    @GetMapping("/statuses")
    public ResponseEntity<TaskStatus[]> getTaskStatuses() {
        return ResponseEntity.ok(TaskStatus.values());
    }

    /**
     * Get task priorities enum values
     */
    @GetMapping("/priorities")
    public ResponseEntity<TaskPriority[]> getTaskPriorities() {
        return ResponseEntity.ok(TaskPriority.values());
    }
}
