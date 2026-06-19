package com.fintech.paymentledger.service;

import com.fintech.paymentledger.dto.TransferRequest;
import com.fintech.paymentledger.dto.TransferResult;
import com.fintech.paymentledger.entity.EntryType;
import com.fintech.paymentledger.entity.LedgerEntry;
import com.fintech.paymentledger.entity.User;
import com.fintech.paymentledger.entity.Wallet;
import com.fintech.paymentledger.repository.LedgerEntryRepository;
import com.fintech.paymentledger.repository.UserRepository;
import com.fintech.paymentledger.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class TransactionService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final LedgerEntryRepository ledgerEntryRepository;

    public TransactionService(
            UserRepository userRepository,
            WalletRepository walletRepository,
            LedgerEntryRepository ledgerEntryRepository) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.ledgerEntryRepository = ledgerEntryRepository;
    }

    @Transactional
    public TransferResult transfer(TransferRequest request) {

        User sender = userRepository.findByUsername(request.getFromUsername())
                .orElse(null);

        User receiver = userRepository.findByUsername(request.getToUsername())
                .orElse(null);

        if (sender == null || receiver == null) {
            return new TransferResult(false, "User not found");
        }

        Wallet senderWallet = walletRepository.findByUser(sender)
                .orElse(null);

        Wallet receiverWallet = walletRepository.findByUser(receiver)
                .orElse(null);

        if (senderWallet == null || receiverWallet == null) {
            return new TransferResult(false, "Wallet not found");
        }

        BigDecimal amount = request.getAmount();

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return new TransferResult(false, "Amount must be positive");
        }

        if (senderWallet.getBalance().compareTo(amount) < 0) {
            return new TransferResult(false, "Insufficient balance");
        }

        senderWallet.setBalance(senderWallet.getBalance().subtract(amount));
        receiverWallet.setBalance(receiverWallet.getBalance().add(amount));

        walletRepository.save(senderWallet);
        walletRepository.save(receiverWallet);

        LedgerEntry debitEntry = new LedgerEntry();
        debitEntry.setWallet(senderWallet);
        debitEntry.setEntryType(EntryType.DEBIT);
        debitEntry.setAmount(amount);
        debitEntry.setCreatedAt(LocalDateTime.now());

        LedgerEntry creditEntry = new LedgerEntry();
        creditEntry.setWallet(receiverWallet);
        creditEntry.setEntryType(EntryType.CREDIT);
        creditEntry.setAmount(amount);
        creditEntry.setCreatedAt(LocalDateTime.now());

        ledgerEntryRepository.save(debitEntry);
        ledgerEntryRepository.save(creditEntry);

        return new TransferResult(true, "Transfer successful");
    }
}