package com.zplus.adminpanel.repository;

import com.zplus.adminpanel.entity.Registration;
import com.zplus.adminpanel.entity.RegistrationStatus;
import com.zplus.adminpanel.entity.UserType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    
    // Basic queries
    List<Registration> findByStatus(RegistrationStatus status);
    Page<Registration> findByStatus(RegistrationStatus status, Pageable pageable);
    Optional<Registration> findByEmail(String email);
    Optional<Registration> findBySelfId(String selfId);
    
    // Count by status
    long countByStatus(RegistrationStatus status);
    
    // Complex filtering query
    @Query("SELECT r FROM Registration r WHERE " +
           "(:status IS NULL OR r.status = :status) AND " +
           "(:userType IS NULL OR r.userType = :userType) AND " +
           "(:search IS NULL OR LOWER(r.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(r.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(r.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(r.selfId) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Registration> findWithFilters(@Param("status") RegistrationStatus status,
                                     @Param("userType") UserType userType,
                                     @Param("search") String search,
                                     Pageable pageable);
    
    // Get latest registration
    Registration findTopByOrderByCreatedAtDesc();
}