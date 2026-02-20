package com.api.account.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Table(name = "accounts")
@Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String alias;
    private String actName;
    private String actNo;
    private BigDecimal balance;
    private BigDecimal transferLimit;
    private Boolean isHidden;
    // accounts have only account type one
    @ManyToOne
    private AccountType accountType;
    @OneToOne
    private Card card;
    @ManyToOne
    @JoinTable(
            name = "user_accounts",
            joinColumns = @JoinColumn(name = "account_id",referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private User user;
    @OneToMany(mappedBy = "owner")
    private List<Transaction> transactionsOfOwner;
}
