package com.zplus.adminpanel.service;

import com.zplus.adminpanel.entity.Project;
import com.zplus.adminpanel.entity.ProjectStatus;
import com.zplus.adminpanel.entity.User;
import com.zplus.adminpanel.repository.ProjectRepository;
import com.zplus.adminpanel.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;

/**
 * Service class for project-related business logic
 */
@Service
public class ProjectService {

    private static final Logger logger = LoggerFactory.getLogger(ProjectService.class);

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Create a new project with validation
     */
    public Project createProject(Project project) {
        // Validate project ID format
        if (!isValidProjectId(project.getProjectId())) {
            throw new IllegalArgumentException("Invalid project ID format. Expected format: ZP-YYYY-XXX");
        }

        // Check if project ID already exists
        if (projectRepository.existsByProjectId(project.getProjectId())) {
            throw new IllegalArgumentException("Project ID already exists: " + project.getProjectId());
        }

        // Set default values
        project.setCreatedAt(LocalDateTime.now());
        project.setUpdatedAt(LocalDateTime.now());
        project.setIsActive(true);

        // Validate dates
        if (project.getStartDate() != null && project.getEndDate() != null) {
            if (project.getStartDate().isAfter(project.getEndDate())) {
                throw new IllegalArgumentException("Start date cannot be after end date");
            }
        }

        // Validate progress percentage
        if (project.getProgressPercentage() != null) {
            if (project.getProgressPercentage() < 0 || project.getProgressPercentage() > 100) {
                throw new IllegalArgumentException("Progress percentage must be between 0 and 100");
            }
        }

        return projectRepository.save(project);
    }

    /**
     * Update an existing project
     */
    public Project updateProject(Long id, Project projectDetails) {
        Optional<Project> projectOptional = projectRepository.findById(id);
        
        if (projectOptional.isEmpty()) {
            throw new IllegalArgumentException("Project not found with id: " + id);
        }

        Project project = projectOptional.get();

        // Update fields
        if (projectDetails.getProjectName() != null) {
            project.setProjectName(projectDetails.getProjectName());
        }
        if (projectDetails.getDescription() != null) {
            project.setDescription(projectDetails.getDescription());
        }
        if (projectDetails.getStatus() != null) {
            project.setStatus(projectDetails.getStatus());
        }
        if (projectDetails.getPriority() != null) {
            project.setPriority(projectDetails.getPriority());
        }
        if (projectDetails.getStartDate() != null) {
            project.setStartDate(projectDetails.getStartDate());
        }
        if (projectDetails.getEndDate() != null) {
            project.setEndDate(projectDetails.getEndDate());
        }
        if (projectDetails.getBudget() != null) {
            project.setBudget(projectDetails.getBudget());
        }
        if (projectDetails.getActualCost() != null) {
            project.setActualCost(projectDetails.getActualCost());
        }
        if (projectDetails.getProgressPercentage() != null) {
            if (projectDetails.getProgressPercentage() < 0 || projectDetails.getProgressPercentage() > 100) {
                throw new IllegalArgumentException("Progress percentage must be between 0 and 100");
            }
            project.setProgressPercentage(projectDetails.getProgressPercentage());
        }
        if (projectDetails.getDepartment() != null) {
            project.setDepartment(projectDetails.getDepartment());
        }
        if (projectDetails.getTechnologies() != null) {
            project.setTechnologies(projectDetails.getTechnologies());
        }
        if (projectDetails.getNotes() != null) {
            project.setNotes(projectDetails.getNotes());
        }

        // Validate dates
        if (project.getStartDate() != null && project.getEndDate() != null) {
            if (project.getStartDate().isAfter(project.getEndDate())) {
                throw new IllegalArgumentException("Start date cannot be after end date");
            }
        }

        project.setUpdatedAt(LocalDateTime.now());
        return projectRepository.save(project);
    }

    /**
     * Assign team members to a project
     */
    public Project assignTeamMembers(Long projectId, List<Long> userIds) {
        Optional<Project> projectOptional = projectRepository.findById(projectId);
        
        if (projectOptional.isEmpty()) {
            throw new IllegalArgumentException("Project not found with id: " + projectId);
        }

        Project project = projectOptional.get();
        
        // Clear existing team members
        project.getTeamMembers().clear();
        
        // Add new team members
        for (Long userId : userIds) {
            Optional<User> userOptional = userRepository.findById(userId);
            if (userOptional.isPresent()) {
                project.addTeamMember(userOptional.get());
            } else {
                logger.warn("User not found with id: {}", userId);
            }
        }

        project.setUpdatedAt(LocalDateTime.now());
        return projectRepository.save(project);
    }

