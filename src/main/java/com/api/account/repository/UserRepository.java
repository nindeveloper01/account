package com.api.account.repository;


import com.api.account.model.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    // SELECT * FROM users WHERE phoneNumber =?
    Optional<User> findByPhoneNumberAndIsDeletedFalse(String phoneNumber);

    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByEmail(String email);
    boolean existsByNationalCardId(String username);

    Optional<User> findByEmail(String email);
    // SELECT * FROM users WHERE uuid =?
    Optional<User> findByUuid(String uuid);
}