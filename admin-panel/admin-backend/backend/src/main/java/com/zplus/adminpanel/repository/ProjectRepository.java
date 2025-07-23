package com.zplus.adminpanel.repository;

import com.zplus.adminpanel.entity.Project;
import com.zplus.adminpanel.entity.ProjectStatus;
import com.zplus.adminpanel.entity.ProjectPriority;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Project entity operations
 */
@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    // Find operations
    Optional<Project> findByProjectId(String projectId);
    
    List<Project> findByStatus(ProjectStatus status);
    
    List<Project> findByPriority(ProjectPriority priority);
    
    List<Project> findByDepartment(String department);
    
    List<Project> findByManagerId(Long managerId);
    
    List<Project> findByClientId(Long clientId);
    
    Page<Project> findByStatus(ProjectStatus status, Pageable pageable);
    
    Page<Project> findByPriority(ProjectPriority priority, Pageable pageable);
    
    Page<Project> findByDepartment(String department, Pageable pageable);

    // Count operations
    long countByStatus(ProjectStatus status);
    
    long countByPriority(ProjectPriority priority);
    
    long countByDepartment(String department);

    // Existence checks
    boolean existsByProjectId(String projectId);

    // Custom queries
    @Query("SELECT p FROM Project p WHERE p.projectName LIKE %:keyword% OR p.projectId LIKE %:keyword% OR p.description LIKE %:keyword%")
    Page<Project> searchProjects(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT p FROM Project p WHERE p.status = :status AND p.priority = :priority")
    List<Project> findByStatusAndPriority(@Param("status") ProjectStatus status, @Param("priority") ProjectPriority priority);

    @Query("SELECT p FROM Project p WHERE p.startDate BETWEEN :startDate AND :endDate")
    List<Project> findByStartDateBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT p FROM Project p WHERE p.endDate BETWEEN :startDate AND :endDate")
    List<Project> findByEndDateBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT p FROM Project p WHERE p.isActive = true AND p.status = 'ACTIVE'")
    List<Project> findAllActiveProjects();

    @Query("SELECT p FROM Project p WHERE p.progressPercentage >= :minProgress")
    List<Project> findByProgressGreaterThanEqual(@Param("minProgress") Integer minProgress);

    @Query("SELECT p FROM Project p WHERE p.progressPercentage <= :maxProgress")
    List<Project> findByProgressLessThanEqual(@Param("maxProgress") Integer maxProgress);

    // Advanced search
    @Query("SELECT p FROM Project p WHERE " +
           "(:status IS NULL OR p.status = :status) AND " +
           "(:priority IS NULL OR p.priority = :priority) AND " +
           "(:department IS NULL OR p.department = :department) AND " +
           "(:search IS NULL OR p.projectName LIKE %:search% OR p.projectId LIKE %:search%)")
    Page<Project> findProjectsWithFilters(
            @Param("status") ProjectStatus status,
            @Param("priority") ProjectPriority priority,
            @Param("department") String department,
            @Param("search") String search,
            Pageable pageable);

    // Recent projects
    @Query("SELECT p FROM Project p ORDER BY p.createdAt DESC")
    List<Project> findTop10ByOrderByCreatedAtDesc();

    // Overdue projects
    @Query("SELECT p FROM Project p WHERE p.endDate < :now AND p.status != 'COMPLETED'")
    List<Project> findOverdueProjects(@Param("now") LocalDateTime now);

    // Projects by progress range
    @Query("SELECT p FROM Project p WHERE p.progressPercentage BETWEEN :minProgress AND :maxProgress")
    List<Project> findByProgressBetween(@Param("minProgress") Integer minProgress, @Param("maxProgress") Integer maxProgress);

    // Department-wise queries
    @Query("SELECT DISTINCT p.department FROM Project p WHERE p.isActive = true")
    List<String> findAllActiveDepartments();

    @Query("SELECT p FROM Project p WHERE p.department = :department AND p.isActive = true")
    List<Project> findActiveProjectsByDepartment(@Param("department") String department);

    // Budget queries
    @Query("SELECT p FROM Project p WHERE p.budget >= :minBudget")
    List<Project> findByBudgetGreaterThanEqual(@Param("minBudget") Double minBudget);

    @Query("SELECT p FROM Project p WHERE p.budget <= :maxBudget")
    List<Project> findByBudgetLessThanEqual(@Param("maxBudget") Double maxBudget);

    // Team member queries
    @Query("SELECT p FROM Project p JOIN p.teamMembers tm WHERE tm.id = :userId")
    List<Project> findProjectsByTeamMember(@Param("userId") Long userId);

    // Statistics queries
    @Query("SELECT p.department, COUNT(p) FROM Project p WHERE p.status = 'ACTIVE' GROUP BY p.department")
    List<Object[]> getProjectCountByDepartment();

    @Query("SELECT p.status, COUNT(p) FROM Project p GROUP BY p.status")
    List<Object[]> getProjectCountByStatus();

    @Query("SELECT p.priority, COUNT(p) FROM Project p GROUP BY p.priority")
    List<Object[]> getProjectCountByPriority();

    // Revenue queries
    @Query("SELECT SUM(p.budget) FROM Project p WHERE p.status = 'COMPLETED'")
    Double getTotalBudgetForCompletedProjects();

    @Query("SELECT SUM(p.actualCost) FROM Project p WHERE p.status = 'COMPLETED'")
    Double getTotalActualCostForCompletedProjects();
} 