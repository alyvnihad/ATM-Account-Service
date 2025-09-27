package org.example.accountservice.dto;

import lombok.Data;
import org.example.accountservice.model.Currency;

@Data
public class AccountRequest {
    private Long cardNumber;
    private double amount;
    private Currency currency;
}
