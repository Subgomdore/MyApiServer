package com.myapp.apiserver.service;

import com.myapp.apiserver.model.dto.UpbitCoinDTO;
import com.myapp.apiserver.model.dto.UpbitCoinDayPriceDTO;
import com.myapp.apiserver.model.entity.UpbitCoin;
import com.myapp.apiserver.model.entity.UpbitCoinDayPrice;
import jakarta.transaction.Transactional;

@Transactional
public interface ExternalUpbitService {

    void doGetUpbitCoinList();

    void doGetUpbitCoinPrice(String count);

    default UpbitCoinDTO entityToDTO(UpbitCoin entity) {
        return UpbitCoinDTO.builder()
                .market(entity.getMarket())  // UpbitId의 market
                .seq(entity.getSeq())
                .korean_name(entity.getKorean_name())
                .english_name(entity.getEnglish_name())
                .del_flag(entity.getDel_flag())
                .add_date(entity.getAdd_date())
                .change_date(entity.getChange_date())
                .build();
    }

    default UpbitCoin DTOtoEntity(UpbitCoinDTO dto) {
        return UpbitCoin.builder()
                .market(dto.getMarket())  // UpbitId 설정
                .seq(dto.getSeq())
                .korean_name(dto.getKorean_name())
                .english_name(dto.getEnglish_name())
                .del_flag(dto.getDel_flag())
                .add_date(dto.getAdd_date())
                .change_date(dto.getChange_date())
                .build();
    }

    default UpbitCoinDayPriceDTO entityToDTO(UpbitCoinDayPrice entity) {
        return UpbitCoinDayPriceDTO.builder()
                .market(entity.getMarket())  // 코인 마켓 (예: KRW-BTC)
                .candle_date_time_kst(entity.getCandle_date_time_kst())  // KST 시간의 캔들 데이터
                .candle_date_time_utc(entity.getCandle_date_time_utc())  // UTC 시간의 캔들 데이터
                .opening_price(entity.getOpening_price())  // 시가
                .high_price(entity.getHigh_price())  // 고가
                .low_price(entity.getLow_price())  // 저가
                .trade_price(entity.getTrade_price())  // 종가
                .timestamp(entity.getTimestamp())  // 타임스탬프
                .candle_acc_trade_price(entity.getCandle_acc_trade_price())  // 누적 거래 금액
                .candle_acc_trade_volume(entity.getCandle_acc_trade_volume())  // 누적 거래량
                .prev_closing_price(entity.getPrev_closing_price())  // 전일 종가
                .change_price(entity.getChange_price())  // 가격 변동량
                .change_rate(entity.getChange_rate())  // 변동률
                .build();
    }

    // UpbitCoinPriceDTO -> UpbitCoinPrice Entity 변환
    default UpbitCoinDayPrice DTOtoEntity(UpbitCoinDayPriceDTO dto) {
        return UpbitCoinDayPrice.builder()
                .market(dto.getMarket())  // 코인 마켓 (예: KRW-BTC)
                .candle_date_time_kst(dto.getCandle_date_time_kst())  // KST 시간의 캔들 데이터
                .candle_date_time_utc(dto.getCandle_date_time_utc())  // UTC 시간의 캔들 데이터
                .opening_price(dto.getOpening_price())  // 시가
                .high_price(dto.getHigh_price())  // 고가
                .low_price(dto.getLow_price())  // 저가
                .trade_price(dto.getTrade_price())  // 종가
                .timestamp(dto.getTimestamp())  // 타임스탬프
                .candle_acc_trade_price(dto.getCandle_acc_trade_price())  // 누적 거래 금액
                .candle_acc_trade_volume(dto.getCandle_acc_trade_volume())  // 누적 거래량
                .prev_closing_price(dto.getPrev_closing_price())  // 전일 종가
                .change_price(dto.getChange_price())  // 가격 변동량
                .change_rate(dto.getChange_rate())  // 변동률
                .build();
    }
}
