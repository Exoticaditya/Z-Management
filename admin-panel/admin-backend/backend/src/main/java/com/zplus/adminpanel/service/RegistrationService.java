package com.zplus.adminpanel.service;

import com.zplus.adminpanel.dto.RegistrationRequest;
import com.zplus.adminpanel.entity.Registration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface RegistrationService {
    Registration saveRegistration(RegistrationRequest request);
    
    List<Registration> getAllRegistrations();
    
    Page<Registration> getAllRegistrations(Pageable pageable);
    
    List<Registration> getPendingRegistrations();
    
    List<Registration> getApprovedRegistrations();
    
    List<Registration> getRejectedRegistrations();
    
    Registration getRegistrationById(Long id);
    
    Registration approveRegistration(Long id);
    
    Registration rejectRegistration(Long id, String reason);
    
    Registration shareRegistration(Long id, String sharedWith);
    
    // Helper methods for password migration
    String encodePassword(String plainPassword);
    
    Registration updateRegistration(Registration registration);
    
    void deleteRegistration(Long id);
}
