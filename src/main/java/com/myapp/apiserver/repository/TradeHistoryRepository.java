package com.myapp.apiserver.repository;

import com.myapp.apiserver.model.entity.UpbitTradeHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TradeHistoryRepository extends JpaRepository<UpbitTradeHistory, Long> {
    List<UpbitTradeHistory> findByAccountIdOrderByCreatedAtDesc(Long accountId);
    List<UpbitTradeHistory> findByCryptoSymbolAndIsRealOrderByCreatedAtDesc(String cryptoSymbol, boolean isReal);
}