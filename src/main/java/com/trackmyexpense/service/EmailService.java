package com.trackmyexpense.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Sends OTP emails via Spring Mail. SMTP settings come from application.properties
 * (spring.mail.*), which should be backed by environment variables in production.
 */
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromAddress;

    public void sendRegistrationOtp(String toEmail, String otp) {
        send(toEmail, "Verify your Track My Expenses account",
                "Your verification code is: " + otp + "\n\n" +
                "This code expires in 10 minutes. If you didn't create an account, you can ignore this email.");
    }

    public void sendPasswordResetOtp(String toEmail, String otp) {
        send(toEmail, "Reset your Track My Expenses password",
                "Your password reset code is: " + otp + "\n\n" +
                "This code expires in 10 minutes. If you didn't request a password reset, you can ignore this email.");
    }

    private void send(String toEmail, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}
