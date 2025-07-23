package com.zplus.adminpanel.controller;

import com.zplus.adminpanel.entity.Project;
import com.zplus.adminpanel.entity.ProjectStatus;
import com.zplus.adminpanel.entity.ProjectPriority;
import com.zplus.adminpanel.repository.ProjectRepository;
import com.zplus.adminpanel.service.ProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controller for managing projects
 */
@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5506", "http://localhost:8080", "http://127.0.0.1:5507"})
public class ProjectController {

    private static final Logger logger = LoggerFactory.getLogger(ProjectController.class);

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectService projectService;

    /**
     * Get all projects with pagination and filtering
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllProjects(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) ProjectStatus status,
            @RequestParam(required = false) ProjectPriority priority,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String search) {
        
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            
            Pageable pageable = PageRequest.of(page, size, sort);
            
            Page<Project> projects;
            
            if (status != null || priority != null || department != null || search != null) {
                projects = projectRepository.findProjectsWithFilters(status, priority, department, search, pageable);
            } else {
                projects = projectRepository.findAll(pageable);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("content", projects.getContent());
            response.put("currentPage", projects.getNumber());
            response.put("totalItems", projects.getTotalElements());
            response.put("totalPages", projects.getTotalPages());
            response.put("size", projects.getSize());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching projects: ", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to fetch projects"));
        }
    }

    /**
     * Get project by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable Long id) {
        try {
            Optional<Project> project = projectRepository.findById(id);
            return project.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            logger.error("Error fetching project with id {}: ", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get project by project ID
     */
    @GetMapping("/project-id/{projectId}")
    public ResponseEntity<Project> getProjectByProjectId(@PathVariable String projectId) {
        try {
            Optional<Project> project = projectRepository.findByProjectId(projectId);
            return project.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            logger.error("Error fetching project with projectId {}: ", projectId, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Create a new project
     */
    @PostMapping
    public ResponseEntity<Project> createProject(@Valid @RequestBody Project project) {
        try {
            project.setCreatedAt(LocalDateTime.now());
            project.setUpdatedAt(LocalDateTime.now());
            project.setIsActive(true);
            
            Project savedProject = projectRepository.save(project);
            logger.info("Created new project: {}", savedProject.getProjectId());
            
            return ResponseEntity.ok(savedProject);
        } catch (Exception e) {
            logger.error("Error creating project: ", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Update an existing project
     */
    @PutMapping("/{id}")
    public ResponseEntity<Project> updateProject(@PathVariable Long id, @Valid @RequestBody Project projectDetails) {
        try {
            Optional<Project> projectOptional = projectRepository.findById(id);
            
            if (projectOptional.isPresent()) {
                Project project = projectOptional.get();
                
                project.setProjectName(projectDetails.getProjectName());
                project.setDescription(projectDetails.getDescription());
                project.setStatus(projectDetails.getStatus());
                project.setPriority(projectDetails.getPriority());
                project.setStartDate(projectDetails.getStartDate());
                project.setEndDate(projectDetails.getEndDate());
                project.setBudget(projectDetails.getBudget());
                project.setActualCost(projectDetails.getActualCost());
                project.setProgressPercentage(projectDetails.getProgressPercentage());
                project.setDepartment(projectDetails.getDepartment());
                project.setTechnologies(projectDetails.getTechnologies());
                project.setNotes(projectDetails.getNotes());
                project.setUpdatedAt(LocalDateTime.now());
                
                Project updatedProject = projectRepository.save(project);
                logger.info("Updated project: {}", updatedProject.getProjectId());
                
                return ResponseEntity.ok(updatedProject);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error updating project with id {}: ", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Delete a project
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteProject(@PathVariable Long id) {
        try {
            Optional<Project> projectOptional = projectRepository.findById(id);
            
            if (projectOptional.isPresent()) {
                Project project = projectOptional.get();
                project.setIsActive(false);
                project.setUpdatedAt(LocalDateTime.now());
                projectRepository.save(project);
                
                logger.info("Soft deleted project: {}", project.getProjectId());
                
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Project deleted successfully");
                response.put("projectId", project.getProjectId());
                
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error deleting project with id {}: ", id, e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to delete project"));
        }
    }

    /**
     * Get projects by status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Project>> getProjectsByStatus(@PathVariable ProjectStatus status) {
        try {
            List<Project> projects = projectRepository.findByStatus(status);
            return ResponseEntity.ok(projects);
        } catch (Exception e) {
            logger.error("Error fetching projects by status {}: ", status, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get projects by priority
     */
    @GetMapping("/priority/{priority}")
    public ResponseEntity<List<Project>> getProjectsByPriority(@PathVariable ProjectPriority priority) {
        try {
            List<Project> projects = projectRepository.findByPriority(priority);
            return ResponseEntity.ok(projects);
        } catch (Exception e) {
            logger.error("Error fetching projects by priority {}: ", priority, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get projects by department
     */
    @GetMapping("/department/{department}")
    public ResponseEntity<List<Project>> getProjectsByDepartment(@PathVariable String department) {
        try {
            List<Project> projects = projectRepository.findByDepartment(department);
            return ResponseEntity.ok(projects);
        } catch (Exception e) {
            logger.error("Error fetching projects by department {}: ", department, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get active projects
     */
    @GetMapping("/active")
    public ResponseEntity<List<Project>> getActiveProjects() {
        try {
            List<Project> projects = projectRepository.findAllActiveProjects();
            return ResponseEntity.ok(projects);
        } catch (Exception e) {
            logger.error("Error fetching active projects: ", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get overdue projects
     */
    @GetMapping("/overdue")
    public ResponseEntity<List<Project>> getOverdueProjects() {
        try {
            List<Project> projects = projectRepository.findOverdueProjects(LocalDateTime.now());
            return ResponseEntity.ok(projects);
        } catch (Exception e) {
            logger.error("Error fetching overdue projects: ", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get project statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getProjectStatistics() {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            stats.put("totalProjects", projectRepository.count());
            stats.put("activeProjects", projectRepository.countByStatus(ProjectStatus.ACTIVE));
            stats.put("completedProjects", projectRepository.countByStatus(ProjectStatus.COMPLETED));
            stats.put("overdueProjects", projectRepository.findOverdueProjects(LocalDateTime.now()).size());
            
            // Department-wise statistics
            List<Object[]> deptStats = projectRepository.getProjectCountByDepartment();
            Map<String, Long> departmentStats = new HashMap<>();
            for (Object[] stat : deptStats) {
                departmentStats.put((String) stat[0], (Long) stat[1]);
            }
            stats.put("departmentStats", departmentStats);
            
            // Status-wise statistics
            List<Object[]> statusStats = projectRepository.getProjectCountByStatus();
            Map<String, Long> projectStatusStats = new HashMap<>();
            for (Object[] stat : statusStats) {
                projectStatusStats.put(((ProjectStatus) stat[0]).getDisplayName(), (Long) stat[1]);
            }
            stats.put("statusStats", projectStatusStats);
            
            // Priority-wise statistics
            List<Object[]> priorityStats = projectRepository.getProjectCountByPriority();
            Map<String, Long> projectPriorityStats = new HashMap<>();
            for (Object[] stat : priorityStats) {
                projectPriorityStats.put(((ProjectPriority) stat[0]).getDisplayName(), (Long) stat[1]);
            }
            stats.put("priorityStats", projectPriorityStats);
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            logger.error("Error fetching project statistics: ", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to fetch project statistics"));
        }
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Project Service");
        response.put("timestamp", LocalDateTime.now());
        response.put("totalProjects", projectRepository.count());
        
        return ResponseEntity.ok(response);
    }

	public ProjectService getProjectService() {
		return projectService;
	}

	public void setProjectService(ProjectService projectService) {
		this.projectService = projectService;
	}
} 