package com.zplus.adminpanel.service;

import com.zplus.adminpanel.dto.*;
import com.zplus.adminpanel.entity.RegistrationStatus;
import com.zplus.adminpanel.entity.UserType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface UserService {
    RegistrationResponse registerUser(RegistrationRequest registrationRequest);
    LoginResponse authenticateUser(String selfIdOrUsername, String password);
    
    String uploadProfilePhoto(Long userId, MultipartFile file);
    UserDTO getUserByEmail(String email);
    Page<UserDTO> getPendingRegistrations(Pageable pageable);
    Map<String, Object> getRegistrationStatistics();
    Page<UserDTO> getAllUsers(Pageable pageable, RegistrationStatus status, UserType userType, String search);
    UserDTO updateProfile(Long userId, UpdateProfileRequestDto updateRequest);
    void deleteUser(Long userId);
    UserDTO getLatestRegistration();
    UserDTO getUserById(Long userId);
}