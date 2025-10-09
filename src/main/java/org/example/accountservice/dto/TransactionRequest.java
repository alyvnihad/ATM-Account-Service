package org.example.accountservice.dto;

import lombok.Data;

@Data
public class TransactionRequest {
    private Long accountId;
    private Long cardNumber;
    private String type;
    private Double amount;
    private Long atmId;
}
