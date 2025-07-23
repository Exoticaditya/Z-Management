package com.zplus.adminpanel.dto;

import com.zplus.adminpanel.entity.RegistrationStatus;
import com.zplus.adminpanel.entity.User;
import com.zplus.adminpanel.entity.UserType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for displaying user information in admin panel
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Long id;
    private String selfId;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private String phone;
    private String department;
    private UserType userType;
    private String projectId;
    private RegistrationStatus status;
    private String profilePhotoUrl;
    private String reason;
    private String supervisor;
    private String approvedBy;
    private LocalDateTime approvedAt;
    private String rejectionReason;
    private String referenceId;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    private Boolean isActive;

    // Static factory method
    public static UserDTO fromEntity(User user) {
        return new UserDTO(user);
    }

    // Utility methods
    public boolean isApproved() {
        return RegistrationStatus.APPROVED.equals(status);
    }

    public boolean isPending() {
        return RegistrationStatus.PENDING.equals(status);
    }

    public boolean isRejected() {
        return RegistrationStatus.REJECTED.equals(status);
    }

    public String getStatusDisplayName() {
        return status != null ? status.getDisplayName() : "Unknown";
    }

    public String getUserTypeDisplayName() {
        return userType != null ? userType.getDisplayName() : "Unknown";
    }

    public UserDTO(User user) {
        this.id = user.getId();
        this.selfId = user.getSelfId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.fullName = user.getFirstName() + " " + user.getLastName();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.department = user.getDepartment();
        this.userType = user.getUserType();
        this.projectId = user.getProjectId();
        this.status = user.getStatus();
        this.profilePhotoUrl = user.getProfilePhotoUrl();
        this.reason = user.getRejectionReason();
        this.supervisor = user.getSupervisor();
        this.approvedBy = user.getApprovedBy();
        this.approvedAt = user.getApprovedAt();
        this.rejectionReason = user.getRejectionReason();
        this.createdAt = user.getCreatedAt();
        this.lastLogin = user.getLastLogin();
        this.isActive = user.getIsActive();
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "id=" + id +
                ", selfId='" + selfId + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", userType=" + userType +
                ", status=" + status +
                ", department='" + department + '\'' +
                '}';
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setUpdatedAt'");
    }

    public void setRejectedAt(LocalDateTime rejectedAt) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setRejectedAt'");
    }

    public void setRejectedBy(String rejectedBy) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setRejectedBy'");
    }

    public void setPosition(Object position) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setPosition'");
    }
}
