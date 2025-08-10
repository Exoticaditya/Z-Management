package com.zplus.adminpanel.controller;

import com.zplus.adminpanel.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = {
    "http://localhost:3000",
    "http://localhost:3001", 
    "http://localhost:8080",
    "http://127.0.0.1:5509",
    "http://127.0.0.1:5508",
    "http://127.0.0.1:5507",
    "https://zpluse.com",
    "https://www.zpluse.com",
    "http://zpluse.com",
    "http://www.zpluse.com"
})
public class DashboardController {

    private final UserService userService;

    public DashboardController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        try {
            Map<String, Object> stats = userService.getRegistrationStatistics();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            Map<String, Object> error = Map.of(
                "error", e.getMessage(),
                "message", "Failed to load dashboard stats"
            );
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @GetMapping("/stats/public")
    public ResponseEntity<Map<String, Object>> getDashboardStatsPublic() {
        try {
            Map<String, Object> stats = userService.getRegistrationStatistics();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            Map<String, Object> error = Map.of(
                "error", e.getMessage(),
                "message", "Failed to load dashboard stats"
            );
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @GetMapping("/recent-activity")
    public ResponseEntity<List<Map<String, Object>>> getRecentActivity() {
        try {
            // Get actual recent activity from the database
            List<Map<String, Object>> activity = userService.getRecentActivity();
            return ResponseEntity.ok(activity);
        } catch (Exception e) {
            // Fallback with placeholder data
            List<Map<String, Object>> activity = List.of(
                Map.of("user", "System", "action", "initialized", "time", "recently")
            );
            return ResponseEntity.ok(activity);
        }
    }

    // Role-specific dashboard endpoints
    
    @GetMapping("/admin")
    public ResponseEntity<Map<String, Object>> getAdminDashboard() {
        try {
            Map<String, Object> dashboard = userService.getAdminDashboard();
            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            Map<String, Object> error = Map.of(
                "error", e.getMessage(),
                "message", "Failed to load admin dashboard"
            );
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @GetMapping("/employee")
    public ResponseEntity<Map<String, Object>> getEmployeeDashboard() {
        try {
            Map<String, Object> dashboard = userService.getEmployeeDashboard();
            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            Map<String, Object> error = Map.of(
                "error", e.getMessage(),
                "message", "Failed to load employee dashboard"
            );
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @GetMapping("/client")
    public ResponseEntity<Map<String, Object>> getClientDashboard() {
        try {
            Map<String, Object> dashboard = userService.getClientDashboard();
            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            Map<String, Object> error = Map.of(
                "error", e.getMessage(),
                "message", "Failed to load client dashboard"
            );
            return ResponseEntity.internalServerError().body(error);
        }
    }

    // Public testing endpoints for all roles
    @GetMapping("/admin/public")
    public ResponseEntity<Map<String, Object>> getAdminDashboardPublic() {
        try {
            Map<String, Object> dashboard = userService.getAdminDashboard();
            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            Map<String, Object> error = Map.of(
                "error", e.getMessage(),
                "message", "Failed to load admin dashboard"
            );
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @GetMapping("/employee/public")
    public ResponseEntity<Map<String, Object>> getEmployeeDashboardPublic() {
        try {
            Map<String, Object> dashboard = userService.getEmployeeDashboard();
            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            Map<String, Object> error = Map.of(
                "error", e.getMessage(),
                "message", "Failed to load employee dashboard"
            );
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @GetMapping("/client/public")
    public ResponseEntity<Map<String, Object>> getClientDashboardPublic() {
        try {
            Map<String, Object> dashboard = userService.getClientDashboard();
            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            Map<String, Object> error = Map.of(
                "error", e.getMessage(),
                "message", "Failed to load client dashboard"
            );
            return ResponseEntity.internalServerError().body(error);
        }
    }
}
