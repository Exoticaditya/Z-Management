package com.zplus.adminpanel.controller;

import com.zplus.adminpanel.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller for handling admin notifications and dashboard data
 */
@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = {
    "http://localhost:3000",
    "http://localhost:8080",
    "https://zpluse.com",
    "https://www.zpluse.com",
    "http://zpluse.com",
    "http://www.zpluse.com"
})
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    /**
     * Get admin notifications
     */
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getAdminNotifications() {
        Map<String, Object> notifications = notificationService.getAdminNotifications();
        return ResponseEntity.ok(notifications);
    }

    /**
     * Get dashboard statistics
     */
    @GetMapping("/dashboard/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = notificationService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }

    /**
     * Get recent activity
     */
    @GetMapping("/recent-activity")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getRecentActivity() {
        Map<String, Object> activity = notificationService.getRecentActivity();
        return ResponseEntity.ok(activity);
    }
}
