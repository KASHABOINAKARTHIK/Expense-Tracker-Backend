package com.trackmyexpense.exception;

/**
 * Thrown when an expense lookup by ID fails.
 * Mapped to HTTP 404 by GlobalExceptionHandler.
 */
public class ExpenseNotFoundException extends RuntimeException {

    public ExpenseNotFoundException(Long id) {
        super("Expense not found with id: " + id);
    }

    public ExpenseNotFoundException(String message) {
        super(message);
    }
}
