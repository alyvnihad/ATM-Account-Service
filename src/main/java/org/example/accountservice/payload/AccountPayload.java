package org.example.accountservice.payload;

import lombok.Data;

@Data
public class AccountPayload {
    private Long accountId;
    private Long accountNumber;
    private String email;
    private Long cardNumber;
    private Double amount;
    private Long atmId;
    private double balance;
    private String currency;
    private double transferLimit;
}
