package com.trackmyexpense.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * ONE-TIME UTILITY - not a Spring bean, not wired into the app.
 *
 * Run this standalone (right-click > Run in your IDE, or `mvn compile exec:java`)
 * to generate a BCrypt hash for whatever password you choose. Copy the printed
 * hash into application.properties as app.security.password-hash, then you can
 * delete this file - it has no runtime role in the application.
 *
 * Usage: edit RAW_PASSWORD below, run main(), copy the output.
 */
public class PasswordHashGenerator {

    private static final String RAW_PASSWORD = "Karthik2004"; // <-- change this before running

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = encoder.encode(RAW_PASSWORD);
        System.out.println("Password hash for '" + RAW_PASSWORD + "':");
        System.out.println(hash);
    }
}
