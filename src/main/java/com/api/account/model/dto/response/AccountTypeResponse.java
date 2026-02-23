package com.api.account.model.dto.response;
import lombok.Builder;
@Builder
public record AccountTypeResponse(
        String alias,
        String name,
        String description,
        Boolean isDeleted
) {
}