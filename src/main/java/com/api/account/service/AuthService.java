package com.api.account.service;

import com.api.account.model.dto.request.LoginRequest;
import com.api.account.model.dto.request.RefreshTokenRequest;
import com.api.account.model.dto.request.RegisterRequest;
import com.api.account.model.dto.request.VerificationRequest;
import com.api.account.model.dto.response.AuthResponse;
import com.api.account.model.dto.response.RegisterResponse;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public interface AuthService {
    AuthResponse login(LoginRequest loginRequest);

    AuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest);

    RegisterResponse register(RegisterRequest registerRequest);

    void sendVerification(String email) throws MessagingException;

    void verify(VerificationRequest verificationRequest) throws MessagingException;

    void resendVerification(String email) throws MessagingException;
}
