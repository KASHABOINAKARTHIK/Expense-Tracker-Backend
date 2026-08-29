package com.trackmyexpense.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * This application has exactly one user (the owner). Credentials live in
 * application.properties (app.security.username / app.security.password-hash),
 * which should be supplied via environment variables in production - no users
 * table, no registration flow, nothing for an attacker to enumerate.
 */
@Service
public class SingleUserDetailsService implements UserDetailsService {

    private final String configuredUsername;
    private final String configuredPasswordHash;

    public SingleUserDetailsService(
            @Value("${app.security.username}") String configuredUsername,
            @Value("${app.security.password-hash}") String configuredPasswordHash) {
        this.configuredUsername = configuredUsername;
        this.configuredPasswordHash = configuredPasswordHash;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (!configuredUsername.equalsIgnoreCase(username)) {
            throw new UsernameNotFoundException("Invalid username or password");
        }
        return new User(
                configuredUsername,
                configuredPasswordHash,
                List.of() // no roles/authorities needed - single user, full access
        );
    }
}
