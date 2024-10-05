package com.myapp.apiserver.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor  // 기본 생성자 자동 생성
@AllArgsConstructor  // 모든 필드를 포함한 생성자 자동 생성
@EqualsAndHashCode  // equals()와 hashCode() 자동 생성
public class UpbitCoinPriceId implements Serializable {

    private String market;
    private String candle_date_time_kst;
}
