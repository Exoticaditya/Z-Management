package com.zplus.adminpanel.controller;

import com.zplus.adminpanel.dto.LoginResponse;
import com.zplus.adminpanel.dto.RegistrationRequest;
import com.zplus.adminpanel.dto.RegistrationResponse;
import com.zplus.adminpanel.dto.UpdateProfileRequestDto;
import com.zplus.adminpanel.dto.UserDTO;
import com.zplus.adminpanel.entity.RegistrationStatus;
import com.zplus.adminpanel.entity.UserType;
import com.zplus.adminpanel.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = {
    "http://localhost:3000",
    "http://localhost:3001",
    "http://localhost:8080",
    "http://127.0.0.1:5509",
    "http://127.0.0.1:5508",
    "http://127.0.0.1:5507",
    "https://zpluse.com",
    "https://www.zpluse.com",
    "http://zpluse.com",
    "http://www.zpluse.com"
})
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegistrationResponse> registerUser(
            @Valid @RequestBody RegistrationRequest registrationRequest) {
        try {
            logger.info("Processing registration request for email: {}", registrationRequest.getEmail());
            userService.registerUser(registrationRequest);
            logger.info("User registration successful for email: {}", registrationRequest.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(new RegistrationResponse(
					true, "Registration successful. Please check your email for verification instructions."));
        } catch (RuntimeException e) {
            logger.error("Registration failed for email: {}", registrationRequest.getEmail(), e);
            return ResponseEntity.badRequest().body(new RegistrationResponse(false, "Registration failed: " + e.getMessage()));
        }
    }

    @PostMapping("/{userId}/profile-photo")
    public ResponseEntity<Map<String, Object>> uploadProfilePhoto(
            @PathVariable Long userId,
            @RequestParam("file") MultipartFile file) {
        try {
            logger.info("Processing profile photo upload for user ID: {}", userId);
            String photoUrl = userService.uploadProfilePhoto(userId, file);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Profile photo uploaded successfully",
                    "photoUrl", photoUrl
            ));
        } catch (RuntimeException e) {
            logger.error("Profile photo upload failed for user ID: {}", userId, e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Upload failed: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserDTO> getUserByEmail(@PathVariable String email) {
        try {
            UserDTO user = userService.getUserByEmail(email);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            logger.error("Failed to get user by email: {}", email, e);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/pending")
    public ResponseEntity<Page<UserDTO>> getPendingRegistrations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<UserDTO> pendingUsers = userService.getPendingRegistrations(pageable);
            return ResponseEntity.ok(pendingUsers);
        } catch (Exception e) {
            logger.error("Failed to get pending registrations", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/registration-stats")
    public ResponseEntity<Map<String, Object>> getRegistrationStats() {
        try {
            Map<String, Object> stats = userService.getRegistrationStatistics();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            logger.error("Failed to get registration statistics", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping
    public ResponseEntity<Page<UserDTO>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) RegistrationStatus status,
            @RequestParam(required = false) UserType userType,
            @RequestParam(required = false) String search) {
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<UserDTO> users = userService.getAllUsers(pageable, status, userType, search);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            logger.error("Failed to get users", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserDTO> updateProfile(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateProfileRequestDto updateRequest) {
        try {
            logger.info("Processing profile update for user ID: {}", userId);
            UserDTO updatedUser = userService.updateProfile(userId, updateRequest);
            logger.info("Profile updated successfully for user ID: {}", userId);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            logger.error("Profile update failed for user ID: {}", userId, e);
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long userId) {
        try {
            logger.info("Processing user deletion for ID: {}", userId);
            userService.deleteUser(userId);
            logger.info("User deleted successfully with ID: {}", userId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "User deleted successfully"
            ));
        } catch (RuntimeException e) {
            logger.error("User deletion failed for ID: {}", userId, e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Deletion failed: " + e.getMessage()
            ));
        }
    }
    
    @GetMapping("/registrations/latest")
    public ResponseEntity<UserDTO> getLatestRegistration() {
        try {
            UserDTO latest = userService.getLatestRegistration();
            if (latest != null) {
                return ResponseEntity.ok(latest);
            } else {
                return ResponseEntity.noContent().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@RequestBody Map<String, String> loginRequest) {
        String selfIdOrUsername = loginRequest.get("selfIdOrUsername");
        String password = loginRequest.get("password");
        LoginResponse response = userService.authenticateUser(selfIdOrUsername, password);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long userId) {
        try {
            UserDTO user = userService.getUserById(userId);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            logger.error("Failed to get user by ID: {}", userId, e);
            return ResponseEntity.notFound().build();
        }
    }
}