package com.zplus.adminpanel.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class EmailServiceTest {

    @Autowired
    private EmailService emailService;

    @Autowired
    private JavaMailSender mailSender;

    @Test
    void testMailSenderConfiguration() {
        assertNotNull(mailSender, "JavaMailSender should be configured");
    }

    @Test
    void testEmailServiceInjection() {
        assertNotNull(emailService, "EmailService should be injected");
    }

    // Uncomment and modify this test when you have valid email credentials
    /*
    @Test
    void testSendWelcomeEmail() {
        try {
            emailService.sendWelcomeEmail("test@example.com", "Test User");
            // If no exception is thrown, email was sent successfully
            assertTrue(true, "Email sent successfully");
        } catch (Exception e) {
            fail("Email sending failed: " + e.getMessage());
        }
    }
    */

    // Uncomment and modify this test when you have valid email credentials
    /*
    @Test
    void testSendApprovalEmail() {
        try {
            emailService.sendApprovalEmail("test@example.com", "Test User");
            assertTrue(true, "Approval email sent successfully");
        } catch (Exception e) {
            fail("Approval email sending failed: " + e.getMessage());
        }
    }
    */
}
