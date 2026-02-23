package com.api.account.repository;

import com.api.account.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Integer> {
    Optional<Account> findByActNo(String actNo);
    // SELECT EXISTS(SELECT * FROM account WHERE act_no =?)
    Boolean existsByActNo(String actNo);

}
