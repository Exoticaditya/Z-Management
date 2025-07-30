package com.zplus.adminpanel.controller;

import com.zplus.adminpanel.dto.ContactInquiryRequest;
import com.zplus.adminpanel.entity.ContactInquiry;
import com.zplus.adminpanel.entity.ContactStatus;
import com.zplus.adminpanel.entity.Registration;
import com.zplus.adminpanel.service.ContactInquiryService;
import com.zplus.adminpanel.service.RegistrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for handling contact form submissions and inquiry management
 */
@RestController
@RequestMapping("/api/contact")
@CrossOrigin(origins = {
    "http://localhost:3000", 
    "http://localhost:3001", 
    "http://localhost:8080", 
    "http://127.0.0.1:5507",
    "http://127.0.0.1:5508", 
    "http://localhost:5506", 
    "http://127.0.0.1:3000", 
    "https://zpluse.com",
    "https://www.zpluse.com",
    "http://zpluse.com",
    "http://www.zpluse.com",
    "file://"
})
public class ContactController {

    private static final Logger logger = LoggerFactory.getLogger(ContactController.class);

    @Autowired
    private ContactInquiryService contactInquiryService;

    @Autowired
    private RegistrationService registrationService;

    /**
     * Submit a new contact inquiry
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> submitContactInquiry(
            @Valid @RequestBody ContactInquiryRequest request) {
        
        logger.info("Received contact inquiry from: {}", request.getEmail());
        
        try {
            ContactInquiry inquiry = contactInquiryService.saveContactInquiry(request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Thank you for your inquiry! We'll get back to you soon.");
            response.put("inquiryId", inquiry.getId());
            response.put("timestamp", LocalDateTime.now());
            
            logger.info("Contact inquiry created successfully with ID: {}", inquiry.getId());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error creating contact inquiry: {}", e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Sorry, there was an error processing your inquiry. Please try again.");
            response.put("error", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get all contact inquiries with pagination
     */
    @GetMapping
    public ResponseEntity<Page<ContactInquiry>> getAllInquiries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) ContactStatus status,
            @RequestParam(required = false) String search) {
        
        logger.info("Fetching contact inquiries - page: {}, size: {}, status: {}, search: {}", 
                   page, size, status, search);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ContactInquiry> inquiries = contactInquiryService.getAllInquiries(status, search, pageable);
        
        return ResponseEntity.ok(inquiries);
    }

    /**
     * Get a specific contact inquiry by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ContactInquiry> getInquiryById(@PathVariable Long id) {
        logger.info("Fetching contact inquiry with ID: {}", id);
        
        ContactInquiry inquiry = contactInquiryService.getInquiryById(id);
        if (inquiry != null) {
            return ResponseEntity.ok(inquiry);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Update contact inquiry status
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<ContactInquiry> updateInquiryStatus(
            @PathVariable Long id,
            @RequestParam ContactStatus status,
            @RequestParam(required = false) String notes) {
        
        logger.info("Updating contact inquiry {} status to: {}", id, status);
        
        ContactInquiry updatedInquiry = contactInquiryService.updateInquiryStatus(id, status, notes);
        if (updatedInquiry != null) {
            return ResponseEntity.ok(updatedInquiry);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Assign inquiry to a team member
     */
    @PutMapping("/{id}/assign")
    public ResponseEntity<ContactInquiry> assignInquiry(
            @PathVariable Long id,
            @RequestParam String assignedTo) {
        
        logger.info("Assigning contact inquiry {} to: {}", id, assignedTo);
        
        ContactInquiry updatedInquiry = contactInquiryService.assignInquiry(id, assignedTo);
        if (updatedInquiry != null) {
            return ResponseEntity.ok(updatedInquiry);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Add notes to inquiry
     */
    @PutMapping("/{id}/notes")
    public ResponseEntity<ContactInquiry> addNotes(
            @PathVariable Long id,
            @RequestParam String notes) {
        
        logger.info("Adding notes to contact inquiry: {}", id);
        
        ContactInquiry updatedInquiry = contactInquiryService.addNotes(id, notes);
        if (updatedInquiry != null) {
            return ResponseEntity.ok(updatedInquiry);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Mark inquiry as responded
     */
    @PutMapping("/{id}/respond")
    public ResponseEntity<ContactInquiry> markAsResponded(
            @PathVariable Long id,
            @RequestParam String responseNotes) {
        
        logger.info("Marking contact inquiry {} as responded", id);
        
        ContactInquiry updatedInquiry = contactInquiryService.markAsResponded(id, responseNotes);
        if (updatedInquiry != null) {
            return ResponseEntity.ok(updatedInquiry);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Share contact inquiry with project ID or email
     */
    @PostMapping("/{id}/share")
    public ResponseEntity<Map<String, Object>> shareContactInquiry(
            @PathVariable Long id,
            @RequestParam String sharedWith,
            @RequestParam(required = false) String shareNotes) {
        
        logger.info("Sharing contact inquiry {} with: {}", id, sharedWith);
        
        try {
            ContactInquiry updatedInquiry = contactInquiryService.shareInquiry(id, sharedWith, shareNotes);
            if (updatedInquiry != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Contact inquiry shared successfully");
                response.put("data", updatedInquiry);
                response.put("sharedWith", sharedWith);
                response.put("sharedAt", updatedInquiry.getSharedAt());
                
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Contact inquiry not found");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error sharing contact inquiry {}: {}", id, e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to share contact inquiry: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get inquiries by status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<ContactInquiry>> getInquiriesByStatus(@PathVariable String status) {
        logger.info("Fetching contact inquiries with status: {}", status);
        
        try {
            ContactStatus contactStatus = ContactStatus.valueOf(status.toUpperCase());
            List<ContactInquiry> inquiries = contactInquiryService.getInquiriesByStatus(contactStatus);
            return ResponseEntity.ok(inquiries);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid status: {}", status);
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * Get inquiries by email
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<List<ContactInquiry>> getInquiriesByEmail(@PathVariable String email) {
        logger.info("Fetching contact inquiries for email: {}", email);
        
        List<ContactInquiry> inquiries = contactInquiryService.getInquiriesByEmail(email);
        return ResponseEntity.ok(inquiries);
    }

    /**
     * Get inquiries by date range
     */
    @GetMapping("/date-range")
    public ResponseEntity<List<ContactInquiry>> getInquiriesByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        
        logger.info("Fetching contact inquiries between {} and {}", startDate, endDate);
        
        LocalDateTime start = LocalDateTime.parse(startDate);
        LocalDateTime end = LocalDateTime.parse(endDate);
        List<ContactInquiry> inquiries = contactInquiryService.getInquiriesByDateRange(start, end);
        
        return ResponseEntity.ok(inquiries);
    }

    /**
     * Get inquiry statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getInquiryStatistics() {
        logger.info("Fetching contact inquiry statistics");
        
        Map<String, Object> statistics = contactInquiryService.getInquiryStatistics();
        return ResponseEntity.ok(statistics);
    }

    /**
     * Get recent inquiries
     */
    @GetMapping("/recent")
    public ResponseEntity<List<ContactInquiry>> getRecentInquiries(
            @RequestParam(defaultValue = "7") int days) {
        
        logger.info("Fetching recent contact inquiries from last {} days", days);
        
        List<ContactInquiry> inquiries = contactInquiryService.getRecentInquiries(days);
        return ResponseEntity.ok(inquiries);
    }

    /**
     * Get unassigned inquiries
     */
    @GetMapping("/unassigned")
    public ResponseEntity<List<ContactInquiry>> getUnassignedInquiries() {
        logger.info("Fetching unassigned contact inquiries");
        
        List<ContactInquiry> inquiries = contactInquiryService.getUnassignedInquiries();
        return ResponseEntity.ok(inquiries);
    }

    /**
     * Get high priority inquiries (new inquiries older than 24 hours)
     */
    @GetMapping("/high-priority")
    public ResponseEntity<List<ContactInquiry>> getHighPriorityInquiries() {
        logger.info("Fetching high priority contact inquiries");
        
        List<ContactInquiry> inquiries = contactInquiryService.getHighPriorityInquiries();
        return ResponseEntity.ok(inquiries);
    }

    /**
     * Delete contact inquiry (admin only)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteInquiry(@PathVariable Long id) {
        logger.info("Deleting contact inquiry: {}", id);
        
        boolean deleted = contactInquiryService.deleteInquiry(id);
        
        Map<String, Object> response = new HashMap<>();
        if (deleted) {
            response.put("success", true);
            response.put("message", "Contact inquiry deleted successfully");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Contact inquiry not found");
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/latest")
    public ResponseEntity<ContactInquiry> getLatestContactInquiry() {
        ContactInquiry latest = contactInquiryService.getLatestContactInquiry();
        if (latest != null) {
            return ResponseEntity.ok(latest);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    /**
     * Get all contact inquiries
     */
    @GetMapping("/inquiries")
    public ResponseEntity<List<ContactInquiryDTO>> getAllContactInquiries() {
        List<ContactInquiry> inquiries = contactInquiryService.getAllContactInquiries();
        List<ContactInquiryDTO> dtos = inquiries.stream().map(inquiry -> {
            ContactInquiryDTO dto = new ContactInquiryDTO();
            dto.setId(inquiry.getId());
            dto.setFullName(inquiry.getFullName());
            dto.setEmail(inquiry.getEmail());
            dto.setMessage(inquiry.getMessage());
            dto.setCreatedAt(inquiry.getCreatedAt());
            dto.setStatus(inquiry.getStatus() != null ? inquiry.getStatus().name() : null);
            // Map serviceInterests to List<String> if not null
            if (inquiry.getServiceInterests() != null) {
                dto.setServiceInterests(
                    inquiry.getServiceInterests().stream()
                        .map(Object::toString)
                        .toList()
                );
            }
            return dto;
        }).toList();
        return ResponseEntity.ok(dtos);
    }

    /**
     * Get pending registrations
     */
    @GetMapping("/registrations/pending")
    public ResponseEntity<List<Registration>> getPendingRegistrations() {
        List<Registration> pendingRegistrations = registrationService.getPendingRegistrations();
        return ResponseEntity.ok(pendingRegistrations);
    }

    /**
     * Get inquiries assigned to a specific employee
     */
    @GetMapping("/assigned/{assignedTo}")
    public ResponseEntity<List<ContactInquiry>> getInquiriesAssignedTo(@PathVariable String assignedTo) {
        logger.info("Fetching contact inquiries assigned to: {}", assignedTo);
        
        List<ContactInquiry> inquiries = contactInquiryService.getInquiriesAssignedTo(assignedTo);
        return ResponseEntity.ok(inquiries);
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now());
        response.put("service", "Contact Inquiry Service");
        
        return ResponseEntity.ok(response);
    }
} 