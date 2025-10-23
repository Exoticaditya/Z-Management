package com.zplus.adminpanel.controller;

import com.zplus.adminpanel.dto.ContactInquiryRequest;
import com.zplus.adminpanel.entity.ContactInquiry;
import com.zplus.adminpanel.entity.ContactStatus;
import com.zplus.adminpanel.service.ContactInquiryService;
import com.zplus.adminpanel.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@CrossOrigin(origins = "*")
@RequestMapping("/api/contact")
public class ContactController {

    private static final Logger logger = LoggerFactory.getLogger(ContactController.class);

    @Autowired
    private ContactInquiryService contactInquiryService;
    
    @Autowired
    private EmailService emailService;

    /**
     * Submit a new contact inquiry
     */
    @PostMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<Map<String, Object>> submitContactInquiry(
            @Valid @RequestBody ContactInquiryRequest request) {
        
        logger.info("Received contact inquiry from: {}", request.getEmail());
        
        try {
            ContactInquiry inquiry = contactInquiryService.saveContactInquiry(request);
            
            // Send email notification
            try {
                emailService.sendContactInquiryNotification(inquiry);
                logger.info("Contact inquiry email sent successfully for inquiry ID: {}", inquiry.getId());
            } catch (Exception emailError) {
                logger.error("Failed to send contact inquiry email: {}", emailError.getMessage());
                // Don't fail the whole request if email fails
            }
            
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
     * Get all contact inquiries (simplified endpoint for 'all')
     */
    @GetMapping("/inquiries")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<ContactInquiry>> getAllInquiriesSimple() {
        logger.info("Fetching all contact inquiries");
        
        try {
            List<ContactInquiry> inquiries = contactInquiryService.getAllInquiriesSimple();
            return ResponseEntity.ok(inquiries);
        } catch (Exception e) {
            logger.error("Error fetching all contact inquiries: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get contact inquiries by status (simplified endpoint)
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<ContactInquiry>> getInquiriesByStatus(@PathVariable ContactStatus status) {
        logger.info("Fetching contact inquiries with status: {}", status);
        
        try {
            List<ContactInquiry> inquiries = contactInquiryService.getInquiriesByStatus(status);
            return ResponseEntity.ok(inquiries);
        } catch (Exception e) {
            logger.error("Error fetching contact inquiries by status {}: {}", status, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get all contact inquiries with pagination and filtering
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
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
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ContactInquiry> getInquiryById(@PathVariable Long id) {
        logger.info("Fetching contact inquiry with ID: {}", id);
        
        try {
            ContactInquiry inquiry = contactInquiryService.getInquiryById(id);
            return ResponseEntity.ok(inquiry);
        } catch (Exception e) {
            logger.error("Error fetching contact inquiry with ID {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Update contact inquiry status
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ContactInquiry> updateInquiryStatus(
            @PathVariable Long id,
            @RequestParam ContactStatus status,
            @RequestParam(required = false) String notes) {
        
        logger.info("Updating contact inquiry {} status to: {}", id, status);
        
        try {
            ContactInquiry updatedInquiry = contactInquiryService.updateInquiryStatus(id, status, notes);
            return ResponseEntity.ok(updatedInquiry);
        } catch (Exception e) {
            logger.error("Error updating contact inquiry status: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Resolve a contact inquiry with detailed response
     */
    @PutMapping("/{id}/resolve")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ContactInquiry> resolveInquiryDetailed(
            @PathVariable Long id,
            @RequestBody Map<String, Object> requestBody) {
        
        logger.info("Resolving contact inquiry with ID: {} with detailed response", id);
        
        try {
            String responseNotes = (String) requestBody.get("responseNotes");
            String status = (String) requestBody.get("status");
            
            ContactInquiry resolvedInquiry = contactInquiryService.updateInquiryStatus(
                id, 
                ContactStatus.valueOf(status != null ? status : "RESOLVED"), 
                responseNotes != null ? responseNotes : "Inquiry resolved"
            );
            
            // Set response timestamp
            resolvedInquiry.setRespondedAt(LocalDateTime.now());
            
            return ResponseEntity.ok(resolvedInquiry);
        } catch (Exception e) {
            logger.error("Error resolving contact inquiry with details: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Resolve a contact inquiry (simple version for backward compatibility)
     */
    @PostMapping("/{id}/resolve")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ContactInquiry> resolveInquiry(@PathVariable Long id) {
        logger.info("Resolving contact inquiry with ID: {}", id);
        
        try {
            ContactInquiry resolvedInquiry = contactInquiryService.updateInquiryStatus(id, ContactStatus.RESOLVED, "Inquiry resolved");
            return ResponseEntity.ok(resolvedInquiry);
        } catch (Exception e) {
            logger.error("Error resolving contact inquiry: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Share a contact inquiry with team members
     */
    @PutMapping("/{id}/share")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ContactInquiry> shareInquiry(
            @PathVariable Long id,
            @RequestBody Map<String, Object> requestBody) {
        
        logger.info("Sharing contact inquiry with ID: {}", id);
        
        try {
            String sharedWith = (String) requestBody.get("sharedWith");
            String shareNotes = (String) requestBody.get("shareNotes");
            String sharedBy = (String) requestBody.get("sharedBy");
            
            ContactInquiry sharedInquiry = contactInquiryService.shareInquiry(id, sharedWith, shareNotes, sharedBy);
            return ResponseEntity.ok(sharedInquiry);
        } catch (Exception e) {
            logger.error("Error sharing contact inquiry: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Assign a contact inquiry to a team member
     */
    @PutMapping("/{id}/assign")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ContactInquiry> assignInquiry(
            @PathVariable Long id,
            @RequestParam String assignedTo) {
        
        logger.info("Assigning contact inquiry {} to: {}", id, assignedTo);
        
        try {
            ContactInquiry assignedInquiry = contactInquiryService.assignInquiry(id, assignedTo);
            return ResponseEntity.ok(assignedInquiry);
        } catch (Exception e) {
            logger.error("Error assigning contact inquiry: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Delete a contact inquiry
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteInquiry(@PathVariable Long id) {
        logger.info("Deleting contact inquiry with ID: {}", id);
        
        try {
            contactInquiryService.deleteInquiry(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Contact inquiry deleted successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error deleting contact inquiry: {}", e.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to delete contact inquiry");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get contact inquiry statistics
     */
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Map<String, Object>> getInquiryStats() {
        logger.info("Fetching contact inquiry statistics");
        
        try {
            Map<String, Object> stats = contactInquiryService.getInquiryStatistics();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            logger.error("Error fetching contact inquiry statistics: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}