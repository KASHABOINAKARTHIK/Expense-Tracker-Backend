package com.trackmyexpense.controller;

import com.trackmyexpense.dto.UserProfileResponse;
import com.trackmyexpense.security.CurrentUserProvider;
import com.trackmyexpense.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final CurrentUserProvider currentUserProvider;

    // GET /api/users/me - protected, requires valid JWT (see SecurityConfig)
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMyProfile() {
        Long userId = currentUserProvider.getCurrentUserId();
        return ResponseEntity.ok(userService.getProfile(userId));
    }
}
