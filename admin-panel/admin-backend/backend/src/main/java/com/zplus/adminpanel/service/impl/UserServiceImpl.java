package com.zplus.adminpanel.service.impl;

import com.zplus.adminpanel.dto.LoginResponse;
import com.zplus.adminpanel.dto.RegistrationRequest;
import com.zplus.adminpanel.dto.RegistrationResponse;
import com.zplus.adminpanel.dto.UpdateProfileRequestDto;
import com.zplus.adminpanel.dto.UserDTO;
import com.zplus.adminpanel.exception.ResourceNotFoundException;
import com.zplus.adminpanel.exception.AuthenticationException;
import com.zplus.adminpanel.entity.User;
import com.zplus.adminpanel.entity.Registration;
import com.zplus.adminpanel.entity.RegistrationStatus;
import com.zplus.adminpanel.entity.UserType;
import com.zplus.adminpanel.repository.UserRepository;
import com.zplus.adminpanel.repository.RegistrationRepository;
import com.zplus.adminpanel.service.UserService;
import com.zplus.adminpanel.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final RegistrationRepository registrationRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserServiceImpl(UserRepository userRepository, RegistrationRepository registrationRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.registrationRepository = registrationRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public RegistrationResponse registerUser(RegistrationRequest request) {
        try {
            logger.info("Registration attempt for user: {}", request.getSelfId());
            
            // Check if email already exists in users OR registrations
            if (userRepository.findByEmail(request.getEmail()).isPresent() ||
                registrationRepository.findByEmail(request.getEmail()).isPresent()) {
                logger.warn("Registration failed - email already exists: {}", request.getEmail());
                return new RegistrationResponse(false, "Email already exists", null, null);
            }
            
            // Check if selfId already exists
            if (userRepository.findBySelfId(request.getSelfId()).isPresent() ||
                registrationRepository.findBySelfId(request.getSelfId()).isPresent()) {
                logger.warn("Registration failed - self ID already exists: {}", request.getSelfId());
                return new RegistrationResponse(false, "Self ID already exists", null, null);
            }

            // Create new registration (not user yet)
            Registration newRegistration = new Registration();
            newRegistration.setFirstName(request.getFirstName());
            newRegistration.setLastName(request.getLastName());
            newRegistration.setEmail(request.getEmail());
            newRegistration.setPhone(request.getPhone());
            newRegistration.setDepartment(request.getDepartment());
            newRegistration.setSelfId(request.getSelfId());
            // Remove username field - not needed for registrations
            newRegistration.setPassword(passwordEncoder.encode(request.getPassword())); // Hash password
            
            // Set user type from request
            try {
                UserType userType = UserType.valueOf(request.getUserType().toUpperCase());
                newRegistration.setUserType(userType);
            } catch (IllegalArgumentException e) {
                logger.warn("Invalid user type: {}, defaulting to CLIENT", request.getUserType());
                newRegistration.setUserType(UserType.CLIENT);
            }
            
            newRegistration.setStatus(RegistrationStatus.PENDING);
            newRegistration.setIsActive(true);
            newRegistration.setCreatedAt(LocalDateTime.now());
            newRegistration.setUpdatedAt(LocalDateTime.now());
            
            // Set default values for required fields
            newRegistration.setReason("Access required for " + request.getDepartment() + " department");
            newRegistration.setSupervisor("To be assigned");
            newRegistration.setProjectId("TBD-" + request.getSelfId());
            
            // Save to registrations table
            registrationRepository.save(newRegistration);
            
            logger.info("Registration saved successfully for user: {}", request.getSelfId());
            
            return new RegistrationResponse(
                true, 
                "Registration submitted successfully. Awaiting admin approval.", 
                null, 
                request.getEmail()
            );
                
        } catch (Exception e) {
            logger.error("Registration failed for user: {} - Error: {}", request.getSelfId(), e.getMessage(), e);
            return new RegistrationResponse(
                false, 
                "Registration failed: " + e.getMessage(), 
                null, 
                null
            );
        }
    }

    @Override
    public LoginResponse authenticateUser(String email, String password) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
            
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new AuthenticationException("Invalid credentials");
        }
            
        String token = jwtUtil.generateToken(user);
        return new LoginResponse(true, "Login successful", token, user.getUserType().toString(), user.getSelfId(), user.getFirstName() + " " + user.getLastName());
    }

    @Override
    public String uploadProfilePhoto(Long userId, MultipartFile file) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        
        String photoUrl = "https://example.com/profile-photos/" + userId;
        user.setProfilePhotoUrl(photoUrl);
        userRepository.save(user);
        return photoUrl;
    }

    @Override
    public UserDTO getUserByEmail(String email) {
        return userRepository.findByEmail(email)
            .map(this::convertToDto)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public Page<UserDTO> getPendingRegistrations(Pageable pageable) {
        // Change from users table to registrations table
        return registrationRepository.findByStatus(RegistrationStatus.PENDING, pageable)
            .map(this::convertRegistrationToDto);
    }

    @Override
    public Map<String, Object> getRegistrationStatistics() {
        Map<String, Object> stats = new HashMap<>();
        try {
            // Get stats from registrations table instead of users table
            stats.put("total", registrationRepository.count());
            
            // Initialize all possible statuses with 0
            for (RegistrationStatus status : RegistrationStatus.values()) {
                stats.put(status.name().toLowerCase(), 0L);
            }
            
            // Update with actual counts from registrations table
            for (RegistrationStatus status : RegistrationStatus.values()) {
                try {
                    long count = registrationRepository.countByStatus(status);
                    stats.put(status.name().toLowerCase(), count);
                } catch (Exception e) {
                    logger.warn("Error counting registrations with status {}: {}", status, e.getMessage());
                }
            }
        } catch (Exception e) {
            logger.error("Error getting registration statistics: {}", e.getMessage(), e);
            stats.put("error", "Error retrieving statistics: " + e.getMessage());
        }
        return stats;
    }

    @Override
    public Page<UserDTO> getAllUsers(Pageable pageable, RegistrationStatus status, UserType userType, String search) {
        // Query registrations table instead of users table
        if (status != null && userType != null && search != null) {
            return registrationRepository.findWithFilters(status, userType, search, pageable)
                .map(this::convertRegistrationToDto);
        } else if (status != null) {
            return registrationRepository.findByStatus(status, pageable)
                .map(this::convertRegistrationToDto);
        } else {
            return registrationRepository.findAll(pageable)
                .map(this::convertRegistrationToDto);
        }
    }

    @Override
    public UserDTO updateProfile(Long userId, UpdateProfileRequestDto updateRequest) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setFirstName(updateRequest.getFirstName());
        user.setLastName(updateRequest.getLastName());
        user.setPhone(updateRequest.getPhoneNumber());
        user.setUpdatedAt(LocalDateTime.now());
        
        userRepository.save(user);
        return convertToDto(user);
    }

    @Override
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
    }

    @Override
    public UserDTO getUserById(Long userId) {
        return userRepository.findById(userId)
            .map(this::convertToDto)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public UserDTO getLatestRegistration() {
        User user = userRepository.findTopByOrderByCreatedAtDesc();
        return user != null ? convertToDto(user) : null;
    }

    private UserDTO convertToDto(User user) {
        return new UserDTO(user);
    }

    // Add new method to convert Registration to UserDTO
    private UserDTO convertRegistrationToDto(Registration registration) {
        // Create a temporary User entity from Registration to use existing constructor
        User tempUser = new User();
        tempUser.setId(registration.getId());
        tempUser.setFirstName(registration.getFirstName());
        tempUser.setLastName(registration.getLastName());
        tempUser.setEmail(registration.getEmail());
        tempUser.setPhone(registration.getPhone());
        tempUser.setDepartment(registration.getDepartment());
        tempUser.setSelfId(registration.getSelfId());
        tempUser.setUserType(registration.getUserType());
        tempUser.setStatus(registration.getStatus());
        tempUser.setIsActive(registration.getIsActive());
        tempUser.setCreatedAt(registration.getCreatedAt());
        tempUser.setApprovedAt(registration.getApprovedAt());
        tempUser.setApprovedBy(registration.getApprovedBy());
        tempUser.setRejectionReason(registration.getRejectionReason());
        tempUser.setProjectId(registration.getProjectId());
        tempUser.setSupervisor(registration.getSupervisor());
        
        return new UserDTO(tempUser);
    }

    @Override
    public List<Map<String, Object>> getRecentActivity() {
        try {
            List<Map<String, Object>> activity = new java.util.ArrayList<>();
            
            // Get recent registrations
            List<Registration> recentRegistrations = registrationRepository.findTop5ByOrderByCreatedAtDesc();
            for (Registration reg : recentRegistrations) {
                activity.add(Map.of(
                    "user", reg.getFirstName() + " " + reg.getLastName(),
                    "action", "registered",
                    "time", reg.getCreatedAt().toString(),
                    "type", "registration"
                ));
            }
            
            // Get recent users
            List<User> recentUsers = userRepository.findTop5ByOrderByCreatedAtDesc();
            for (User user : recentUsers) {
                activity.add(Map.of(
                    "user", user.getFirstName() + " " + user.getLastName(),
                    "action", "joined",
                    "time", user.getCreatedAt().toString(),
                    "type", "user"
                ));
            }
            
            // Sort by time (most recent first)
            activity.sort((a, b) -> b.get("time").toString().compareTo(a.get("time").toString()));
            
            return activity.stream().limit(10).toList();
        } catch (Exception e) {
            logger.error("Error getting recent activity: {}", e.getMessage(), e);
            return List.of(Map.of("user", "System", "action", "error getting activity", "time", "now"));
        }
    }

    @Override
    public Map<String, Object> getAdminDashboard() {
        Map<String, Object> dashboard = new HashMap<>();
        
        try {
            // User statistics
            dashboard.put("totalUsers", userRepository.count());
            dashboard.put("adminUsers", userRepository.countByUserType("ADMIN"));
            dashboard.put("employeeUsers", userRepository.countByUserType("EMPLOYEE"));
            dashboard.put("clientUsers", userRepository.countByUserType("CLIENT"));
            
            // Registration statistics
            dashboard.put("totalRegistrations", registrationRepository.count());
            dashboard.put("pendingRegistrations", registrationRepository.countByStatus(RegistrationStatus.PENDING));
            dashboard.put("approvedRegistrations", registrationRepository.countByStatus(RegistrationStatus.APPROVED));
            dashboard.put("rejectedRegistrations", registrationRepository.countByStatus(RegistrationStatus.REJECTED));
            
            // Recent activity
            dashboard.put("recentActivity", getRecentActivity());
            
            dashboard.put("status", "success");
        } catch (Exception e) {
            logger.error("Error loading admin dashboard: {}", e.getMessage(), e);
            dashboard.put("status", "error");
            dashboard.put("error", e.getMessage());
        }
        
        return dashboard;
    }

    @Override
    public Map<String, Object> getEmployeeDashboard() {
        Map<String, Object> dashboard = new HashMap<>();
        
        try {
            // Employee-specific stats
            dashboard.put("totalEmployees", userRepository.countByUserType("EMPLOYEE"));
            dashboard.put("totalClients", userRepository.countByUserType("CLIENT"));
            dashboard.put("pendingRegistrations", registrationRepository.countByStatus(RegistrationStatus.PENDING));
            
            // Recent activity (limited for employees)
            List<Map<String, Object>> activity = getRecentActivity();
            dashboard.put("recentActivity", activity.stream().limit(5).toList());
            
            dashboard.put("status", "success");
            dashboard.put("userType", "EMPLOYEE");
        } catch (Exception e) {
            logger.error("Error loading employee dashboard: {}", e.getMessage(), e);
            dashboard.put("status", "error");
            dashboard.put("error", e.getMessage());
        }
        
        return dashboard;
    }

    @Override
    public Map<String, Object> getClientDashboard() {
        Map<String, Object> dashboard = new HashMap<>();
        
        try {
            // Client-specific stats (limited)
            dashboard.put("totalClients", userRepository.countByUserType("CLIENT"));
            dashboard.put("myRegistrationStatus", "Active");
            
            // Limited recent activity for clients
            dashboard.put("recentUpdates", List.of(
                Map.of("message", "Welcome to Z+ Admin Panel", "time", "Today"),
                Map.of("message", "Your account is active", "time", "Today")
            ));
            
            dashboard.put("status", "success");
            dashboard.put("userType", "CLIENT");
        } catch (Exception e) {
            logger.error("Error loading client dashboard: {}", e.getMessage(), e);
            dashboard.put("status", "error");
            dashboard.put("error", e.getMessage());
        }
        
        return dashboard;
    }
}
