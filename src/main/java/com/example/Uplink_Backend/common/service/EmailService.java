package com.example.Uplink_Backend.common.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String mailFrom;

    public void sendEmail(String to, String subject, String body) {
        if (mailSender != null && mailFrom != null && !mailFrom.isBlank()) {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(mailFrom);
                message.setTo(to);
                message.setSubject(subject);
                message.setText(body);
                mailSender.send(message);
                logger.info("Email sent successfully to {}", to);
                return;
            } catch (Exception ex) {
                logger.warn("Failed to send real email to {}: {}. Falling back to simulation.", to, ex.getMessage());
            }
        }

        // Resilient fallback / simulation mode
        logger.info("\n========== [EMAIL SERVICE SIMULATION] ==========\nTo: {}\nSubject: {}\nContent:\n{}\n================================================",
                to, subject, body);
    }

    public void sendVerificationEmail(String to, String token) {
        String link = "https://uplink.edu/auth/verify?token=" + token;
        String subject = "Verify your Uplink Account";
        String body = "Welcome to Uplink Platform!\n\nPlease verify your email address by clicking the link below:\n" + link + "\n\nIf you did not create an account, please ignore this email.";
        sendEmail(to, subject, body);
    }

    public void sendPasswordResetEmail(String to, String token) {
        String link = "https://uplink.edu/auth/reset-password?token=" + token;
        String subject = "Uplink - Password Reset Request";
        String body = "You requested to reset your password.\n\nPlease click the following link to set a new password:\n" + link + "\n\nThis link is valid for 1 hour.";
        sendEmail(to, subject, body);
    }

    public void sendApplicationStatusEmail(String to, String opportunityTitle, String status, String remarks) {
        String subject = "Uplink - Application Status Update: " + opportunityTitle;
        String body = String.format("Hello,\n\nYour application for '%s' has been updated to: %s.\n\nRemarks: %s\n\nBest regards,\nUplink Placement Team",
                opportunityTitle, status, remarks != null ? remarks : "None");
        sendEmail(to, subject, body);
    }
}
