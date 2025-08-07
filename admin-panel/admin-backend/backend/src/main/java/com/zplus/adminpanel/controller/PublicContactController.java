package com.zplus.adminpanel.controller;

import com.zplus.adminpanel.dto.ContactInquiryRequest;
import com.zplus.adminpanel.entity.ContactInquiry;
import com.zplus.adminpanel.service.ContactInquiryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.Arrays;
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
    
    @Autowired
    private Environment environment;

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
     * Debug environment variables (safely)
     */
    @GetMapping("/debug-env")
    public ResponseEntity<Map<String, Object>> debugEnvironment() {
        Map<String, Object> response = new HashMap<>();
        
        // Check DATABASE_URL format (safely)
        String dbUrl = System.getenv("DATABASE_URL");
        if (dbUrl != null) {
            // Show URL format without credentials
            if (dbUrl.startsWith("postgres://")) {
                response.put("database_url_format", "postgres://user:***@host:port/db");
                response.put("database_url_protocol", "postgres");
            } else if (dbUrl.startsWith("postgresql://")) {
                response.put("database_url_format", "postgresql://user:***@host:port/db");
                response.put("database_url_protocol", "postgresql");
            } else {
                response.put("database_url_format", "unknown_format");
            }
            
            // Extract host and port safely
            try {
                String[] parts = dbUrl.split("@");
                if (parts.length > 1) {
                    String hostPart = parts[1].split("/")[0];
                    response.put("database_host", hostPart);
                }
            } catch (Exception e) {
                response.put("database_host", "parse_error");
            }
        } else {
            response.put("database_url_format", "NOT_SET");
        }
        
        // Check Spring profile
        String activeProfiles = System.getenv("SPRING_PROFILES_ACTIVE");
        response.put("spring_profiles_active", activeProfiles != null ? activeProfiles : "NOT_SET");
        
        response.put("timestamp", LocalDateTime.now());
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
    
    /**
     * Comprehensive debug endpoint for database connection
     */
    @GetMapping("/debug")
    public ResponseEntity<Map<String, Object>> debugEndpoint() {
        Map<String, Object> response = new HashMap<>();
        
        // Environment info
        response.put("active_profiles", Arrays.toString(environment.getActiveProfiles()));
        response.put("default_profiles", Arrays.toString(environment.getDefaultProfiles()));
        
        // Database URL info (safely)
        String dbUrl = environment.getProperty("spring.datasource.url");
        String databaseUrl = System.getenv("DATABASE_URL");
        
        if (dbUrl != null) {
            // Hide credentials but show structure
            String safeUrl = dbUrl.replaceAll("://[^@]+@", "://***:***@");
            response.put("spring_datasource_url", safeUrl);
        }
        
        if (databaseUrl != null) {
            String safeDbUrl = databaseUrl.replaceAll("://[^@]+@", "://***:***@");
            response.put("database_url_env", safeDbUrl);
            response.put("database_url_protocol", databaseUrl.startsWith("postgres://") ? "postgres" : "postgresql");
        }
        
        // Test actual database connection
        try {
            long count = contactInquiryService.countAllInquiries();
            response.put("database_connection", "SUCCESS");
            response.put("inquiry_count", count);
        } catch (Exception e) {
            response.put("database_connection", "FAILED");
            response.put("connection_error", e.getMessage());
            response.put("error_type", e.getClass().getSimpleName());
        }
        
        return ResponseEntity.ok(response);
    }
}
