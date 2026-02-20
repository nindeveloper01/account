package com.api.account.service;

import com.api.account.model.dto.request.LoginRequest;
import com.api.account.model.dto.request.RefreshTokenRequest;
import com.api.account.model.dto.response.AuthResponse;
import jakarta.validation.Valid;

public interface AuthService {
    AuthResponse login(LoginRequest loginRequest);

    AuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest);
}
