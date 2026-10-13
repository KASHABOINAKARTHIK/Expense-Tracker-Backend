package com.trackmyexpense.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Small helper so services can grab "who is making this request" without
 * repeating SecurityContextHolder boilerplate everywhere.
 */
@Component
public class CurrentUserProvider {

    public Long getCurrentUserId() {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        return principal.getId();
    }

    public String getCurrentUserEmail() {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        return principal.getUsername();
    }
}
