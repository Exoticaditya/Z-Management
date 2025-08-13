package com.zplus.adminpanel.controller;

import com.zplus.adminpanel.dto.RegistrationRequest;
import com.zplus.adminpanel.entity.Registration;
import com.zplus.adminpanel.service.RegistrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/registrations")
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
public class RegistrationController {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);

    @Autowired
    private RegistrationService registrationService;

    /**
     * Submit a new registration
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> submitRegistration(
            @Valid @RequestBody RegistrationRequest request) {
        
        logger.info("Received registration request from: {}", request.getEmail());
        
        try {
            Registration registration = registrationService.saveRegistration(request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Registration submitted successfully! Your application is under review.");
            response.put("registrationId", registration.getId());
            response.put("timestamp", LocalDateTime.now());
            response.put("status", "PENDING");
            
            logger.info("Registration created successfully with ID: {}", registration.getId());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error creating registration: {}", e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Sorry, there was an error processing your registration. Please try again.");
            response.put("error", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping
    public ResponseEntity<List<Registration>> getAllRegistrations() {
        return ResponseEntity.ok(registrationService.getAllRegistrations());
    }

    @GetMapping("/pending")
    public ResponseEntity<List<Registration>> getPendingRegistrations() {
        return ResponseEntity.ok(registrationService.getPendingRegistrations());
    }

    @GetMapping("/approved")
    public ResponseEntity<List<Registration>> getApprovedRegistrations() {
        return ResponseEntity.ok(registrationService.getApprovedRegistrations());
    }

    @GetMapping("/rejected")
    public ResponseEntity<List<Registration>> getRejectedRegistrations() {
        return ResponseEntity.ok(registrationService.getRejectedRegistrations());
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<Registration> approveRegistration(@PathVariable Long id) {
        return ResponseEntity.ok(registrationService.approveRegistration(id));
    }

    /**
     * Public endpoint for testing registration approval
     */
    @PostMapping("/{id}/approve/public")
    public ResponseEntity<Map<String, Object>> approveRegistrationPublic(@PathVariable Long id) {
        try {
            Registration approved = registrationService.approveRegistration(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Registration approved successfully and user account created");
            response.put("registrationId", approved.getId());
            response.put("userSelfId", approved.getSelfId());
            response.put("email", approved.getEmail());
            response.put("status", approved.getStatus().name());
            response.put("approvedAt", approved.getApprovedAt().toString());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Failed to approve registration");
            error.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<Registration> rejectRegistration(
            @PathVariable Long id, 
            @RequestParam String reason) {
        return ResponseEntity.ok(registrationService.rejectRegistration(id, reason));
    }

    @PostMapping("/{id}/share")
    public ResponseEntity<Registration> shareRegistration(
            @PathVariable Long id,
            @RequestParam String sharedWith) {
        return ResponseEntity.ok(registrationService.shareRegistration(id, sharedWith));
    }
    
    /**
     * Debug endpoint to check registration status distribution
     */
    @GetMapping("/debug/status-count")
    public ResponseEntity<Map<String, Object>> getRegistrationStatusCount() {
        try {
            List<Registration> allRegistrations = registrationService.getAllRegistrations();
            Map<String, Long> statusCounts = new HashMap<>();
            
            for (Registration reg : allRegistrations) {
                String status = reg.getStatus() != null ? reg.getStatus().toString() : "NULL";
                statusCounts.put(status, statusCounts.getOrDefault(status, 0L) + 1);
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("totalRegistrations", allRegistrations.size());
            result.put("statusCounts", statusCounts);
            result.put("timestamp", LocalDateTime.now());
            
            // Also include detailed list for debugging
            Map<Long, String> registrationDetails = new HashMap<>();
            for (Registration reg : allRegistrations) {
                registrationDetails.put(reg.getId(), reg.getStatus() != null ? reg.getStatus().toString() : "NULL");
            }
            result.put("registrationDetails", registrationDetails);
            
            logger.info("Registration status debug: {}", result);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Error in registration status debug: ", e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
}
