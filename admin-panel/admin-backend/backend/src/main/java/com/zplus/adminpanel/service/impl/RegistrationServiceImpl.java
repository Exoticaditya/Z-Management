package com.zplus.adminpanel.service.impl;

import com.zplus.adminpanel.dto.RegistrationRequest;
import com.zplus.adminpanel.entity.Registration;
import com.zplus.adminpanel.entity.RegistrationStatus;
import com.zplus.adminpanel.entity.User;
import com.zplus.adminpanel.entity.UserType;
import com.zplus.adminpanel.exception.ResourceNotFoundException;
import com.zplus.adminpanel.repository.RegistrationRepository;
import com.zplus.adminpanel.repository.UserRepository;
import com.zplus.adminpanel.service.RegistrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class RegistrationServiceImpl implements RegistrationService {
    
    private static final Logger logger = LoggerFactory.getLogger(RegistrationServiceImpl.class);
    
    @Autowired
    private RegistrationRepository registrationRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

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
        registration.setPassword(passwordEncoder.encode(request.getPassword())); // Encode password for security
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
            
        logger.info("Approving registration for user: {} with selfId: {}", registration.getEmail(), registration.getSelfId());
        
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
        
        // Save the updated registration first
        Registration savedRegistration = registrationRepository.save(registration);
        
        // Create a user account for the approved registration
        try {
            createUserFromRegistration(savedRegistration);
            logger.info("Successfully created user account for registration: {}", savedRegistration.getSelfId());
        } catch (Exception e) {
            logger.error("Failed to create user account for registration {}: {}", savedRegistration.getSelfId(), e.getMessage(), e);
            // Don't fail the approval if user creation fails, but log it
        }
        
        return savedRegistration;
    }
    
    /**
     * Create a user account from an approved registration
     */
    private void createUserFromRegistration(Registration registration) {
        // Check if user already exists
        if (userRepository.findBySelfId(registration.getSelfId()).isPresent()) {
            logger.warn("User with selfId {} already exists, skipping user creation", registration.getSelfId());
            return;
        }
        
        if (userRepository.findByEmail(registration.getEmail()).isPresent()) {
            logger.warn("User with email {} already exists, skipping user creation", registration.getEmail());
            return;
        }
        
        // Create new user
        User user = new User();
        user.setFirstName(registration.getFirstName());
        user.setLastName(registration.getLastName());
        user.setEmail(registration.getEmail());
        user.setPhone(registration.getPhone());
        user.setDepartment(registration.getDepartment());
        user.setSelfId(registration.getSelfId());
        user.setUserType(registration.getUserType());
        user.setStatus(RegistrationStatus.APPROVED);
        user.setIsActive(true);
        
        // Use the same password hash from registration (it should already be encoded)
        user.setPasswordHash(registration.getPassword());
        
        // Set approval details
        user.setApprovedAt(registration.getApprovedAt());
        user.setApprovedBy(registration.getApprovedBy());
        user.setProjectId(registration.getProjectId());
        user.setSupervisor(registration.getSupervisor());
        
        // Set timestamps
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        // Save the user
        userRepository.save(user);
        
        logger.info("Created user account for {} with selfId: {}", user.getEmail(), user.getSelfId());
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
