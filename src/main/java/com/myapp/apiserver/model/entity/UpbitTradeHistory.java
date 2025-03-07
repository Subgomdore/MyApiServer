package com.myapp.apiserver.model.entity;

import com.myapp.apiserver.model.OrderType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "trade_history")
public class UpbitTradeHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private UpbitTradingAccount account;

    private String cryptoSymbol; // BTC, ETH, XRP 등

    @Enumerated(EnumType.STRING)
    private OrderType orderType; // BUY or SELL

    private double price; // 체결 가격
    private double amount; // 체결 코인 수량
    private double total; // 거래 금액 (price * amount)

    private boolean isReal; // 실제 계좌 여부

    private LocalDateTime createdAt = LocalDateTime.now();
}
