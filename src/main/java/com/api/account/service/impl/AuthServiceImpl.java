package com.api.account.service.impl;

import com.api.account.mapper.UserMapper;
import com.api.account.model.dto.request.LoginRequest;
import com.api.account.model.dto.request.RefreshTokenRequest;
import com.api.account.model.dto.response.AuthResponse;
import com.api.account.repository.RoleRepository;
import com.api.account.repository.UserRepository;
import com.api.account.repository.UserVerificationRepository;
import com.api.account.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserVerificationRepository userVerificationRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    private final DaoAuthenticationProvider daoAuthenticationProvider;
    private final JwtAuthenticationProvider jwtAuthenticationProvider;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    // send mail
    private final UserVerificationRepository verificationRepository;

    private final JwtEncoder accessTokenJwtEncoder;
    private final JwtEncoder refreshTokenJwtEncoder;
    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        // auth of spring security
        Authentication auth = new UsernamePasswordAuthenticationToken(loginRequest.phoneNumber(),loginRequest.password());
        auth = daoAuthenticationProvider.authenticate(auth);
        // log auth
        log.info("Auth: {}", auth.getPrincipal());

        // log role user
        auth.getAuthorities()
                .forEach(grantedAuthority ->
                        log.info("Authorities: {}", grantedAuthority.getAuthority()));

        // ROLE_USER ROLE_ADMIN
        String scope = auth
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)// map and get authority
                .collect(Collectors.joining(" ")); // map spring join service
        log.info("Scope: {}", scope);
        // Generate JWT Token by JwtEncoder
        // 1. Define JwtClaimsSet (payload)
        Instant now = Instant.now();
        JwtClaimsSet jwtAccessClaimsSet = JwtClaimsSet.builder()
                .id(auth.getName())
                .subject("Access APIs")
                .issuer(auth.getName())
                .issuedAt(now)
                .expiresAt(now.plus(30, ChronoUnit.MINUTES))
                .audience(List.of("Next js","Android","ios"))
                //custom
                .claim("isAdmin",true)
                .claim("studentId","ISTAD0022")
                .claim("scope",scope)
                .build();
        // refresh token
        JwtClaimsSet jwtRefreshClaimsSet = JwtClaimsSet.builder()
                .id(auth.getName())
                .subject("Refresh Token")
                .issuer(auth.getName())
                .issuedAt(now)
                .expiresAt(now.plus(7, ChronoUnit.DAYS))
                .audience(List.of("Next js","Android","ios"))
                //custom
                .claim("scope",scope)
                .build();
        // 2. Generate Token
        // generate access
        String accessToken = accessTokenJwtEncoder
                .encode(JwtEncoderParameters.from(jwtAccessClaimsSet))
                .getTokenValue();

        // generate refresh
        String refreshToken = refreshTokenJwtEncoder
                .encode(JwtEncoderParameters.from(jwtRefreshClaimsSet))
                .getTokenValue();
        log.info("Access Token : {}",accessToken);
        log.info("Refresh Token : {}",refreshToken);
        return AuthResponse.builder()
                .tokenType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.refreshToken();
        Authentication auth = new BearerTokenAuthenticationToken(refreshToken);
        auth= jwtAuthenticationProvider.authenticate(auth);
        log.info("Auth: {}", auth.getPrincipal());
        // ROLE_USER ROLE_ADMIN
        String scope = auth
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)// map and get authority
                .collect(Collectors.joining(" ")); // map spring join service
        log.info("Scope: {}", scope);
        // Generate JWT Token by JwtEncoder
        // 1. Define JwtClaimsSet (payload)
        Jwt jwt = (Jwt) auth.getCredentials();
        Instant now = Instant.now();
        JwtClaimsSet jwtAccessClaimsSet = JwtClaimsSet.builder()
                .id(jwt.getId())
                .subject("Access APIs")
                .issuer(jwt.getId())
                .issuedAt(now)
                .expiresAt(now.plus(10, ChronoUnit.SECONDS))
                .audience(jwt.getAudience())
                //custom
                .claim("isAdmin",true)
                .claim("studentId","ISTAD0022")
                .claim("scope",jwt.getClaimAsString("scope"))
                .build();

//        // 2. Generate Token
//
        String accessToken = accessTokenJwtEncoder
                .encode(JwtEncoderParameters.from(jwtAccessClaimsSet))
                .getTokenValue();
        Instant expiresAt = jwt.getExpiresAt();
        long remainingDays = Duration.between(now, expiresAt).toDays();
        log.info("remainingDays: {}", remainingDays);
        if(remainingDays<=1){
            // refresh token
            JwtClaimsSet jwtRefreshClaimsSet = JwtClaimsSet.builder()
                    .id(auth.getName())
                    .subject("Refresh Token")
                    .issuer(auth.getName())
                    .issuedAt(now)
                    .expiresAt(now.plus(1, ChronoUnit.DAYS))
                    .audience(List.of("Next js","Android","ios"))
                    //custom
                    .claim("scope",jwt.getClaimAsString("scope"))
                    .build();
            refreshToken = refreshTokenJwtEncoder
                    .encode(JwtEncoderParameters.from(jwtRefreshClaimsSet))
                    .getTokenValue();
        }

        return AuthResponse.builder()
                .tokenType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
