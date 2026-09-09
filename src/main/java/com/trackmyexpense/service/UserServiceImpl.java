package com.trackmyexpense.service;

import com.trackmyexpense.dto.*;
import com.trackmyexpense.entity.OtpPurpose;
import com.trackmyexpense.entity.User;
import com.trackmyexpense.exception.EmailAlreadyRegisteredException;
import com.trackmyexpense.exception.InvalidCurrentPasswordException;
import com.trackmyexpense.exception.InvalidOtpException;
import com.trackmyexpense.exception.UserNotFoundException;
import com.trackmyexpense.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Value("${app.otp.expiration-minutes:10}")
    private long otpExpirationMinutes;

    @Override
    public MessageResponse register(RegisterRequest request) {
        User user = userRepository.findByEmailIgnoreCase(request.getEmail()).orElse(null);

        if (user != null && user.isEnabled()) {
            throw new EmailAlreadyRegisteredException(request.getEmail());
        }

        if (user == null) {
            user = User.builder()
                    .name(request.getName())
                    .email(request.getEmail())
                    .enabled(false)
                    .build();
        } else {
            // Existing but unverified account trying to register again - refresh their details/OTP.
            user.setName(request.getName());
        }

        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        applyNewOtp(user, OtpPurpose.REGISTRATION);
        userRepository.save(user);

        emailService.sendRegistrationOtp(user.getEmail(), user.getOtpCode());

        return MessageResponse.builder()
                .message("Registration successful. Please check your email for the verification code.")
                .build();
    }

    @Override
    public MessageResponse verifyOtp(VerifyOtpRequest request) {
        User user = userRepository.findByEmailIgnoreCase(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException(request.getEmail()));

        validateOtp(user, request.getOtp(), OtpPurpose.REGISTRATION);

        user.setEnabled(true);
        clearOtp(user);
        userRepository.save(user);

        return MessageResponse.builder()
                .message("Email verified successfully. You can now log in.")
                .build();
    }

    @Override
    public MessageResponse resendOtp(ResendOtpRequest request) {
        User user = userRepository.findByEmailIgnoreCase(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException(request.getEmail()));

        if (user.isEnabled()) {
            return MessageResponse.builder()
                    .message("This account is already verified. Please log in.")
                    .build();
        }

        applyNewOtp(user, OtpPurpose.REGISTRATION);
        userRepository.save(user);

        emailService.sendRegistrationOtp(user.getEmail(), user.getOtpCode());

        return MessageResponse.builder()
                .message("A new verification code has been sent to your email.")
                .build();
    }

    @Override
    public MessageResponse forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmailIgnoreCase(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException(request.getEmail()));

        applyNewOtp(user, OtpPurpose.PASSWORD_RESET);
        userRepository.save(user);

        emailService.sendPasswordResetOtp(user.getEmail(), user.getOtpCode());

        return MessageResponse.builder()
                .message("A password reset code has been sent to your email.")
                .build();
    }

    @Override
    public MessageResponse resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByEmailIgnoreCase(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException(request.getEmail()));

        validateOtp(user, request.getOtp(), OtpPurpose.PASSWORD_RESET);

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        clearOtp(user);
        userRepository.save(user);

        return MessageResponse.builder()
                .message("Password reset successful. You can now log in with your new password.")
                .build();
    }

    @Override
    public MessageResponse changePassword(Long userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new InvalidCurrentPasswordException();
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return MessageResponse.builder()
                .message("Password changed successfully.")
                .build();
    }
    
    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        return UserProfileResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .build();
    }

    // ---------- helpers ----------

    private void applyNewOtp(User user, OtpPurpose purpose) {
        user.setOtpCode(generateOtp());
        user.setOtpExpiresAt(LocalDateTime.now().plusMinutes(otpExpirationMinutes));
        user.setOtpPurpose(purpose);
    }

    private void clearOtp(User user) {
        user.setOtpCode(null);
        user.setOtpExpiresAt(null);
        user.setOtpPurpose(null);
    }

    private void validateOtp(User user, String submittedOtp, OtpPurpose expectedPurpose) {
        if (user.getOtpCode() == null || user.getOtpPurpose() != expectedPurpose) {
            throw new InvalidOtpException("No pending verification code for this action. Please request a new one.");
        }
        if (user.getOtpExpiresAt() == null || user.getOtpExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InvalidOtpException("This code has expired. Please request a new one.");
        }
        if (!user.getOtpCode().equals(submittedOtp)) {
            throw new InvalidOtpException("Invalid verification code.");
        }
    }

    private String generateOtp() {
        int code = RANDOM.nextInt(1_000_000); // 0 - 999999
        return String.format("%06d", code);
    }
}
