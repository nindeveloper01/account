package com.api.account.exception;

import lombok.Builder;
@Builder
public record FieldError(
        String field,
        String detail
) {
}