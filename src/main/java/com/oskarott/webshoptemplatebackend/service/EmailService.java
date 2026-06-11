package com.oskarott.webshoptemplatebackend.service;

import com.oskarott.webshoptemplatebackend.email.EmailTemplates;
import com.oskarott.webshoptemplatebackend.model.Order;
import com.oskarott.webshoptemplatebackend.model.UserEntity;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${mail.from}")
    private String from;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendWelcomeEmail(UserEntity user) {
        try {
            send(
                    user.getEmail(),
                    EmailTemplates.welcomeSubject(),
                    EmailTemplates.welcomeHtml(user.getFirstName())
            );
        } catch (Exception e) {
            log.error("Failed to send welcome email to {}: {}", user.getEmail(), e.getMessage(), e);
        }
    }

    public void sendPasswordResetEmail(UserEntity user, String resetLink) {
        try {
            send(
                    user.getEmail(),
                    EmailTemplates.resetPasswordSubject(),
                    EmailTemplates.resetPasswordHtml(user.getFirstName(), resetLink)
            );
        } catch (Exception e) {
            log.error("Failed to send password reset email to {}: {}", user.getEmail(), e.getMessage(), e);
        }
    }

    public void sendOrderConfirmation(Order order) {
        String recipient = order.getUser().getEmail();
        try {
            send(
                    recipient,
                    EmailTemplates.orderConfirmationSubject(order.getId()),
                    EmailTemplates.orderConfirmationHtml(order)
            );
        } catch (Exception e) {
            log.error("Failed to send order confirmation to {} for order {}: {}",
                    recipient, order.getId(), e.getMessage(), e);
        }
    }

    private void send(String to, String subject, String htmlBody) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);
        mailSender.send(message);
        log.debug("Email '{}' sent to {}", subject, to);
    }
}
