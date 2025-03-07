package com.myapp.apiserver.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "trading_account")
public class UpbitTradingAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double balance; // 보유 현금 (KRW)

    @Column(columnDefinition = "JSON")
    private String cryptoBalance; // {"BTC": 0.5, "ETH": 2.0, "XRP": 100}

    private boolean isReal; // 실제 계좌 여부

    private LocalDateTime createdAt = LocalDateTime.now();
}
