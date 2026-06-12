package com.fintech.paymentledger.repository;

import com.fintech.paymentledger.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository
        extends JpaRepository<Transaction, Long> {
}