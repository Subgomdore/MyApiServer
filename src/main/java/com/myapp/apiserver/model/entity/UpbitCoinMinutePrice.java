package com.myapp.apiserver.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "upbit_minute_price")
@IdClass(UpbitCoinMinutePriceId.class)
public class UpbitCoinMinutePrice {
    @Id
    @Comment("코인 마켓 (예: KRW-BTC)")
    private String market;  // 코인 마켓 (예: KRW-BTC)

    @Id
    @Comment("KST 시간의 캔들 데이터")
    private String candle_date_time_kst;  // KST 시간의 캔들 데이터

    @Comment("UTC 시간의 캔들 데이터")
    private String candle_date_time_utc;  // UTC 시간의 캔들 데이터

    @Comment("시가")
    private String opening_price;  // 시가

    @Comment("고가")
    private String high_price;  // 고가

    @Comment("저가")
    private String low_price;  // 저가

    @Comment("종가")
    private String trade_price;  // 종가

    @Comment("타임스탬프 (밀리초)")
    private String timestamp;  // 타임스탬프 (밀리초)

    @Comment("누적 거래 금액")
    private String candle_acc_trade_price;  // 누적 거래 금액

    @Comment("누적 거래량")
    private String candle_acc_trade_volume;  // 누적 거래량

    @Comment("분 단위(유닛)")
    private String unit;  // 전일 종가

}


