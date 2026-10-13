package com.trackmyexpense.entity;

/**
 * What a currently-stored OTP on a User is for. Prevents a leftover
 * registration OTP from being usable to reset a password, and vice versa.
 */
public enum OtpPurpose {
    REGISTRATION,
    PASSWORD_RESET
}
