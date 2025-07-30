package com.zplus.adminpanel.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for exposing contact inquiry data safely
 */
public class ContactInquiryDTO {
    private Long id;
    private String fullName;
    private String email;
    private String message;
    private LocalDateTime createdAt;
    private String status;
    private List<String> serviceInterests; // Use String or a DTO if needed

    // Constructors
    public ContactInquiryDTO() {}

    public ContactInquiryDTO(Long id, String fullName, String email, String message, LocalDateTime createdAt, String status, List<String> serviceInterests) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.message = message;
        this.createdAt = createdAt;
        this.status = status;
        this.serviceInterests = serviceInterests;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public List<String> getServiceInterests() { return serviceInterests; }
    public void setServiceInterests(List<String> serviceInterests) { this.serviceInterests = serviceInterests; }
}
