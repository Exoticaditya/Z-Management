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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
        logger.info("Processing registration request for email: {}", request.getEmail());
        
        // Check if email already exists
        if (registrationRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists in registration system");
        }
        
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists in user system");
        }
        
        Registration registration = new Registration();
        
        // Set basic information
        registration.setFirstName(request.getFirstName());
        registration.setLastName(request.getLastName());
        registration.setEmail(request.getEmail());
        registration.setPhone(request.getPhone());
        registration.setDepartment(request.getDepartment());
        
        // Convert string userType to enum with better error handling
        try {
            registration.setUserType(UserType.valueOf(request.getUserType().toUpperCase()));
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid userType provided: {}, defaulting to CLIENT", request.getUserType());
            registration.setUserType(UserType.CLIENT);
        }
        
        // Set required fields
        registration.setProjectId(generateProjectId());
        registration.setSelfId(request.getSelfId());
        registration.setPassword(passwordEncoder.encode(request.getPassword()));
        registration.setReason("New user registration via website");
        registration.setSupervisor("System Administrator");
        
        // Set status and timestamps
        registration.setStatus(RegistrationStatus.PENDING);
        registration.setCreatedAt(LocalDateTime.now());
        registration.setUpdatedAt(LocalDateTime.now());
        registration.setIsActive(true);
        
        Registration savedRegistration = registrationRepository.save(registration);
        logger.info("Registration created successfully with ID: {}", savedRegistration.getId());
        
        return savedRegistration;
    }
    
    private String generateProjectId() {
        return "ZP-" + LocalDateTime.now().getYear() + "-" + 
               String.format("%03d", System.currentTimeMillis() % 1000);
    }
    
    @Override
    public List<Registration> getAllRegistrations() {
        return registrationRepository.findAll();
    }
    
    @Override
    public Page<Registration> getAllRegistrations(Pageable pageable) {
        return registrationRepository.findAll(pageable);
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
    public Registration getRegistrationById(Long id) {
        return registrationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Registration not found with ID: " + id));
    }

    @Override
    @Transactional
    public Registration approveRegistration(Long id) {
        Registration registration = getRegistrationById(id);
        
        if (registration.getStatus() != RegistrationStatus.PENDING) {
            throw new IllegalStateException("Only pending registrations can be approved");
        }
            
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
            // Rollback the registration approval if user creation fails
            registration.setStatus(RegistrationStatus.PENDING);
            registration.setApprovedAt(null);
            registration.setApprovedBy(null);
            registrationRepository.save(registration);
            throw new RuntimeException("Failed to create user account: " + e.getMessage(), e);
        }
        
        return savedRegistration;
    }
    
    /**
     * Create a user account from an approved registration
     */
    private void createUserFromRegistration(Registration registration) {
        // Check if user already exists
        Optional<User> existingUserBySelfId = userRepository.findBySelfId(registration.getSelfId());
        if (existingUserBySelfId.isPresent()) {
            logger.warn("User with selfId {} already exists, skipping user creation", registration.getSelfId());
            return;
        }
        
        Optional<User> existingUserByEmail = userRepository.findByEmail(registration.getEmail());
        if (existingUserByEmail.isPresent()) {
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
        User savedUser = userRepository.save(user);
        
        logger.info("Created user account for {} with selfId: {}", savedUser.getEmail(), savedUser.getSelfId());
    }

    @Override
    public Registration rejectRegistration(Long id, String reason) {
        Registration registration = getRegistrationById(id);
        
        if (registration.getStatus() != RegistrationStatus.PENDING) {
            throw new IllegalStateException("Only pending registrations can be rejected");
        }
            
        registration.setStatus(RegistrationStatus.REJECTED);
        registration.setRejectionReason(reason);
        registration.setUpdatedAt(LocalDateTime.now());
        
        Registration savedRegistration = registrationRepository.save(registration);
        logger.info("Registration rejected for user: {} with reason: {}", registration.getEmail(), reason);
        
        return savedRegistration;
    }

    @Override
    public Registration shareRegistration(Long id, String sharedWith) {
        Registration registration = getRegistrationById(id);
            
        registration.setSharedWith(sharedWith);
        registration.setUpdatedAt(LocalDateTime.now());
        
        Registration savedRegistration = registrationRepository.save(registration);
        logger.info("Registration shared for user: {} with: {}", registration.getEmail(), sharedWith);
        
        return savedRegistration;
    }
    
    @Override
    public String encodePassword(String plainPassword) {
        return passwordEncoder.encode(plainPassword);
    }
    
    @Override
    public Registration updateRegistration(Registration registration) {
        registration.setUpdatedAt(LocalDateTime.now());
        return registrationRepository.save(registration);
    }

    @Override
    public void deleteRegistration(Long id) {
        Registration registration = getRegistrationById(id);
        registrationRepository.delete(registration);
        logger.info("Registration deleted for ID: {}", id);
    }
}
