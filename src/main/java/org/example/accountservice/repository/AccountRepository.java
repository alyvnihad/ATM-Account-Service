package org.example.accountservice.repository;

import org.example.accountservice.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface AccountRepository extends JpaRepository<Account,Long> {
    Optional<Account> findByCardNumber(Long cardNumber);
}
