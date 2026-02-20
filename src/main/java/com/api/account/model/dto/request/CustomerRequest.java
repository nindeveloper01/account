package com.api.account.model.dto.request;

import java.time.LocalDate;

public record CustomerRequest(
        String name,

        String email,

        String mobileNumber,

        LocalDate createDate
) {
}