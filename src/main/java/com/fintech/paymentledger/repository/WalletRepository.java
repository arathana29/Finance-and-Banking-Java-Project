package com.fintech.paymentledger.repository;

import com.fintech.paymentledger.entity.User;
import com.fintech.paymentledger.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface WalletRepository extends JpaRepository<Wallet, UUID> {

    Optional<Wallet> findByUser(User user);

}
