package com.trackmyexpense.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * Sends OTP emails via the Brevo HTTPS API.
 *
 * Public method signatures are unchanged so other classes
 * such as UserServiceImpl do not need to change.
 */
@Service
@Slf4j
public class EmailService {
	
	

    private static final String BREVO_API_URL =
            "https://api.brevo.com/v3/smtp/email";

    private final RestTemplate restTemplate;
    private final String apiKey;
    private final String fromEmail;
    private final String fromName;

    public EmailService(
            @Value("${brevo.api-key}") String apiKey,
            @Value("${brevo.from-email}") String fromEmail,
            @Value("${brevo.from-name:Track My Expenses}") String fromName) {

        this.apiKey = apiKey;
        this.fromEmail = fromEmail;
        this.fromName = fromName;
        this.restTemplate = new RestTemplate();

    }

    public void sendRegistrationOtp(String toEmail, String otp) {

        send(
                toEmail,
                "Verify your Track My Expenses account",
                "Your verification code is: " + otp + "\n\n" +
                        "This code expires in 10 minutes. " +
                        "If you didn't create an account, you can ignore this email."
        );
    }

    public void sendPasswordResetOtp(String toEmail, String otp) {

        send(
                toEmail,
                "Reset your Track My Expenses password",
                "Your password reset code is: " + otp + "\n\n" +
                        "This code expires in 10 minutes. " +
                        "If you didn't request a password reset, you can ignore this email."
        );
    }

    private void send(String toEmail, String subject, String text) {

        try {

            HttpHeaders headers = new HttpHeaders();

            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));

            // Brevo API authentication
            headers.set("api-key", apiKey);

            Map<String, Object> sender = Map.of(
                    "name", fromName,
                    "email", fromEmail
            );

            Map<String, Object> recipient = Map.of(
                    "email", toEmail
            );

            Map<String, Object> requestBody = Map.of(
                    "sender", sender,
                    "to", List.of(recipient),
                    "subject", subject,
                    "textContent", text
            );

            HttpEntity<Map<String, Object>> request =
                    new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response =
                    restTemplate.postForEntity(
                            BREVO_API_URL,
                            request,
                            String.class
                    );

            if (response.getStatusCode().is2xxSuccessful()) {

                log.info(
                        "Email sent via Brevo. to={}, subject={}",
                        toEmail,
                        subject
                );

            } else {

                log.error(
                        "Brevo returned unexpected status. status={}, response={}",
                        response.getStatusCode(),
                        response.getBody()
                );

                throw new RuntimeException(
                        "Failed to send email. Please try again later."
                );
            }

        } catch (RestClientException ex) {

            log.error(
                    "Failed to send email via Brevo. to={}, subject={}, error={}",
                    toEmail,
                    subject,
                    ex.getMessage()
            );

            throw new RuntimeException(
                    "Failed to send email. Please try again later."
            );
        }
    }
}