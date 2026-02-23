package com.api.account.service.impl;

import com.api.account.mapper.AccountMapper;
import com.api.account.mapper.AccountTypeMapper;
import com.api.account.mapper.UserMapper;
import com.api.account.model.Account;
import com.api.account.model.AccountType;
import com.api.account.model.User;
import com.api.account.model.dto.request.AccountRequest;
import com.api.account.model.dto.response.AccountResponse;
import com.api.account.repository.AccountRepository;
import com.api.account.repository.AccountTypeRepository;
import com.api.account.repository.UserRepository;
import com.api.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final UserRepository userRepository;
    private final AccountTypeRepository accountTypeRepository;
    @Override
    public AccountResponse createNew(AccountRequest accountRequest) {
        // validation acc type
        AccountType accountType= accountTypeRepository.
                findByAlias(accountRequest.accountTypeAlias())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Account type not found"
                ));
        //validation user id
        User user = userRepository.
                findByUuid(accountRequest.userUuid())
                .orElseThrow(()->new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));
        // validation acc no
        if(accountRepository.existsByActNo(accountRequest.actNo())){
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Account no already exists");
        }
        if (accountRequest.balance().compareTo(BigDecimal.valueOf(10))< 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Balance 10$ is required to creat account");
        }
        // Transfer dto to domain
        // when write by yourself
//
        // write by lombok
        Account account = accountMapper.fromAccountRequest(accountRequest);
        account.setAccountType(accountType);
        account.setUser(user);
        account.setAlias(accountRequest.accountTypeAlias());
        //system generate data
        account.setActName(user.getName());
        account.setIsHidden(false);
        account.setTransferLimit(BigDecimal.valueOf(1000));
        //save to database
        accountRepository.save(account);
        return accountMapper.toAccountResponse(account);// lombok and mapstruct write auto
    }

    @Override
    public Page<AccountResponse> findList(int pageNumber, int pageSize) {
        Sort sortById = Sort.by(Sort.Direction.DESC, "id");
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, sortById);
        Page<Account> accounts = accountRepository.findAll(pageRequest);
        return accounts.map(accountMapper::toAccountResponse);// account to account response
    }
}
