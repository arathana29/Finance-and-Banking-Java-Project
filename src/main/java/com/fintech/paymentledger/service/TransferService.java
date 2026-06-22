package com.fintech.paymentledger.service;

import com.fintech.paymentledger.entity.User;
import com.fintech.paymentledger.entity.Wallet;
import com.fintech.paymentledger.entity.LedgerEntry;
import com.fintech.paymentledger.entity.EntryType;
import com.fintech.paymentledger.repository.UserRepository;

import com.fintech.paymentledger.dto.TransferRequest;
import com.fintech.paymentledger.dto.TransferResult;
import com.fintech.paymentledger.repository.LedgerEntryRepository;
import com.fintech.paymentledger.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;


@Service
public class TransferService {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final LedgerEntryRepository ledgerEntryRepository;

    public TransferService(
            WalletRepository walletRepository,
            UserRepository userRepository,
            LedgerEntryRepository ledgerEntryRepository
    ) {
        this.walletRepository = walletRepository;
        this.userRepository = userRepository;
        this.ledgerEntryRepository = ledgerEntryRepository;
    }

    @Transactional
    public TransferResult transfer(TransferRequest request) {

        String fromUsername = request.getFromUsername();
        String toUsername = request.getToUsername();

        User fromUser = userRepository.findByUsername(fromUsername)
                .orElseThrow(() -> new RuntimeException("Source user not found"));

        User toUser = userRepository.findByUsername(toUsername)
                .orElseThrow(() -> new RuntimeException("Destination user not found"));

        Wallet sourceWallet = walletRepository.findByUser(fromUser)
                .orElseThrow(() -> new RuntimeException("Source wallet not found"));

        Wallet destinationWallet = walletRepository.findByUser(toUser)
                .orElseThrow(() -> new RuntimeException("Destination wallet not found"));

        BigDecimal amount = request.getAmount();

        if (sourceWallet.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient funds");
        }

        sourceWallet.setBalance(
                sourceWallet.getBalance().subtract(amount)
        );

        destinationWallet.setBalance(
                destinationWallet.getBalance().add(amount)
        );

        walletRepository.save(sourceWallet);
        walletRepository.save(destinationWallet);

        LedgerEntry debitEntry = new LedgerEntry();
        debitEntry.setWallet(sourceWallet);
        debitEntry.setEntryType(EntryType.DEBIT);
        debitEntry.setAmount(amount);

        LedgerEntry creditEntry = new LedgerEntry();
        creditEntry.setWallet(destinationWallet);
        creditEntry.setEntryType(EntryType.CREDIT);
        creditEntry.setAmount(amount);

        ledgerEntryRepository.save(debitEntry);
        ledgerEntryRepository.save(creditEntry);

        TransferResult result = new TransferResult();

        result.setSuccess(true);
        result.setMessage("Transfer Successful");

        return result;
    }
}