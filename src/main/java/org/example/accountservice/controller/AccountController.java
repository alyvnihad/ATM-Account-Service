package org.example.accountservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.accountservice.payload.AccountPayload;
import org.example.accountservice.service.AccountService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/register")
    public void register(@RequestBody AccountPayload payload) {
        accountService.register(payload);
    }

    @PostMapping("/deposit")
    public void deposit(@RequestBody AccountPayload payload){
        accountService.deposit(payload);
    }

    @PostMapping("/withdraw")
    public void withdraw(@RequestBody AccountPayload payload){
        accountService.withdraw(payload);
    }

    @PostMapping("/balance")
    public Double balanceRead(@RequestBody AccountPayload payload){
        return accountService.getBalance(payload);
    }
}
