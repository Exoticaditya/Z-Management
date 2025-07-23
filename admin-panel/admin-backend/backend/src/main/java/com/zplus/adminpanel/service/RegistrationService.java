package com.zplus.adminpanel.service;

import com.zplus.adminpanel.entity.Registration;
import java.util.List;

public interface RegistrationService {
    List<Registration> getAllRegistrations();
    
    List<Registration> getPendingRegistrations();
    
    List<Registration> getApprovedRegistrations();
    
    List<Registration> getRejectedRegistrations();
    
    Registration approveRegistration(Long id);
    
    Registration rejectRegistration(Long id, String reason);
    
    Registration shareRegistration(Long id, String sharedWith);
}
