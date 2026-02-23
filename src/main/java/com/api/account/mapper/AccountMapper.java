package com.api.account.mapper;

import com.api.account.model.Account;
import com.api.account.model.dto.request.AccountRequest;
import com.api.account.model.dto.response.AccountResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    // map acc to accResponse
    // source = account
    // target = accountResponse
//    @Mapping(source ="accountType.alias",target = "accountTypeAlias")
    AccountResponse toAccountResponse(Account account);
    Account fromAccountRequest(AccountRequest accountRequest);
}