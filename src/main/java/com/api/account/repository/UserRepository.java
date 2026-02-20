package com.api.account.repository;


import com.api.account.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    // SELECT * FROM users WHERE phoneNumber =?
    Optional<User> findByPhoneNumberAndIsDeletedFalse(String phoneNumber);
}