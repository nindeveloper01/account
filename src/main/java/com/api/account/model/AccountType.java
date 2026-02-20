package com.api.account.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Table(name = "account_types")
@Entity
public class AccountType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String alias; //sco
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Boolean isDeleted;
    //account type have many account and by direction both table
    @OneToMany(mappedBy = "accountType")
    private List<Account> accounts;


}