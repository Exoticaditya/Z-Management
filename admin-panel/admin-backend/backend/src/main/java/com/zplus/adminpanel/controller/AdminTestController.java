package com.zplus.adminpanel.controller;

import com.zplus.adminpanel.entity.ContactInquiry;
import com.zplus.adminpanel.entity.ContactStatus;
import com.zplus.adminpanel.entity.Registration;
import com.zplus.adminpanel.repository.ContactInquiryRepository;
import com.zplus.adminpanel.repository.RegistrationRepository;
import com.zplus.adminpanel.service.ContactInquiryService;
import com.zplus.adminpanel.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test controller for admin panel functionality
 */
@RestController
@RequestMapping("/api/admin-test")
@CrossOrigin(origins = "*")
public class AdminTestController {

    private static final Logger logger = LoggerFactory.getLogger(AdminTestController.class);

    @Autowired
    private ContactInquiryRepository contactInquiryRepository;

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private ContactInquiryService contactInquiryService;

    @Autowired
    private NotificationService notificationService;

    /**
     * Test endpoint to check data counts
     */
    @GetMapping("/data-counts")
    public ResponseEntity<Map<String, Object>> getDataCounts() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            long contactCount = contactInquiryRepository.count();
            long registrationCount = registrationRepository.count();
            
            response.put("success", true);
            response.put("contactInquiries", contactCount);
            response.put("registrations", registrationCount);
            response.put("timestamp", LocalDateTime.now());
            
            // Get some sample data
            List<ContactInquiry> recentContacts = contactInquiryRepository.findTop5ByOrderByCreatedAtDesc();
            response.put("recentContacts", recentContacts.size());
            
            List<Registration> allRegistrations = registrationRepository.findAll();
            response.put("allRegistrations", allRegistrations.size());
            
        } catch (Exception e) {
            logger.error("Error getting data counts: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * Test notifications
     */
    @GetMapping("/notifications")
    public ResponseEntity<Map<String, Object>> testNotifications() {
        try {
            Map<String, Object> notifications = notificationService.getAdminNotifications();
            notifications.put("success", true);
            notifications.put("testTimestamp", LocalDateTime.now());
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            logger.error("Error testing notifications: {}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            error.put("message", "Notification system error");
            return ResponseEntity.internalServerError().body(error);
        }
    }

    /**
     * Test assignment functionality
     */
    @PostMapping("/assign-test")
    public ResponseEntity<Map<String, Object>> testAssignment(
            @RequestParam Long inquiryId,
            @RequestParam String assignedTo) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Check if inquiry exists
            ContactInquiry inquiry = contactInquiryRepository.findById(inquiryId).orElse(null);
            if (inquiry == null) {
                response.put("success", false);
                response.put("error", "Contact inquiry not found");
                return ResponseEntity.notFound().build();
            }
            
            // Test assignment
            ContactInquiry updated = contactInquiryService.assignInquiry(inquiryId, assignedTo);
            
            response.put("success", true);
            response.put("inquiryId", updated.getId());
            response.put("assignedTo", updated.getAssignedTo());
            response.put("status", updated.getStatus().name());
            response.put("updatedAt", updated.getUpdatedAt());
            response.put("message", "Assignment successful");
            
        } catch (Exception e) {
            logger.error("Error testing assignment: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get all contact inquiries for testing
     */
    @GetMapping("/contacts")
    public ResponseEntity<Map<String, Object>> getAllContacts() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<ContactInquiry> contacts = contactInquiryRepository.findAll();
            
            response.put("success", true);
            response.put("totalContacts", contacts.size());
            response.put("contacts", contacts.stream().map(contact -> {
                Map<String, Object> dto = new HashMap<>();
                dto.put("id", contact.getId());
                dto.put("fullName", contact.getFullName());
                dto.put("email", contact.getEmail());
                dto.put("status", contact.getStatus().name());
                dto.put("assignedTo", contact.getAssignedTo());
                dto.put("createdAt", contact.getCreatedAt());
                return dto;
            }).toList());
            
        } catch (Exception e) {
            logger.error("Error getting contacts: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * Create a test contact for assignment testing
     */
    @PostMapping("/create-test-contact")
    public ResponseEntity<Map<String, Object>> createTestContact() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            ContactInquiry testContact = new ContactInquiry();
            testContact.setFullName("Test Assignment Contact");
            testContact.setEmail("test.assignment@example.com");
            testContact.setPhone("+1234567890");
            testContact.setCountry("USA");
            testContact.setState("California");
            testContact.setCity("San Francisco");
            testContact.setBusinessChallenge("Testing assignment functionality");
            testContact.setContactMethod("email");
            testContact.setStatus(ContactStatus.NEW);
            testContact.setCreatedAt(LocalDateTime.now());
            testContact.setUpdatedAt(LocalDateTime.now());
            
            ContactInquiry saved = contactInquiryRepository.save(testContact);
            
            response.put("success", true);
            response.put("message", "Test contact created for assignment testing");
            response.put("contactId", saved.getId());
            response.put("email", saved.getEmail());
            response.put("status", saved.getStatus().name());
            
        } catch (Exception e) {
            logger.error("Error creating test contact: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
}
