package com.zplus.adminpanel.service;

import com.zplus.adminpanel.dto.ContactInquiryRequest;
import com.zplus.adminpanel.entity.ContactSubmission;
import com.zplus.adminpanel.entity.ContactStatus;
import com.zplus.adminpanel.exception.ContactSubmissionException;
import com.zplus.adminpanel.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ContactService {

    @Autowired
    private ContactRepository contactRepository;

    public ContactSubmission saveContactSubmission(ContactInquiryRequest request) {
        ContactSubmission submission = new ContactSubmission();
        submission.setName(request.getName());
        submission.setEmail(request.getEmail());
        submission.setMessage(request.getBusinessChallenge()); // Use businessChallenge as message
        submission.setStatus(ContactStatus.NEW);
        submission.setCreatedAt(LocalDateTime.now());
        
        try {
            return contactRepository.save(submission);
        } catch (Exception e) {
            throw new ContactSubmissionException("Failed to save contact submission", e);
        }
    }

    public ContactSubmission updateContactStatus(Long id, ContactStatus status) {
        ContactSubmission submission = contactRepository.findById(id)
            .orElseThrow(() -> new ContactSubmissionException("Contact submission not found"));
            
        submission.setStatus(status);
        submission.setUpdatedAt(LocalDateTime.now());
        return contactRepository.save(submission);
    }

    public List<ContactSubmission> getAllContactSubmissions() {
        return contactRepository.findAll();
    }
}
