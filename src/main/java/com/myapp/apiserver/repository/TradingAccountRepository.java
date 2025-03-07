package com.myapp.apiserver.repository;

import com.myapp.apiserver.model.entity.UpbitTradingAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TradingAccountRepository extends JpaRepository<UpbitTradingAccount, Long> {
    Optional<UpbitTradingAccount> findByIdAndIsReal(Long id, boolean isReal);
}