package com.zplus.adminpanel.repository;

import com.zplus.adminpanel.entity.Task;
import com.zplus.adminpanel.entity.TaskStatus;
import com.zplus.adminpanel.entity.TaskPriority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // Find tasks by assignment
    List<Task> findByAssignedTo(String assignedTo);
    
    List<Task> findByAssignedToAndStatus(String assignedTo, TaskStatus status);
    
    // Find tasks by project
    List<Task> findByProjectId(String projectId);
    
    List<Task> findByProjectIdAndStatus(String projectId, TaskStatus status);
    
    // Find tasks by status
    List<Task> findByStatus(TaskStatus status);
    
    // Find tasks by priority
    List<Task> findByPriority(TaskPriority priority);
    
    // Find tasks by creator
    List<Task> findByCreatedBy(String createdBy);
    
    // Find overdue tasks
    @Query("SELECT t FROM Task t WHERE t.dueDate < :now AND t.status != 'COMPLETED'")
    List<Task> findOverdueTasks(@Param("now") LocalDateTime now);
    
    // Find tasks due soon (within next 24 hours)
    @Query("SELECT t FROM Task t WHERE t.dueDate BETWEEN :now AND :tomorrow AND t.status != 'COMPLETED'")
    List<Task> findTasksDueSoon(@Param("now") LocalDateTime now, @Param("tomorrow") LocalDateTime tomorrow);
    
    // Find unassigned tasks
    List<Task> findByAssignedToIsNull();
    
    // Find tasks by multiple criteria
    @Query("SELECT t FROM Task t WHERE " +
           "(:assignedTo IS NULL OR t.assignedTo = :assignedTo) AND " +
           "(:status IS NULL OR t.status = :status) AND " +
           "(:projectId IS NULL OR t.projectId = :projectId) AND " +
           "(:priority IS NULL OR t.priority = :priority)")
    List<Task> findTasksByCriteria(
        @Param("assignedTo") String assignedTo,
        @Param("status") TaskStatus status,
        @Param("projectId") String projectId,
        @Param("priority") TaskPriority priority
    );
    
    // Count tasks by status
    @Query("SELECT t.status, COUNT(t) FROM Task t GROUP BY t.status")
    List<Object[]> countTasksByStatus();
    
    // Count tasks by assigned user
    @Query("SELECT t.assignedTo, COUNT(t) FROM Task t WHERE t.assignedTo IS NOT NULL GROUP BY t.assignedTo")
    List<Object[]> countTasksByAssignee();
    
    // Find tasks with search term in title or description
    @Query("SELECT t FROM Task t WHERE " +
           "LOWER(t.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(t.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Task> findTasksBySearchTerm(@Param("searchTerm") String searchTerm);
    
    // Get task statistics for a user
    @Query("SELECT " +
           "COUNT(CASE WHEN t.status = 'COMPLETED' THEN 1 END) as completed, " +
           "COUNT(CASE WHEN t.status != 'COMPLETED' THEN 1 END) as pending, " +
           "COUNT(CASE WHEN t.dueDate < :now AND t.status != 'COMPLETED' THEN 1 END) as overdue " +
           "FROM Task t WHERE t.assignedTo = :assignedTo")
    Object[] getTaskStatistics(@Param("assignedTo") String assignedTo, @Param("now") LocalDateTime now);
    
    // Get project task statistics
    @Query("SELECT " +
           "COUNT(CASE WHEN t.status = 'COMPLETED' THEN 1 END) as completed, " +
           "COUNT(CASE WHEN t.status != 'COMPLETED' THEN 1 END) as pending, " +
           "COUNT(t) as total " +
           "FROM Task t WHERE t.projectId = :projectId")
    Object[] getProjectTaskStatistics(@Param("projectId") String projectId);
}
