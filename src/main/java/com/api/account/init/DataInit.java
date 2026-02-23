package com.api.account.init;


import com.api.account.model.AccountType;
import com.api.account.model.Role;
import com.api.account.model.User;
import com.api.account.repository.AccountTypeRepository;
import com.api.account.repository.RoleRepository;
import com.api.account.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DataInit {
    private final UserRepository userRepository;
    private final AccountTypeRepository accountTypeRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    void init(){
        if(accountTypeRepository.count() == 0) {
            AccountType accountType = new AccountType();
            accountType.setName("Payroll");
            accountType.setAlias("payroll");
            accountType.setIsDeleted(false);
            accountType.setDescription("Payroll account for user");

            AccountType saving = new AccountType();
            saving.setName("Saving");
            saving.setAlias("saving");
            saving.setIsDeleted(false);
            saving.setDescription("Saving account for user");

            accountTypeRepository.save(accountType);
            accountTypeRepository.save(saving);
        }
        if(userRepository.count() == 0) {
            Role user = new Role();
            user.setName("USER");
            Role manager = new Role();
            manager.setName("MANAGER");
            Role admin = new Role();
            admin.setName("ADMIN");
            Role customer = new Role();
            customer.setName("CUSTOMER");
            roleRepository.saveAll(List.of(user, manager, admin, customer));
            User user1 = new User();
            user1.setUuid(UUID.randomUUID().toString());
            user1.setName("John");
            user1.setGender("Male");
            user1.setPassword(passwordEncoder.encode("Kinin@1234"));
            user1.setPin("1234");
            user1.setPhoneNumber("0122343443");
            user1.setEmail("booom@gmail.com");
            user1.setNationalCardId("123456789");
            user1.setStudentCardId("HRD-100");
            user1.setProfileImage("user3/string.png");
            user1.setIsDeleted(false);
            user1.setIsBlocked(false);
            user1.setRoles(List.of(admin));

            User user2 = new User();
            user2.setUuid(UUID.randomUUID().toString());
            user2.setName("kinin");
            user2.setGender("Male");
            user2.setPassword(passwordEncoder.encode("Kinin@1234"));
            user2.setPin("1234");
            user2.setPhoneNumber("0122343440");
            user2.setEmail("kinin@gmail.com");
            user2.setNationalCardId("123456779");
            user2.setStudentCardId("HRD-1000");
            user2.setProfileImage("user3/string.png");
            user2.setIsDeleted(false);
            user2.setIsBlocked(false);
            user2.setRoles(List.of(user));

            User user3 = new User();
            user3.setUuid(UUID.randomUUID().toString());
            user3.setName("KNin");
            user3.setGender("Male");
            user3.setPassword(passwordEncoder.encode("Kinin@1234"));
            user3.setPin("7777");
            user3.setPhoneNumber("0222343440");
            user3.setEmail("book@gmail.com");
            user3.setNationalCardId("123456777");
            user3.setStudentCardId("HRD-2000");
            user3.setProfileImage("user3/string.png");
            user3.setIsDeleted(false);
            user3.setIsBlocked(false);
            user3.setRoles(List.of(customer));
            userRepository.saveAll(List.of(user1, user2, user3));
        }
    }
}