package com.trackmyexpense.security;

import com.trackmyexpense.entity.User;
import com.trackmyexpense.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Multi-user replacement for the old SingleUserDetailsService. Looks users up
 * by email (the login identifier) via UserRepository.
 *
 * Note: this loads the user even if not yet email-verified (enabled=false) -
 * UserPrincipal.isEnabled() reflects that, and Spring Security's
 * DaoAuthenticationProvider automatically rejects disabled accounts during
 * authenticate() with a DisabledException, which GlobalExceptionHandler maps to a
 * clear "please verify your email" style response.
 */
@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid email or password"));
        return new UserPrincipal(user);
    }
}
