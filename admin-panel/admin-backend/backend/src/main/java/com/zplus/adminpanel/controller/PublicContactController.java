package com.zplus.adminpanel.controller;

import com.zplus.adminpanel.dto.ContactInquiryRequest;
import com.zplus.adminpanel.entity.ContactInquiry;
import com.zplus.adminpanel.service.ContactInquiryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Public contact controller with no security restrictions
 */
@RestController
@RequestMapping("/public/contact")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
public class PublicContactController {

    private static final Logger logger = LoggerFactory.getLogger(PublicContactController.class);

    @Autowired
    private ContactInquiryService contactInquiryService;

    /**
     * Simple test endpoint
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testEndpoint() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Public contact endpoint is working");
        response.put("timestamp", LocalDateTime.now());
        
        // Test database connection
        try {
            long count = contactInquiryService.countAllInquiries();
            response.put("database_status", "connected");
            response.put("total_inquiries", count);
        } catch (Exception e) {
            response.put("database_status", "error");
            response.put("database_error", e.getMessage());
        }
        
        logger.info("Test endpoint accessed successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * Submit a new contact inquiry - public endpoint
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
}
