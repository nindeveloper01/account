package com.api.account.model.dto.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record AccountResponse(
        String alias,
        String actName,
        String actNo,
        BigDecimal balance,
        AccountTypeResponse accountType
) {
}