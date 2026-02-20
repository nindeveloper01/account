package com.api.account.repository;

import com.api.account.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    // JPQL = Jakarta Persistent Query Language; V value specific
    @Query("""
        SELECT r
            FROM Role r
                WHERE r.name = 'USER'
    """)
    Role findRoleUser();
    @Query(""" 
        SELECT r 
            FROM Role As r WHERE r.name= 'CUSTOMER'
    """)
    Role findRoleCustomer();
}