package com.zplus.adminpanel.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ContactInquiry entity representing contact form submissions
 */
@Entity
@Table(name = "contact_inquiries")
@EntityListeners(AuditingEntityListener.class)
public class ContactInquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false)
    @NotBlank(message = "Full name is required")
    @Size(max = 100, message = "Full name must not exceed 100 characters")
    private String fullName;

    @Column(name = "organization")
    @Size(max = 200, message = "Organization must not exceed 200 characters")
    private String organization;

    @Column(name = "email", nullable = false)
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @Column(name = "phone", nullable = false)
    @NotBlank(message = "Phone number is required")
    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    private String phone;

    @Column(name = "country", nullable = false)
    @NotBlank(message = "Country is required")
    @Size(max = 100, message = "Country must not exceed 100 characters")
    private String country;

    @Column(name = "state", nullable = false)
    @NotBlank(message = "State is required")
    @Size(max = 100, message = "State must not exceed 100 characters")
    private String state;

    @Column(name = "city", nullable = false)
    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;

    @Column(name = "address")
    @Size(max = 500, message = "Address must not exceed 500 characters")
    private String address;

    @Column(name = "business_duration")
    @Size(max = 50, message = "Business duration must not exceed 50 characters")
    private String businessDuration;

    @Column(name = "project_timeline")
    @Size(max = 50, message = "Project timeline must not exceed 50 characters")
    private String projectTimeline;

    @ElementCollection
    @CollectionTable(name = "contact_service_interests", 
                     joinColumns = @JoinColumn(name = "contact_inquiry_id"))
    @Column(name = "service_interest")
    private List<String> serviceInterests;

    @Column(name = "business_challenge", nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "Business challenge is required")
    private String businessChallenge;

    @Column(name = "contact_method", nullable = false)
    @NotBlank(message = "Contact method is required")
    @Size(max = 20, message = "Contact method must not exceed 20 characters")
    private String contactMethod;

    @Column(name = "preferred_time")
    @Size(max = 100, message = "Preferred time must not exceed 100 characters")
    private String preferredTime;

    @Column(name = "hear_about")
    @Size(max = 100, message = "Hear about must not exceed 100 characters")
    private String hearAbout;

    @Column(name = "message", nullable = true, columnDefinition = "TEXT")
    private String message;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ContactStatus status = ContactStatus.NEW;

    @Column(name = "assigned_to")
    private String assignedTo;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

    @Column(name = "response_notes", columnDefinition = "TEXT")
    private String responseNotes;

    @Column(name = "shared_with")
    private String sharedWith;

    @Column(name = "shared_at")
    private LocalDateTime sharedAt;

    @Column(name = "shared_by")
    private String sharedBy;

    @Column(name = "share_notes", columnDefinition = "TEXT")
    private String shareNotes;

    // Constructors
    public ContactInquiry() {}

    public ContactInquiry(String fullName, String email, String phone, String country, 
                         String state, String city, String businessChallenge, String contactMethod) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.country = country;
        this.state = state;
        this.city = city;
        this.businessChallenge = businessChallenge;
        this.contactMethod = contactMethod;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBusinessDuration() {
        return businessDuration;
    }

    public void setBusinessDuration(String businessDuration) {
        this.businessDuration = businessDuration;
    }

    public String getProjectTimeline() {
        return projectTimeline;
    }

    public void setProjectTimeline(String projectTimeline) {
        this.projectTimeline = projectTimeline;
    }

    public List<String> getServiceInterests() {
        return serviceInterests;
    }

    public void setServiceInterests(List<String> serviceInterests) {
        this.serviceInterests = serviceInterests;
    }

    public String getBusinessChallenge() {
        return businessChallenge;
    }

    public void setBusinessChallenge(String businessChallenge) {
        this.businessChallenge = businessChallenge;
    }

    public String getContactMethod() {
        return contactMethod;
    }

    public void setContactMethod(String contactMethod) {
        this.contactMethod = contactMethod;
    }

    public String getPreferredTime() {
        return preferredTime;
    }

    public void setPreferredTime(String preferredTime) {
        this.preferredTime = preferredTime;
    }

    public String getHearAbout() {
        return hearAbout;
    }

    public void setHearAbout(String hearAbout) {
        this.hearAbout = hearAbout;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ContactStatus getStatus() {
        return status;
    }

    public void setStatus(ContactStatus status) {
        this.status = status;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getRespondedAt() {
        return respondedAt;
    }

    public void setRespondedAt(LocalDateTime respondedAt) {
        this.respondedAt = respondedAt;
    }

    public String getResponseNotes() {
        return responseNotes;
    }

    public void setResponseNotes(String responseNotes) {
        this.responseNotes = responseNotes;
    }

    public String getSharedWith() {
        return sharedWith;
    }

    public void setSharedWith(String sharedWith) {
        this.sharedWith = sharedWith;
    }

    public LocalDateTime getSharedAt() {
        return sharedAt;
    }

    public void setSharedAt(LocalDateTime sharedAt) {
        this.sharedAt = sharedAt;
    }

    public String getSharedBy() {
        return sharedBy;
    }

    public void setSharedBy(String sharedBy) {
        this.sharedBy = sharedBy;
    }

    public String getShareNotes() {
        return shareNotes;
    }

    public void setShareNotes(String shareNotes) {
        this.shareNotes = shareNotes;
    }

    @Override
    public String toString() {
        return "ContactInquiry{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", country='" + country + '\'' +
                ", state='" + state + '\'' +
                ", city='" + city + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }
} 