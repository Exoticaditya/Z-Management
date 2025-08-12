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
        Map<String, Object> stats = userService.getRegistrationStatistics();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/recent-activity")
    public ResponseEntity<List<Map<String, Object>>> getRecentActivity() {
        // Placeholder for recent activity logic
        List<Map<String, Object>> activity = List.of(
            Map.of("user", "John Doe", "action", "registered", "time", "2 hours ago"),
            Map.of("user", "Jane Smith", "action", "updated profile", "time", "5 hours ago")
        );
        return ResponseEntity.ok(activity);
    }
}