    /**
     * Update project progress
     */
    public Project updateProgress(Long projectId, Integer progressPercentage) {
        if (progressPercentage < 0 || progressPercentage > 100) {
            throw new IllegalArgumentException("Progress percentage must be between 0 and 100");
        }

        Optional<Project> projectOptional = projectRepository.findById(projectId);
        
        if (projectOptional.isEmpty()) {
            throw new IllegalArgumentException("Project not found with id: " + projectId);
        }

        Project project = projectOptional.get();
        project.setProgressPercentage(progressPercentage);
        
        // Auto-update status based on progress
        if (progressPercentage == 100) {
            project.setStatus(ProjectStatus.COMPLETED);
        } else if (progressPercentage > 0) {
            project.setStatus(ProjectStatus.ACTIVE);
        }

        project.setUpdatedAt(LocalDateTime.now());
        return projectRepository.save(project);
    }

    /**
     * Get project analytics
     */
    public Map<String, Object> getProjectAnalytics() {
        Map<String, Object> analytics = new HashMap<>();

        try {
            // Basic counts
            analytics.put("totalProjects", projectRepository.count());
            analytics.put("activeProjects", projectRepository.countByStatus(ProjectStatus.ACTIVE));
            analytics.put("completedProjects", projectRepository.countByStatus(ProjectStatus.COMPLETED));
            analytics.put("overdueProjects", projectRepository.findOverdueProjects(LocalDateTime.now()).size());

            // Financial analytics
            Double totalBudget = projectRepository.getTotalBudgetForCompletedProjects();
            Double totalActualCost = projectRepository.getTotalActualCostForCompletedProjects();
            
            analytics.put("totalBudget", totalBudget != null ? totalBudget : 0.0);
            analytics.put("totalActualCost", totalActualCost != null ? totalActualCost : 0.0);
            
            if (totalBudget != null && totalBudget > 0) {
                double costVariance = totalActualCost != null ? totalActualCost - totalBudget : 0.0;
                double costVariancePercentage = (costVariance / totalBudget) * 100;
                analytics.put("costVariance", costVariance);
                analytics.put("costVariancePercentage", costVariancePercentage);
            }

            // Progress analytics
            List<Project> activeProjects = projectRepository.findByStatus(ProjectStatus.ACTIVE);
            if (!activeProjects.isEmpty()) {
                double avgProgress = activeProjects.stream()
                    .mapToInt(p -> p.getProgressPercentage() != null ? p.getProgressPercentage() : 0)
                    .average()
                    .orElse(0.0);
                analytics.put("averageProgress", avgProgress);
            }

            // Department analytics
            List<Object[]> deptStats = projectRepository.getProjectCountByDepartment();
            Map<String, Long> departmentStats = new HashMap<>();
            for (Object[] stat : deptStats) {
                departmentStats.put((String) stat[0], (Long) stat[1]);
            }
            analytics.put("departmentStats", departmentStats);

        } catch (Exception e) {
            logger.error("Error calculating project analytics: ", e);
            analytics.put("error", "Failed to calculate analytics");
        }

        return analytics;
    }

    /**
     * Validate project ID format
     */
    private boolean isValidProjectId(String projectId) {
        return projectId != null && projectId.matches("^ZP-\\d{4}-\\d{3}$");
    }

    /**
     * Get projects that need attention (overdue or low progress)
     */
    public List<Project> getProjectsNeedingAttention() {
        List<Project> attentionProjects = projectRepository.findOverdueProjects(LocalDateTime.now());
        
        // Add projects with low progress
        List<Project> lowProgressProjects = projectRepository.findByProgressLessThanEqual(25);
        attentionProjects.addAll(lowProgressProjects);
        
        return attentionProjects;
    }

    /**
     * Calculate project completion rate
     */
    public double getProjectCompletionRate() {
        long totalProjects = projectRepository.count();
        long completedProjects = projectRepository.countByStatus(ProjectStatus.COMPLETED);
        
        if (totalProjects == 0) {
            return 0.0;
        }
        
        return (double) completedProjects / totalProjects * 100;
    }

    /**
     * Get projects by manager
     */
    public List<Project> getProjectsByManager(Long managerId) {
        return projectRepository.findByManagerId(managerId);
    }

    /**
     * Get projects by client
     */
    public List<Project> getProjectsByClient(Long clientId) {
        return projectRepository.findByClientId(clientId);
    }

    /**
     * Search projects
     */
    public List<Project> searchProjects(String keyword) {
        // This would typically use a more sophisticated search
        // For now, we'll use the repository method
        return projectRepository.findAll().stream()
            .filter(p -> p.getProjectName().toLowerCase().contains(keyword.toLowerCase()) ||
                        p.getProjectId().toLowerCase().contains(keyword.toLowerCase()) ||
                        (p.getDescription() != null && p.getDescription().toLowerCase().contains(keyword.toLowerCase())))
            .toList();
    }
} 