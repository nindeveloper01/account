package com.api.account.service;

import com.api.account.model.dto.request.AccountRequest;
import com.api.account.model.dto.response.AccountResponse;
import org.springframework.data.domain.Page;

public interface AccountService {
    AccountResponse createNew(  AccountRequest accountRequest);
    Page<AccountResponse> findList(int pageNumber, int pageSize);
}
