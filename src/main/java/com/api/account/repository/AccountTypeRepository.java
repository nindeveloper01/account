package com.api.account.repository;


import com.api.account.model.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountTypeRepository extends JpaRepository<AccountType, Integer> {
    Boolean existsByAlias(String alias);
    // SELECT * FROM account_types WHERE alias =?
    Optional<AccountType> findByAlias(String alias);
}