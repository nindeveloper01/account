package com.api.account.model.dto.request;
public record AccountTypeUpdateRequest(
        String description,
        Boolean isDeleted
) {
}