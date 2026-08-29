package com.trackmyexpense.exception;

/**
 * Thrown when the "month" query parameter is not in the expected yyyy-MM format.
 * Mapped to HTTP 400 by GlobalExceptionHandler.
 */
public class InvalidMonthFormatException extends RuntimeException {
    public InvalidMonthFormatException(String month) {
        super("Invalid month format: '" + month + "'. Expected format is yyyy-MM, e.g. 2026-08");
    }
}
