package com.zplus.adminpanel.controller;

import com.zplus.adminpanel.entity.Registration;
import com.zplus.adminpanel.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @Autowired
    private RegistrationService registrationService;

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
}
