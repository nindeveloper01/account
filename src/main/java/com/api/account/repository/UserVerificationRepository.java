package com.api.account.repository;

import com.api.account.model.User;
import com.api.account.model.UserVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserVerificationRepository extends JpaRepository<UserVerification, Long> {
    // relationship
    Optional<UserVerification> findByUserAndVerificationCode(User user, String verificationCode);
    Optional<UserVerification> findByUser(User user);

}