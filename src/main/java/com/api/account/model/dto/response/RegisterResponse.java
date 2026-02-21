package com.api.account.model.dto.response;

import lombok.Builder;

@Builder
public record RegisterResponse(
        String message,
        String email
) {
}