package com.zplus.adminpanel.service;

import com.zplus.adminpanel.dto.ContactInquiryRequest;
import com.zplus.adminpanel.entity.ContactInquiry;
import com.zplus.adminpanel.entity.ContactStatus;
import com.zplus.adminpanel.repository.ContactInquiryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.NoSuchElementException;

/**
 * Service class for handling contact inquiry business logic
 */
@Service
@Transactional
public class ContactInquiryService {
    
    private static final Logger logger = LoggerFactory.getLogger(ContactInquiryService.class);

    private final ContactInquiryRepository contactInquiryRepository;

    public ContactInquiryService(ContactInquiryRepository contactInquiryRepository) {
        this.contactInquiryRepository = contactInquiryRepository;
    }

    /**
     * Save a new contact inquiry
     */
    public ContactInquiry saveContactInquiry(ContactInquiryRequest request) {
        ContactInquiry inquiry = new ContactInquiry();
        inquiry.setFullName(request.getFullName());
        inquiry.setEmail(request.getEmail());
        inquiry.setPhone(request.getPhone());
        inquiry.setOrganization(request.getOrganization());
        inquiry.setCountry(request.getCountry());
        inquiry.setState(request.getState());
        inquiry.setCity(request.getCity());
        inquiry.setAddress(request.getAddress());
        inquiry.setBusinessDuration(request.getBusinessDuration());
        inquiry.setProjectTimeline(request.getProjectTimeline());
        inquiry.setServiceInterests(request.getServiceInterests());
        inquiry.setBusinessChallenge(request.getBusinessChallenge());
        inquiry.setMessage(request.getMessage());
        inquiry.setContactMethod(request.getContactMethod());
        inquiry.setPreferredTime(request.getPreferredTime());
        inquiry.setHearAbout(request.getHowHeard());
        inquiry.setStatus(ContactStatus.NEW);
        inquiry.setCreatedAt(LocalDateTime.now());
        inquiry.setUpdatedAt(LocalDateTime.now());
        
        return contactInquiryRepository.save(inquiry);
    }

    /**
     * Get all contact inquiries based on the provided status, search term, and pagination
     */
    public Page<ContactInquiry> getAllInquiries(ContactStatus status, String searchTerm, Pageable pageable) {
        return contactInquiryRepository.findWithFilters(status, searchTerm, pageable);
    }

    /**
     * Get all contact inquiries (without pagination)
     */
    public List<ContactInquiry> getAllInquiriesSimple() {
        return contactInquiryRepository.findAll();
    }

    /**
     * Get contact inquiries by status (without pagination)
     */
    public List<ContactInquiry> getInquiriesByStatus(ContactStatus status) {
        return contactInquiryRepository.findByStatus(status);
    }

    /**
     * Get a contact inquiry by ID
     */
    public ContactInquiry getInquiryById(Long id) {
        return contactInquiryRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Contact inquiry not found with id: " + id));
    }

    /**
     * Update inquiry status
     */
    public ContactInquiry updateInquiryStatus(Long id, ContactStatus status, String notes) {
        ContactInquiry inquiry = getInquiryById(id);
        inquiry.setStatus(status);
        if (notes != null) {
            inquiry.setNotes(notes);
        }
        inquiry.setUpdatedAt(LocalDateTime.now());
        return contactInquiryRepository.save(inquiry);
    }

    /**
     * Assign inquiry to a team member
     */
    public ContactInquiry assignInquiry(Long id, String assignedTo) {
        try {
            ContactInquiry inquiry = getInquiryById(id);
            if (inquiry == null) {
                logger.error("Contact inquiry not found with id: {}", id);
                return null;
            }
            
            logger.info("Assigning inquiry {} to user: {}", id, assignedTo);
            inquiry.setAssignedTo(assignedTo);
            inquiry.setStatus(ContactStatus.IN_PROGRESS); // Update status when assigned
            inquiry.setUpdatedAt(LocalDateTime.now());
            
            // Force initialization of serviceInterests to avoid lazy loading issues
            if (inquiry.getServiceInterests() != null) {
                inquiry.getServiceInterests().size(); // This triggers initialization
            }
            
            ContactInquiry saved = contactInquiryRepository.save(inquiry);
            logger.info("Successfully assigned inquiry {} to {}", id, assignedTo);
            return saved;
            
        } catch (Exception e) {
            logger.error("Error assigning inquiry {} to {}: {}", id, assignedTo, e.getMessage(), e);
            throw new RuntimeException("Failed to assign inquiry: " + e.getMessage(), e);
        }
    }

