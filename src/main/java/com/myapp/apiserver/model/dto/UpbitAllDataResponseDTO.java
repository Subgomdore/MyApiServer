package com.myapp.apiserver.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpbitAllDataResponseDTO {

    private String market;
    private Long seq;
    private String korean_name;
    private String english_name;
    private String del_flag;
    private String add_date;
    private String change_date;

    // 가격 정보
    private String candle_date_time_kst;
    private String candle_date_time_utc;
    private String opening_price;
    private String high_price;
    private String low_price;
    private String trade_price;
    private String timestamp;
    private String candle_acc_trade_price;
    private String candle_acc_trade_volume;
    private String prev_closing_price;
    private String change_price;
    private String change_rate;
}
