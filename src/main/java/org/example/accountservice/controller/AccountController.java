package org.example.accountservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.accountservice.dto.AccountRequest;
import org.example.accountservice.service.AccountService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/register")
    public void register(@RequestBody AccountRequest request) {
        accountService.register(request.getCardNumber(),request.getCurrency());
    }

    @PostMapping("/deposit")
    public void deposit(@RequestBody AccountRequest request){
        accountService.deposit(request.getCardNumber(), request.getAmount());
    }
}
