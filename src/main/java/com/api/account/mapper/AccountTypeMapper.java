package com.api.account.mapper;

import com.api.account.model.AccountType;
import com.api.account.model.dto.request.AccountTypeRequest;
import com.api.account.model.dto.request.AccountTypeUpdateRequest;
import com.api.account.model.dto.response.AccountTypeResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AccountTypeMapper {
    // partially map for update all, and do not null data field on table
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void fromAccountTypeUpdateRequest(AccountTypeUpdateRequest accountTypeUpdateRequest,@MappingTarget AccountType accountType);
    AccountType fromAccountTypeUpdateRequest(AccountTypeUpdateRequest accountTypeUpdateRequest);
    AccountTypeResponse toAccountTypeResponse(AccountType accountType);
    AccountType fromAccountTypeRequest(AccountTypeRequest accountTypeRequest);
    List<AccountTypeResponse> toAccountTypeResponseList(List<AccountType> accountTypes);
}
