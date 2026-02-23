package com.api.account.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@AllArgsConstructor // Generates the constructor for your test
@NoArgsConstructor
public class CustomerResponse {
    private Long id;
    private String name;
    private String email;
    private String mobileNumber;
    private LocalDate createDate;
    private Boolean communicationAlreadySent;
}
