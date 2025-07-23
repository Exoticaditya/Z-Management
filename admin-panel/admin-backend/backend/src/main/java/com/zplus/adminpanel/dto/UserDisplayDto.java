package com.zplus.adminpanel.dto;

import com.zplus.adminpanel.entity.RegistrationStatus;
import com.zplus.adminpanel.entity.UserType;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO for displaying user information in admin panel
 */
@Data
public class UserDisplayDto {

    private Long id;
    private String employeeId;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String department;
    private String position;
    private UserType userType;
    private RegistrationStatus registrationStatus;
    private String profilePhotoUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String approvedBy;
    private LocalDateTime approvedAt;
    private String rejectedBy;
    private LocalDateTime rejectedAt;
    private String rejectionReason;

    @Override
    public String toString() {
        return "UserDisplayDto{" +
                "id=" + id +
                ", employeeId='" + employeeId + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", userType=" + userType +
                ", registrationStatus=" + registrationStatus +
                ", department='" + department + '\'' +
                '}';
    }
}