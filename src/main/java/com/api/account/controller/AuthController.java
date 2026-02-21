package com.api.account.controller;

import com.api.account.model.dto.request.LoginRequest;
import com.api.account.model.dto.request.RefreshTokenRequest;
import com.api.account.model.dto.request.RegisterRequest;
import com.api.account.model.dto.response.AuthResponse;
import com.api.account.model.dto.response.RegisterResponse;
import com.api.account.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/login")
    AuthResponse login(@Valid @RequestBody LoginRequest loginRequest){
        return authService.login(loginRequest);
    }

    @PostMapping("/refresh-token")
    AuthResponse refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest){
        return authService.refreshToken(refreshTokenRequest);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/register")
    RegisterResponse register(@Valid @RequestBody RegisterRequest registerRequest) {
        return authService.register(registerRequest);
    }
}
