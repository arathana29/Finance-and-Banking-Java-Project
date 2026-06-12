package com.fintech.paymentledger.repository;

import com.fintech.paymentledger.entity.LedgerEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, UUID> {
}