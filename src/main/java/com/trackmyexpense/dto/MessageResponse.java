package com.trackmyexpense.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic { "message": "..." } response for endpoints that don't return a
 * resource - register, verify-otp, resend-otp, forgot-password, reset-password, change-password.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageResponse {
    private String message;
}
