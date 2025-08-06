package com.zplus.adminpanel.repository;

import java.time.LocalDateTime;
import java.util.List;
import com.zplus.adminpanel.entity.ContactInquiry;
import com.zplus.adminpanel.entity.ContactStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

/**
 * Repository interface for ContactInquiry entity
 */
@Repository
public interface ContactInquiryRepository extends JpaRepository<ContactInquiry, Long> {

    // Find operations
    List<ContactInquiry> findByStatus(ContactStatus status);
    
    List<ContactInquiry> findByEmail(String email);
    
    Page<ContactInquiry> findByStatus(ContactStatus status, Pageable pageable);
    
    Page<ContactInquiry> findByEmailContainingIgnoreCase(String email, Pageable pageable);
    
    Page<ContactInquiry> findByFullNameContainingIgnoreCase(String fullName, Pageable pageable);

    // Date-based queries
    List<ContactInquiry> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    List<ContactInquiry> findByCreatedAtAfter(LocalDateTime date);
    
    List<ContactInquiry> findByCreatedAtBefore(LocalDateTime date);

    // Service interest queries
    @Query("SELECT cs FROM ContactInquiry cs JOIN cs.serviceInterests si WHERE si = :serviceInterest")
    List<ContactInquiry> findByServiceInterest(@Param("serviceInterest") String serviceInterest);

    // Location-based queries
    List<ContactInquiry> findByCountry(String country);
    
    List<ContactInquiry> findByState(String state);
    
    List<ContactInquiry> findByCity(String city);
    
    List<ContactInquiry> findByCountryAndState(String country, String state);

    // Status and assignment queries
    List<ContactInquiry> findByStatusAndAssignedTo(ContactStatus status, String assignedTo);
    
    List<ContactInquiry> findByAssignedTo(String assignedTo);

    // Existence checks
    boolean existsByEmail(String email);
    
    long countByStatus(ContactStatus status);
    
    ContactInquiry findTopByOrderByCreatedAtDesc();
    
    // Get top 5 recent contact inquiries
    List<ContactInquiry> findTop5ByOrderByCreatedAtDesc();

    // Custom queries
    @Query("SELECT cs FROM ContactInquiry cs WHERE " +
           "(:status IS NULL OR cs.status = :status) AND " +
           "(:search IS NULL OR cs.fullName LIKE %:search% OR cs.email LIKE %:search% OR cs.organization LIKE %:search%)")
    Page<ContactInquiry> findWithFilters(
            @Param("status") ContactStatus status,
            @Param("search") String search,
            Pageable pageable);

    // Statistics queries
    @Query("SELECT cs.status, COUNT(cs) FROM ContactInquiry cs GROUP BY cs.status")
    List<Object[]> getStatusCounts();

    @Query("SELECT cs.country, COUNT(cs) FROM ContactInquiry cs GROUP BY cs.country ORDER BY COUNT(cs) DESC")
    List<Object[]> getCountryCounts();

    @Query("SELECT cs.state, COUNT(cs) FROM ContactInquiry cs WHERE cs.country = :country GROUP BY cs.state ORDER BY COUNT(cs) DESC")
    List<Object[]> getStateCountsByCountry(@Param("country") String country);

    @Query("SELECT cs.city, COUNT(cs) FROM ContactInquiry cs WHERE cs.state = :state GROUP BY cs.city ORDER BY COUNT(cs) DESC")
    List<Object[]> getCityCountsByState(@Param("state") String state);

    // Recent activities
    @Query("SELECT cs FROM ContactInquiry cs WHERE cs.createdAt >= :since ORDER BY cs.createdAt DESC")
    List<ContactInquiry> findRecentInquiries(@Param("since") LocalDateTime since);

    // Unassigned inquiries
    @Query("SELECT cs FROM ContactInquiry cs WHERE cs.assignedTo IS NULL OR cs.assignedTo = ''")
    List<ContactInquiry> findUnassignedInquiries();

    // High priority inquiries (new inquiries older than 24 hours)
    @Query("SELECT cs FROM ContactInquiry cs WHERE cs.status = 'NEW' AND cs.createdAt < :cutoffTime")
    List<ContactInquiry> findHighPriorityInquiries(@Param("cutoffTime") LocalDateTime cutoffTime);

    // Service interest statistics
    @Query("SELECT si, COUNT(cs) FROM ContactInquiry cs JOIN cs.serviceInterests si GROUP BY si ORDER BY COUNT(cs) DESC")
    List<Object[]> getServiceInterestCounts();

    // Contact method statistics
    @Query("SELECT cs.contactMethod, COUNT(cs) FROM ContactInquiry cs GROUP BY cs.contactMethod")
    List<Object[]> getContactMethodCounts();

    // Hear about statistics
    @Query("SELECT cs.hearAbout, COUNT(cs) FROM ContactInquiry cs WHERE cs.hearAbout IS NOT NULL GROUP BY cs.hearAbout ORDER BY COUNT(cs) DESC")
    List<Object[]> getHearAboutCounts();

    // Business duration statistics
    @Query("SELECT cs.businessDuration, COUNT(cs) FROM ContactInquiry cs WHERE cs.businessDuration IS NOT NULL GROUP BY cs.businessDuration")
    List<Object[]> getBusinessDurationCounts();

    // Project timeline statistics
    @Query("SELECT cs.projectTimeline, COUNT(cs) FROM ContactInquiry cs WHERE cs.projectTimeline IS NOT NULL GROUP BY cs.projectTimeline")
    List<Object[]> getProjectTimelineCounts();
}