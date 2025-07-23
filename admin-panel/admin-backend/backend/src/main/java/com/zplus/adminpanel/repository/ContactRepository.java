package com.zplus.adminpanel.repository;

import com.zplus.adminpanel.entity.ContactSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactRepository extends JpaRepository<ContactSubmission, Long> {
}
