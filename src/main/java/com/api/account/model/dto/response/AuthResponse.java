package com.api.account.model.dto.response;


import lombok.Builder;

@Builder
public record AuthResponse(
        // Token Type
        String tokenType,
        String accessToken,
        String refreshToken
) {
}