    /**
     * Add notes to inquiry
     */
    public ContactInquiry addNotes(Long id, String notes) {
        ContactInquiry inquiry = getInquiryById(id);
        inquiry.setNotes(notes);
        inquiry.setUpdatedAt(LocalDateTime.now());
        return contactInquiryRepository.save(inquiry);
    }

    /**
     * Mark inquiry as responded
     */
    public ContactInquiry markAsResponded(Long id, String responseNotes) {
        ContactInquiry inquiry = getInquiryById(id);
        inquiry.setStatus(ContactStatus.CLOSED);
        inquiry.setResponseNotes(responseNotes);
        inquiry.setUpdatedAt(LocalDateTime.now());
        return contactInquiryRepository.save(inquiry);
    }

    /**
     * Share contact inquiry with project ID or email
     */
    public ContactInquiry shareInquiry(Long id, String sharedWith, String shareNotes) {
        ContactInquiry inquiry = getInquiryById(id);
        if (inquiry == null) {
            throw new NoSuchElementException("Contact inquiry not found with id: " + id);
        }
        
        // Set sharing information
        inquiry.setSharedWith(sharedWith);
        inquiry.setSharedAt(LocalDateTime.now());
        inquiry.setShareNotes(shareNotes);
        inquiry.setUpdatedAt(LocalDateTime.now());
        
        // You can add logic here to determine who is sharing based on authentication context
        // For now, we'll set it as "system" or get it from security context
        inquiry.setSharedBy("admin"); // This should ideally come from the authenticated user
        
        return contactInquiryRepository.save(inquiry);
    }

    /**
     * Get inquiries by email
     */
    public List<ContactInquiry> getInquiriesByEmail(String email) {
        return contactInquiryRepository.findByEmail(email);
    }

    /**
     * Get inquiries by date range
     */
    public List<ContactInquiry> getInquiriesByDateRange(LocalDateTime start, LocalDateTime end) {
        return contactInquiryRepository.findByCreatedAtBetween(start, end);
    }

    /**
     * Get inquiry statistics
     */
    public Map<String, Object> getInquiryStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", contactInquiryRepository.count());
        stats.put("byStatus", contactInquiryRepository.getStatusCounts());
        return stats;
    }

    /**
     * Get recent inquiries
     */
    public List<ContactInquiry> getRecentInquiries(int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        return contactInquiryRepository.findRecentInquiries(since);
    }

    /**
     * Get unassigned inquiries
     */
    public List<ContactInquiry> getUnassignedInquiries() {
        return contactInquiryRepository.findUnassignedInquiries();
    }

    /**
     * Get high priority inquiries
     */
    public List<ContactInquiry> getHighPriorityInquiries() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(24);
        return contactInquiryRepository.findHighPriorityInquiries(cutoff);
    }

    /**
     * Delete inquiry
     */
    public boolean deleteInquiry(Long id) {
        if (contactInquiryRepository.existsById(id)) {
            contactInquiryRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Get latest contact inquiry
     */
    public ContactInquiry getLatestContactInquiry() {
        return contactInquiryRepository.findTopByOrderByCreatedAtDesc();
    }

    /**
     * Get all contact inquiries
     */
    public List<ContactInquiry> getAllContactInquiries() {
        return contactInquiryRepository.findAll();
    }

    /**
     * Get contact inquiries assigned to a specific employee
     */
    public List<ContactInquiry> getInquiriesAssignedTo(String assignedTo) {
        return contactInquiryRepository.findByAssignedTo(assignedTo);
    }
}