package com.trackmyexpense.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String email) {
        super("No account found with email: " + email);
    }

    public UserNotFoundException(Long id) {
        super("No account found with id: " + id);
    }
}
