package com.zplus.adminpanel.service.impl;

import com.zplus.adminpanel.dto.RegistrationRequest;
import com.zplus.adminpanel.entity.Registration;
import com.zplus.adminpanel.entity.RegistrationStatus;
import com.zplus.adminpanel.exception.ResourceNotFoundException;
import com.zplus.adminpanel.repository.RegistrationRepository;
import com.zplus.adminpanel.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class RegistrationServiceImpl implements RegistrationService {
    
    @Autowired
    private RegistrationRepository registrationRepository;

    @Override
    public Registration saveRegistration(RegistrationRequest request) {
        Registration registration = new Registration();
        
        // Set basic information
        registration.setFirstName(request.getFirstName());
        registration.setLastName(request.getLastName());
        registration.setEmail(request.getEmail());
        registration.setPhone(request.getPhone());
        registration.setDepartment(request.getDepartment());
        
        // Convert string userType to enum
        try {
            registration.setUserType(com.zplus.adminpanel.entity.UserType.valueOf(request.getUserType().toUpperCase()));
        } catch (IllegalArgumentException e) {
            registration.setUserType(com.zplus.adminpanel.entity.UserType.CLIENT); // Default fallback
        }
        
        // Set required fields with defaults or from request
        registration.setProjectId("DEFAULT_PROJECT"); // You may want to make this configurable
        registration.setSelfId(request.getSelfId());
        registration.setPassword(request.getPassword()); // Note: This should be hashed in production
        registration.setReason("New user registration via website");
        registration.setSupervisor("System Administrator");
        
        // Set status and timestamps
        registration.setStatus(RegistrationStatus.PENDING);
        registration.setCreatedAt(LocalDateTime.now());
        registration.setUpdatedAt(LocalDateTime.now());
        registration.setIsActive(true);
        
        return registrationRepository.save(registration);
    }
    
    @Override
    public List<Registration> getAllRegistrations() {
        return registrationRepository.findAll();
    }
    
    @Override
    public List<Registration> getPendingRegistrations() {
        return registrationRepository.findByStatus(RegistrationStatus.PENDING);
    }

    @Override
    public List<Registration> getApprovedRegistrations() {
        return registrationRepository.findByStatus(RegistrationStatus.APPROVED);
    }

    @Override
    public List<Registration> getRejectedRegistrations() {
        return registrationRepository.findByStatus(RegistrationStatus.REJECTED);
    }

    @Override
    public Registration approveRegistration(Long id) {
        Registration registration = registrationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Registration not found"));
            
        registration.setStatus(RegistrationStatus.APPROVED);
        registration.setUpdatedAt(LocalDateTime.now());
        registration.setApprovedAt(LocalDateTime.now());
        registration.setApprovedBy("System Administrator");
        
        // Set default values for required fields if they are null or empty
        if (registration.getReason() == null || registration.getReason().trim().isEmpty()) {
            registration.setReason("Approved for " + registration.getDepartment() + " department access");
        }
        if (registration.getSupervisor() == null || registration.getSupervisor().trim().isEmpty()) {
            registration.setSupervisor("Department Manager");
        }
        if (registration.getProjectId() == null || registration.getProjectId().trim().isEmpty()) {
            registration.setProjectId("ZP-2024-" + String.format("%03d", id));
        }
        
        return registrationRepository.save(registration);
    }

    @Override
    public Registration rejectRegistration(Long id, String reason) {
        Registration registration = registrationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Registration not found"));
            
        registration.setStatus(RegistrationStatus.REJECTED);
        registration.setRejectionReason(reason);
        registration.setUpdatedAt(LocalDateTime.now());
        return registrationRepository.save(registration);
    }

    @Override
    public Registration shareRegistration(Long id, String sharedWith) {
        Registration registration = registrationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Registration not found"));
            
        registration.setSharedWith(sharedWith);
        registration.setUpdatedAt(LocalDateTime.now());
        return registrationRepository.save(registration);
    }
}
