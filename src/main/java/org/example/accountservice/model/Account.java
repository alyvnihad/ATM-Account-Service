package org.example.accountservice.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long accountNumber;
    private Long cardNumber;
    private double balance;

    @Enumerated(EnumType.STRING)
    private Currency currency;
    private double transferLimit;
    private LocalDate transferLimitTime;
}
