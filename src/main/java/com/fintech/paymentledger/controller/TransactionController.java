package com.fintech.paymentledger.controller;

import com.fintech.paymentledger.entity.Transaction;
import com.fintech.paymentledger.service.TransactionService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public Transaction createTransaction(@RequestBody Transaction transaction) {
        return transactionService.saveTransaction(transaction);
    }

    @GetMapping("/test")
    public String test() {
        return "Transaction API Working!";
    }
}