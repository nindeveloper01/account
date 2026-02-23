package com.api.account.controller;

import com.api.account.model.dto.request.AccountRequest;
import com.api.account.model.dto.response.AccountResponse;
import com.api.account.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    AccountResponse createNew(@Valid @RequestBody AccountRequest accountRequest) {
        return accountService.createNew(accountRequest);
    }
    @GetMapping
    Page<AccountResponse> findList(@RequestParam(required = false,defaultValue = "0") int pageNumber,
                                   @RequestParam(required = false ,defaultValue = "10") int pageSize) {
        return accountService.findList(pageNumber, pageSize);
    }
}
