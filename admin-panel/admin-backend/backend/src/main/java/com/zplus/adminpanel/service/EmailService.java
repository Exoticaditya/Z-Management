package com.zplus.adminpanel.service;

import com.zplus.adminpanel.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

/**
 * Service for handling email notifications
 */
@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromEmail;

    @Value("${app.mail.admin-email}")
    private String adminEmail;

    @Value("${app.organization.name:Z+ Admin Panel}")
    private String organizationName;

    /**
     * Send registration confirmation email to user
     */
    public void sendRegistrationConfirmation(User user) {
        try {
            logger.info("Sending registration confirmation email to: {}", user.getEmail());

            String subject = "Registration Confirmation - " + organizationName;
            String message = String.format(
                "Dear %s,\n\n" +
                "Thank you for registering with %s. Your registration has been received and is pending approval.\n\n" +
                "Registration Details:\n" +
                "- Name: %s %s\n" +
                "- Email: %s\n" +
                "- Department: %s\n" +
                "- User Type: %s\n\n" +
                "You will receive another email once your registration is approved.\n\n" +
                "Best regards,\n%s Team",
                user.getFirstName(),
                organizationName,
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getDepartment(),
                user.getUserType(),
                organizationName
            );

            sendNotificationEmail(user.getEmail(), subject, message);
            logger.info("Registration confirmation email sent successfully to: {}", user.getEmail());

            // Also notify admin about new registration
            notifyAdminOfNewRegistration(user);

        } catch (Exception ex) {
            logger.error("Failed to send registration confirmation email to: {}", user.getEmail(), ex);
            // Don't throw exception - registration should still succeed even if email fails
        }
    }

    /**
     * Send approval notification email to user
     */
    public void sendApprovalNotification(User user) {
        try {
            logger.info("Sending approval notification email to: {}", user.getEmail());

            String subject = "Registration Approved - Welcome to " + organizationName;
            String message = String.format(
                "Dear %s,\n\n" +
                "Congratulations! Your registration with %s has been approved.\n\n" +
                "You can now access the system with your registered email and password.\n\n" +
                "Best regards,\n%s Team",
                user.getFirstName(),
                organizationName,
                organizationName
            );

            sendNotificationEmail(user.getEmail(), subject, message);
            logger.info("Approval notification email sent successfully to: {}", user.getEmail());

        } catch (Exception ex) {
            logger.error("Failed to send approval notification email to: {}", user.getEmail(), ex);
        }
    }

    /**
     * Send rejection notification email to user
     */
    public void sendRejectionNotification(User user, String rejectionReason) {
        try {
            logger.info("Sending rejection notification email to: {}", user.getEmail());

            String subject = "Registration Update - " + organizationName;
            String message = String.format(
                "Dear %s,\n\n" +
                "We regret to inform you that your registration with %s has been rejected.\n\n" +
                "Reason: %s\n\n" +
                "If you have any questions, please contact us at %s.\n\n" +
                "Best regards,\n%s Team",
                user.getFirstName(),
                organizationName,
                rejectionReason,
                fromEmail,
                organizationName
            );

            sendNotificationEmail(user.getEmail(), subject, message);
            logger.info("Rejection notification email sent successfully to: {}", user.getEmail());

        } catch (Exception ex) {
            logger.error("Failed to send rejection notification email to: {}", user.getEmail(), ex);
        }
    }

    /**
     * Send password reset email
     */
    public void sendPasswordResetEmail(User user, String resetToken) {
        try {
            logger.info("Sending password reset email to: {}", user.getEmail());

            String subject = "Password Reset Request - " + organizationName;
            String message = String.format(
                "Dear %s,\n\n" +
                "You have requested a password reset for your account with %s.\n\n" +
                "Reset Token: %s\n\n" +
                "If you did not request this reset, please ignore this email.\n\n" +
                "Best regards,\n%s Team",
                user.getFirstName(),
                organizationName,
                resetToken,
                organizationName
            );

            sendNotificationEmail(user.getEmail(), subject, message);
            logger.info("Password reset email sent successfully to: {}", user.getEmail());

        } catch (Exception ex) {
            logger.error("Failed to send password reset email to: {}", user.getEmail(), ex);
            throw new RuntimeException("Failed to send password reset email", ex);
        }
    }

    /**
     * Notify admin of new registration
     */
    private void notifyAdminOfNewRegistration(User user) {
        try {
            logger.info("Notifying admin of new registration for user: {}", user.getEmail());

            String subject = "New Registration Pending Approval - " + organizationName;
            String message = String.format(
                "A new user registration requires approval:\n\n" +
                "User Details:\n" +
                "- Name: %s %s\n" +
                "- Email: %s\n" +
                "- Department: %s\n" +
                "- User Type: %s\n" +
                "- Registration Date: %s\n\n" +
                "Please review and approve/reject this registration.\n\n" +
                "Best regards,\n%s Team",
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getDepartment(),
                user.getUserType(),
                user.getCreatedAt().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")),
                organizationName
            );

            sendNotificationEmail(adminEmail, subject, message);
            logger.info("Admin notification email sent successfully for user: {}", user.getEmail());

        } catch (Exception ex) {
            logger.error("Failed to send admin notification email for user: {}", user.getEmail(), ex);
            // Don't throw exception - this is just a notification
        }
    }

    /**
     * Send general notification email
     */
    public void sendNotificationEmail(String toEmail, String subject, String message) {
        try {
            logger.info("Sending notification email to: {}", toEmail);

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(toEmail);
            mailMessage.setFrom(fromEmail);
            mailMessage.setSubject(subject);
            mailMessage.setText(message);

            mailSender.send(mailMessage);
            logger.info("Notification email sent successfully to: {}", toEmail);

        } catch (MailException ex) {
            logger.error("Failed to send notification email to: {}", toEmail, ex);
            throw new RuntimeException("Failed to send notification email", ex);
        }
    }

    /**
     * Send bulk email to multiple recipients
     */
    public void sendBulkEmail(String[] toEmails, String subject, String message) {
        try {
            logger.info("Sending bulk email to {} recipients", toEmails.length);

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(toEmails);
            mailMessage.setFrom(fromEmail);
            mailMessage.setSubject(subject);
            mailMessage.setText(message);

            mailSender.send(mailMessage);
            logger.info("Bulk email sent successfully to {} recipients", toEmails.length);

        } catch (MailException ex) {
            logger.error("Failed to send bulk email", ex);
            throw new RuntimeException("Failed to send bulk email", ex);
        }
    }

    /**
     * Test email configuration
     */
    public void sendTestEmail(String toEmail) {
        try {
            logger.info("Sending test email to: {}", toEmail);

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(toEmail);
            mailMessage.setFrom(fromEmail);
            mailMessage.setSubject("Test Email - " + organizationName);
            mailMessage.setText("This is a test email to verify email configuration is working correctly.");

            mailSender.send(mailMessage);
            logger.info("Test email sent successfully to: {}", toEmail);

        } catch (MailException ex) {
            logger.error("Failed to send test email to: {}", toEmail, ex);
            throw new RuntimeException("Failed to send test email", ex);
        }
    }

    /**
     * Send contact inquiry notification email to admin
     */
    public void sendContactInquiryNotification(com.zplus.adminpanel.entity.ContactInquiry inquiry) {
        try {
            logger.info("Sending contact inquiry notification for inquiry ID: {}", inquiry.getId());

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(fromEmail);
            mailMessage.setTo("zpluse47@gmail.com"); // Send to Z+ email
            mailMessage.setSubject("New Contact Inquiry - " + inquiry.getFullName());
            
            String serviceInterests = "";
            if (inquiry.getServiceInterests() != null && !inquiry.getServiceInterests().isEmpty()) {
                serviceInterests = String.join(", ", inquiry.getServiceInterests());
            }
            
            String message = String.format(
                "New Contact Inquiry Received!\n\n" +
                "CONTACT DETAILS:\n" +
                "Name: %s\n" +
                "Organization: %s\n" +
                "Email: %s\n" +
                "Phone: %s\n\n" +
                "LOCATION:\n" +
                "Address: %s\n" +
                "City: %s\n" +
                "State: %s\n" +
                "Country: %s\n\n" +
                "BUSINESS DETAILS:\n" +
                "Business Duration: %s\n" +
                "Project Timeline: %s\n" +
                "Service Interests: %s\n\n" +
                "BUSINESS CHALLENGE/GOAL:\n" +
                "%s\n\n" +
                "CONTACT PREFERENCES:\n" +
                "Preferred Method: %s\n" +
                "Preferred Time: %s\n" +
                "How They Heard About Us: %s\n\n" +
                "Inquiry ID: %d\n" +
                "Received: %s\n\n" +
                "---\n" +
                "Z+ Management System\n" +
                "Please respond to this inquiry promptly.",
                
                inquiry.getFullName(),
                inquiry.getOrganization() != null ? inquiry.getOrganization() : "Not specified",
                inquiry.getEmail(),
                inquiry.getPhone(),
                inquiry.getAddress() != null ? inquiry.getAddress() : "Not provided",
                inquiry.getCity(),
                inquiry.getState(),
                inquiry.getCountry(),
                inquiry.getBusinessDuration() != null ? inquiry.getBusinessDuration() : "Not specified",
                inquiry.getProjectTimeline() != null ? inquiry.getProjectTimeline() : "Not specified",
                serviceInterests.isEmpty() ? "Not specified" : serviceInterests,
                inquiry.getBusinessChallenge(),
                inquiry.getContactMethod(),
                inquiry.getPreferredTime() != null ? inquiry.getPreferredTime() : "Anytime",
                inquiry.getHearAbout() != null ? inquiry.getHearAbout() : "Not specified",
                inquiry.getId(),
                inquiry.getCreatedAt() != null ? inquiry.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "Unknown"
            );

            mailMessage.setText(message);
            mailSender.send(mailMessage);
            
            logger.info("Contact inquiry notification sent successfully for inquiry ID: {}", inquiry.getId());

        } catch (MailException ex) {
            logger.error("Failed to send contact inquiry notification for inquiry ID: {}", inquiry.getId(), ex);
            throw new RuntimeException("Failed to send contact inquiry notification", ex);
        }
    }
}
