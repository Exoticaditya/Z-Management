package com.zplus.adminpanel.service;

import com.zplus.adminpanel.entity.Registration;
import com.zplus.adminpanel.entity.ContactInquiry;
import com.zplus.adminpanel.entity.RegistrationStatus;
import com.zplus.adminpanel.repository.RegistrationRepository;
import com.zplus.adminpanel.repository.ContactInquiryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for handling admin notifications and dashboard data
 */
@Service
public class NotificationService {

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private ContactInquiryRepository contactInquiryRepository;

    /**
     * Get all pending notifications for admin dashboard
     */
    public Map<String, Object> getAdminNotifications() {
        Map<String, Object> notifications = new HashMap<>();
        
        try {
            // Get pending registrations count
            List<Registration> pendingRegistrations = registrationRepository.findByStatus(RegistrationStatus.PENDING);
            notifications.put("pendingRegistrations", pendingRegistrations.size());
            
            // Convert to DTOs to avoid lazy loading issues
            List<Map<String, Object>> registrationDTOs = pendingRegistrations.stream()
                .map(reg -> {
                    Map<String, Object> dto = new HashMap<>();
                    dto.put("id", reg.getId());
                    dto.put("firstName", reg.getFirstName());
                    dto.put("lastName", reg.getLastName());
                    dto.put("email", reg.getEmail());
                    dto.put("department", reg.getDepartment());
                    dto.put("createdAt", reg.getCreatedAt());
                    dto.put("status", reg.getStatus().name());
                    return dto;
                })
                .toList();
            notifications.put("pendingRegistrationsList", registrationDTOs);
            
            // Get pending contact inquiries count
            List<ContactInquiry> pendingContacts = contactInquiryRepository.findByStatus(
                com.zplus.adminpanel.entity.ContactStatus.PENDING
            );
            notifications.put("pendingContacts", pendingContacts.size());
            
            // Convert to DTOs to avoid lazy loading issues
            List<Map<String, Object>> contactDTOs = pendingContacts.stream()
                .map(contact -> {
                    Map<String, Object> dto = new HashMap<>();
                    dto.put("id", contact.getId());
                    dto.put("fullName", contact.getFullName());
                    dto.put("email", contact.getEmail());
                    dto.put("phone", contact.getPhone());
                    dto.put("organization", contact.getOrganization());
                    dto.put("createdAt", contact.getCreatedAt());
                    dto.put("status", contact.getStatus().name());
                    dto.put("assignedTo", contact.getAssignedTo());
                    return dto;
                })
                .toList();
            notifications.put("pendingContactsList", contactDTOs);
            
            // Calculate total pending items
            notifications.put("totalPending", pendingRegistrations.size() + pendingContacts.size());
            
        } catch (Exception e) {
            // Fallback with basic counts
            notifications.put("pendingRegistrations", 0);
            notifications.put("pendingContacts", 0);
            notifications.put("totalPending", 0);
            notifications.put("error", e.getMessage());
        }
        
        return notifications;
    }

    /**
     * Get detailed dashboard statistics
     */
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // Registration statistics
        stats.put("totalRegistrations", registrationRepository.count());
        stats.put("pendingRegistrations", registrationRepository.countByStatus(RegistrationStatus.PENDING));
        stats.put("approvedRegistrations", registrationRepository.countByStatus(RegistrationStatus.APPROVED));
        stats.put("rejectedRegistrations", registrationRepository.countByStatus(RegistrationStatus.REJECTED));
        
        // Contact inquiry statistics
        stats.put("totalContacts", contactInquiryRepository.count());
        stats.put("pendingContacts", contactInquiryRepository.countByStatus(
            com.zplus.adminpanel.entity.ContactStatus.PENDING
        ));
        stats.put("resolvedContacts", contactInquiryRepository.countByStatus(
            com.zplus.adminpanel.entity.ContactStatus.RESOLVED
        ));
        
        return stats;
    }

    /**
     * Get recent activity for dashboard
     */
    public Map<String, Object> getRecentActivity() {
        Map<String, Object> activity = new HashMap<>();
        
        // Get 5 most recent registrations
        List<Registration> recentRegistrations = registrationRepository.findTop5ByOrderByCreatedAtDesc();
        activity.put("recentRegistrations", recentRegistrations);
        
        // Get 5 most recent contact inquiries
        List<ContactInquiry> recentContacts = contactInquiryRepository.findTop5ByOrderByCreatedAtDesc();
        activity.put("recentContacts", recentContacts);
        
        return activity;
    }
}
