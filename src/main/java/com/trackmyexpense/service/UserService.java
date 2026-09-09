package com.trackmyexpense.service;

import com.trackmyexpense.dto.*;

public interface UserService {

    MessageResponse register(RegisterRequest request);

    MessageResponse verifyOtp(VerifyOtpRequest request);

    MessageResponse resendOtp(ResendOtpRequest request);

    MessageResponse forgotPassword(ForgotPasswordRequest request);

    MessageResponse resetPassword(ResetPasswordRequest request);

    MessageResponse changePassword(Long userId, ChangePasswordRequest request);
    
    UserProfileResponse getProfile(Long userId);
}
