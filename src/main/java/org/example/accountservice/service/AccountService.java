package org.example.accountservice.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.accountservice.dto.AccountResponse;
import org.example.accountservice.dto.TransactionRequest;
import org.example.accountservice.exception.BadRequestException;
import org.example.accountservice.exception.NotFoundException;
import org.example.accountservice.model.Account;
import org.example.accountservice.model.Currency;
import org.example.accountservice.payload.AccountPayload;
import org.example.accountservice.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import java.time.LocalDate;

// Account service creates a new registry account, account transactions, and account identification number
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final RestTemplate restTemplate;

    @Value("${transaction.url}")
    private String transactionUrl;

    // Register a new account
    public AccountResponse register(AccountPayload payload) {

        // Account identification number generate
        Long byAccountNumber = generateUUID();

        // Create a new account
        Account account = new Account();
        account.setAccountNumber(byAccountNumber);
        account.setCardNumber(payload.getCardNumber());
        account.setEmail(payload.getEmail());
        account.setBalance(0);
        account.setCurrency(Currency.valueOf(payload.getCurrency()));
        account.setTransferLimit(2000);
        account.setTransferLimitTime(LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()));
        accountRepository.save(account);
        AccountResponse accountResponse = new AccountResponse();
        accountResponse.setAccountNumber(account.getAccountNumber());
        return accountResponse;
    }

    // Deposit money and send log to Transaction service
    @Transactional
    public void deposit(AccountPayload payload) {
        Account account = accountRepository.findByCardNumber(payload.getCardNumber())
                .orElse(null);
        if (account != null) {
            if (payload.getAmount() > 0) {
                account.setBalance(account.getBalance() + payload.getAmount());
                accountRepository.save(account);
            } else {
                throw new BadRequestException("Your deposit greater than zero. Amount: " + payload.getAmount());
            }

            // Prepare transaction request
            TransactionRequest request = new TransactionRequest();
            request.setAccountId(account.getId());
            request.setCardNumber(account.getCardNumber());
            request.setEmail(account.getEmail());
            request.setType("deposit");
            request.setAmount(payload.getAmount());
            request.setAtmId(payload.getAtmId());
            request.setToken(payload.getToken());

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(payload.getToken());
            HttpEntity<TransactionRequest> entity = new HttpEntity<>(request,headers);

            // Transaction service call send log
            restTemplate.postForEntity(transactionUrl + "/log", entity, TransactionRequest.class);
        }

    }

    // Withdraw money and send log to Transaction service
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
                throw new BadRequestException("Transfer limit: " + account.getTransferLimit());
            }

            // Prepare transaction request
            TransactionRequest request = new TransactionRequest();
            request.setAccountId(account.getId());
            request.setCardNumber(account.getCardNumber());
            request.setEmail(account.getEmail());
            request.setType("withdraw");
            request.setAmount(payload.getAmount());
            request.setAtmId(payload.getAtmId());
            request.setToken(payload.getToken());

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(payload.getToken());
            HttpEntity<TransactionRequest> entity = new HttpEntity<>(request,headers);

            // Transaction service call send log
            restTemplate.postForEntity(transactionUrl + "/log", entity, TransactionRequest.class);
        }
    }

    // Get current account balance
    public double getBalance(AccountPayload payload) {
        Account account = accountRepository.findByCardNumber(payload.getCardNumber()).orElseThrow(() -> new NotFoundException("Not found Card Number"));
        return account.getBalance();
    }

    // Generate unique account number
    private Long generateUUID() {
        return System.currentTimeMillis() / 100;
    }
}
