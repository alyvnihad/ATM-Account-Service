package org.example.accountservice.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.accountservice.dto.TransactionRequest;
import org.example.accountservice.model.Account;
import org.example.accountservice.model.Currency;
import org.example.accountservice.payload.AccountPayload;
import org.example.accountservice.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final RestTemplate restTemplate;

    @Value("${transaction.url}")
    private String transactionUrl;

    public void register(AccountPayload payload) {
        Long byAccountNumber = generateUUID();
        Account account = new Account();
        account.setAccountNumber(byAccountNumber);
        account.setCardNumber(payload.getCardNumber());
        account.setBalance(0);
        account.setCurrency(Currency.valueOf(payload.getCurrency()));
        account.setTransferLimit(2000);
        account.setTransferLimitTime(LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()));
        accountRepository.save(account);
    }

    @Transactional
    public void deposit(AccountPayload payload) {
        Account account = accountRepository.findByCardNumber(payload.getCardNumber())
                .orElse(null);
        if (account != null) {
            if (payload.getAmount() > 0) {
                account.setBalance(account.getBalance() + payload.getAmount());
                accountRepository.save(account);
            }else{
                throw new RuntimeException("Your deposit greater than zero. Amount: " + payload.getAmount());
            }
        }
    }

    @Transactional
    public void withdraw(AccountPayload payload) {
        Account account = accountRepository.findByCardNumber(payload.getCardNumber())
                .orElse(null);
        if (account != null) {
            if (account.getTransferLimitTime().isBefore(LocalDate.now())) {
                account.setTransferLimitTime(LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()));
                account.setTransferLimit(2000);
            }

            if ((payload.getAmount() > 0 && account.getBalance() >= payload.getAmount()) && (account.getTransferLimit() >= payload.getAmount())) {
                account.setBalance(account.getBalance() - payload.getAmount());
                account.setTransferLimit(account.getTransferLimit() - payload.getAmount());
                accountRepository.save(account);
            } else {
                throw new RuntimeException("Transfer limit: " + account.getTransferLimit());
            }

            TransactionRequest request = new TransactionRequest();
            request.setAccountId(account.getId());
            request.setCardNumber(account.getCardNumber());
            request.setType("withdraw");
            request.setAmount(payload.getAmount());
            request.setAtmId(payload.getAtmId());
            restTemplate.postForEntity(transactionUrl + "/log", request, TransactionRequest.class);
        }
    }

    public double getBalance(AccountPayload payload) {
        Account account = accountRepository.findByCardNumber(payload.getCardNumber()).orElseThrow(() -> new RuntimeException("error"));
        return account.getBalance();
    }


    private Long generateUUID() {
        return System.currentTimeMillis() / 100;
    }
}
