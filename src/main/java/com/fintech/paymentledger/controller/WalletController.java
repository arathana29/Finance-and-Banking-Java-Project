package com.fintech.paymentledger.controller;

import com.fintech.paymentledger.entity.User;
import com.fintech.paymentledger.entity.Wallet;
import com.fintech.paymentledger.repository.UserRepository;
import com.fintech.paymentledger.repository.WalletRepository;

import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/wallet")
public class WalletController {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;

    public WalletController(
            UserRepository userRepository,
            WalletRepository walletRepository
    ) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
    }

    @PostMapping("/create/{username}")
    public Wallet createWallet(
            @PathVariable String username
    ) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Wallet wallet = new Wallet();

        wallet.setUser(user);
        wallet.setBalance(BigDecimal.valueOf(1000));

        return walletRepository.save(wallet);
    }
    @GetMapping("/{username}")
    public Wallet getWallet(
            @PathVariable String username
    ) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        return walletRepository.findByUser(user)
                .orElseThrow(() ->
                        new RuntimeException("Wallet not found"));
    }
}