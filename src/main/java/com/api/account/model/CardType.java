package com.api.account.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Table(name = "card_types")
@Entity
public class CardType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(length = 15, nullable = false ,unique = true)
    private String name;
    private Boolean isDeleted;
    @OneToMany(mappedBy = "cardType")
    private List<Card> cards;
}