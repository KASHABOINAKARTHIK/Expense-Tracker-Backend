package com.trackmyexpense.exception;

/**
 * Covers both "OTP doesn't match" and "OTP expired" - kept as one type since
 * the client-facing handling is the same (ask the user to request a new one),
 * but the message differentiates the two cases.
 */
public class InvalidOtpException extends RuntimeException {
    public InvalidOtpException(String message) {
        super(message);
    }
}
