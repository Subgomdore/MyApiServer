package com.myapp.apiserver.service;

import com.myapp.apiserver.model.dto.FilterRequestDTO;
import com.myapp.apiserver.model.dto.UpbitAllDataResponseDTO;
import com.myapp.apiserver.model.dto.UpbitCoinDTO;
import com.myapp.apiserver.model.dto.UpbitCoinDayPriceDTO;
import com.myapp.apiserver.model.entity.UpbitCoin;
import com.myapp.apiserver.model.entity.UpbitCoinDayPrice;
import jakarta.transaction.Transactional;
import jdk.jfr.Description;

import java.util.List;

@Transactional
public interface UpbitService {

    @Description("전체코인리스트 가져오기")
    List<UpbitCoinDTO> getAllCoinList();

    @Description("전체코인리스트 및 가격 가져오기")
    List<UpbitAllDataResponseDTO> getAllCoinAndPrice();

    @Description("전체코인리스트 캐싱작업")
    List<String> getAllCoinsWithCache();

    @Description("필터적용된 코인검색")
    List<FilterRequestDTO> findFilterCoinList(String priceRange, String volume);

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

    // UpbitCoinPrice -> UpbitCoinPriceDTO 변환
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

    default UpbitAllDataResponseDTO entityToDTO(UpbitCoin coin, UpbitCoinDayPrice price) {
        return UpbitAllDataResponseDTO.builder()
                // 코인 정보 매핑
                .market(coin.getMarket())
                .seq(coin.getSeq())
                .korean_name(coin.getKorean_name())
                .english_name(coin.getEnglish_name())
                .del_flag(coin.getDel_flag())
                .add_date(coin.getAdd_date())
                .change_date(coin.getChange_date())

                // 가격 정보 매핑 (price 객체가 null일 수 있으므로 방어 코드 추가)
                .candle_date_time_kst(price != null ? price.getCandle_date_time_kst() : null)
                .candle_date_time_utc(price != null ? price.getCandle_date_time_utc() : null)
                .opening_price(price != null ? price.getOpening_price() : null)
                .high_price(price != null ? price.getHigh_price() : null)
                .low_price(price != null ? price.getLow_price() : null)
                .trade_price(price != null ? price.getTrade_price() : null)
                .timestamp(price != null ? price.getTimestamp() : null)
                .candle_acc_trade_price(price != null ? price.getCandle_acc_trade_price() : null)
                .candle_acc_trade_volume(price != null ? price.getCandle_acc_trade_volume() : null)
                .prev_closing_price(price != null ? price.getPrev_closing_price() : null)
                .change_price(price != null ? price.getChange_price() : null)
                .change_rate(price != null ? price.getChange_rate() : null)
                .build();
    }

}
