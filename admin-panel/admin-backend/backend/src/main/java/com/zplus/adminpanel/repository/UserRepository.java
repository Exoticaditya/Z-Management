package com.zplus.adminpanel.repository;

import com.zplus.adminpanel.entity.RegistrationStatus;

import com.zplus.adminpanel.entity.User;
import com.zplus.adminpanel.entity.UserType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity operations
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Find operations
    Optional<User> findByEmail(String email);
    
    Optional<User> findBySelfId(String selfId);
    
    Optional<User> findByReferenceId(String referenceId);
    
    Optional<User> findByUsername(String username);
    
    List<User> findByStatus(RegistrationStatus status);
    
    List<User> findByUserType(UserType userType);
    
    List<User> findByDepartment(String department);
    
    Page<User> findByStatus(RegistrationStatus status, Pageable pageable);
    
    Page<User> findByUserType(UserType userType, Pageable pageable);
    
    Page<User> findByDepartment(String department, Pageable pageable);

    // Count operations
    @Query(value = "SELECT COUNT(*) FROM users WHERE status = :status", nativeQuery = true)
    long countByStatus(@Param("status") String status);
    
    // Existence checks
    boolean existsByEmail(String email);
    
    boolean existsBySelfId(String selfId);
    
    boolean existsByReferenceId(String referenceId);

    // Custom queries
    @Query("SELECT u FROM User u WHERE u.firstName LIKE %:keyword% OR u.lastName LIKE %:keyword% OR u.email LIKE %:keyword%")
    Page<User> searchUsers(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.status = :status AND u.userType = :userType")
    List<User> findByStatusAndUserType(@Param("status") RegistrationStatus status, @Param("userType") UserType userType);

    @Query("SELECT u FROM User u WHERE u.createdAt BETWEEN :startDate AND :endDate")
    List<User> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT u FROM User u WHERE u.isActive = true AND u.status = 'APPROVED'")
    List<User> findAllActiveUsers();
    
    @Query(value = "SELECT COUNT(*) FROM users WHERE user_type = :type::user_type", nativeQuery = true)
    long countByUserType(@Param("type") String type);

    @Query(value = "SELECT COUNT(*) FROM users WHERE status = CAST(:status AS registration_status)", nativeQuery = true)
    long countUsersByStatus(@Param("status") String status);
 
    // Additional query methods
    @Query("SELECT u FROM User u WHERE u.selfId = :employeeId")
    Optional<User> findByEmployeeId(@Param("employeeId") String employeeId);

    @Query("SELECT u FROM User u WHERE u.department = :department ORDER BY u.createdAt DESC")
    Page<User> findByDepartmentOrderByCreatedAtDesc(@Param("department") String department, Pageable pageable);

    // Additional query methods needed
    User findTopByOrderByCreatedAtDesc();
    
    @Query("SELECT u FROM User u WHERE " +
           "(:status IS NULL OR u.status = :status) AND " +
           "(:userType IS NULL OR u.userType = :userType) AND " +
           "(:search IS NULL OR " +
           "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<User> findWithFilters(
        @Param("status") RegistrationStatus status,
        @Param("userType") UserType userType,
        @Param("search") String search,
        Pageable pageable);

    @Query("SELECT u.department, COUNT(u) FROM User u WHERE u.status = 'APPROVED' GROUP BY u.department")
    List<Object[]> getUserCountByDepartment();

    @Query("SELECT u FROM User u WHERE u.status = 'PENDING' ORDER BY u.createdAt ASC")
    List<User> findPendingRegistrationsOrderedByDate();

    // Advanced search
    @Query(
    		  value = "SELECT * FROM users " +
    		          "WHERE (:status IS NULL OR status = CAST(:status AS registration_status)) " +
    		          "AND (:userType IS NULL OR user_type = CAST(:userType AS user_type)) " +
    		          "AND (:search IS NULL OR first_name ILIKE CONCAT('%', :search, '%') OR last_name ILIKE CONCAT('%', :search, '%') OR email ILIKE CONCAT('%', :search, '%')) " +
    		          "ORDER BY created_at DESC",
    		  countQuery = "SELECT count(*) FROM users " +
    		               "WHERE (:status IS NULL OR status = CAST(:status AS registration_status)) " +
    		               "AND (:userType IS NULL OR user_type = CAST(:userType AS user_type)) " +
    		               "AND (:search IS NULL OR first_name ILIKE CONCAT('%', :search, '%') OR last_name ILIKE CONCAT('%', :search, '%') OR email ILIKE CONCAT('%', :search, '%'))",
    		  nativeQuery = true
    		)
    		Page<User> findUsersWithFilters(
    		    @Param("status") String status,
    		    @Param("userType") String userType,
    		    @Param("search") String search,
    		    Pageable pageable
    		);
    // Recent activities
    @Query("SELECT u FROM User u WHERE u.lastLogin >= :since ORDER BY u.lastLogin DESC")
    List<User> findRecentlyActiveUsers(@Param("since") LocalDateTime since);

    // Department-wise queries for data sharing
    @Query("SELECT DISTINCT u.department FROM User u WHERE u.status = 'APPROVED'")
    List<String> findAllActiveDepartments();

    @Query("SELECT u FROM User u WHERE u.department = :department AND u.status = 'APPROVED' AND u.isActive = true")
    List<User> findActiveUsersByDepartment(@Param("department") String department);

    // Email list for notifications
    @Query("SELECT u.email FROM User u WHERE u.status = 'APPROVED' AND u.isActive = true")
    List<String> findAllActiveUserEmails();

    @Query("SELECT u.email FROM User u WHERE u.department = :department AND u.status = 'APPROVED' AND u.isActive = true")
    List<String> findActiveUserEmailsByDepartment(@Param("department") String department);

    @Query("SELECT u.email FROM User u WHERE u.userType = :userType AND u.status = 'APPROVED' AND u.isActive = true")
    List<String> findActiveUserEmailsByUserType(@Param("userType") UserType userType);
}
