package org.example.accountservice.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.accountservice.model.Account;
import org.example.accountservice.model.Currency;
import org.example.accountservice.repository.AccountRepository;
import org.springframework.stereotype.Service;


import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public void register(Long cardNumber, Currency currency) {
        Long byAccountNumber = generateUUID();
        Account account = new Account();
        account.setAccountNumber(byAccountNumber);
        account.setCardNumber(cardNumber);
        account.setBalance(0);
        account.setCurrency(currency);
        account.setTransferLimit(2000);
        account.setTransferLimitTime(LocalDate.now());
        accountRepository.save(account);
    }

    @Transactional
    public void deposit(Long cardNumber, double amount) {
        Account account = accountRepository.findByCardNumber(cardNumber)
                .orElse(null);
        if (account != null) {
            if (amount > 0) {
                account.setBalance(account.getBalance() + amount);
                accountRepository.save(account);
            }
        }
    }

    private Long generateUUID() {
        return System.currentTimeMillis() / 100;
    }

}
