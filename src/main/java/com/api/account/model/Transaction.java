package com.api.account.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Table(name = "transactions")
@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private Boolean status;

    @Column(nullable = false)
    private Boolean isDeleted;

    @Column(nullable = false,length = 30)
    private String transactionType;

    private String paymentReceiver;// number, whiter, school

    @Column(nullable = false)
    private LocalDate transactionAt;

    @ManyToOne
    private Account owner;
    @ManyToOne
    private Account receiver;

}