package com.myapp.apiserver.service;

import com.myapp.apiserver.UpbitUtill.UpbitAPI;
import com.myapp.apiserver.UpbitUtill.UpbitUtils;
import com.myapp.apiserver.model.dto.UpbitCoinDTO;
import com.myapp.apiserver.model.dto.UpbitCoinDayPriceDTO;
import com.myapp.apiserver.model.dto.UpbitCoinMinutePriceDTO;
import com.myapp.apiserver.model.entity.UpbitCoin;
import com.myapp.apiserver.model.entity.UpbitCoinDayPrice;
import com.myapp.apiserver.model.entity.UpbitCoinMinutePrice;
import com.myapp.apiserver.repository.UpbitCoinDayPriceRepository;
import com.myapp.apiserver.repository.UpbitCoinMinutePriceRepository;
import com.myapp.apiserver.repository.UpbitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class ExternalUpbitServiceimpl implements ExternalUpbitService {

    private final UpbitAPI upbitAPI;
    private final UpbitRepository upbitRepository;
    private final UpbitService upbitService;
    private final UpbitCoinDayPriceRepository upbitCoinDayPriceRepository;
    private final UpbitCoinMinutePriceRepository upbitCoinMinutePriceRepository;
    private final UpbitUtils upbitUtils;

    @Override
    public void doGetUpbitCoinList() {
        try {
            List<UpbitCoinDTO> coinDTOList = new ArrayList<>();
            List<Map<String, Object>> resultList = upbitAPI.getMarketCode();

            // SEQ 적재를 위한 i
            int i = 1;

            // List<Map<String, Object>>  => DTO
            for (Map<String, Object> tmpMap : resultList) {
                UpbitCoinDTO upbitCoinDTO = UpbitCoinDTO.builder()
                        .market((String) tmpMap.get("market"))
                        .seq(Long.valueOf(i))
                        .korean_name((String) tmpMap.get("korean_name"))
                        .english_name((String) tmpMap.get("english_name"))
                        .add_date((String) tmpMap.get("add_date"))
                        .change_date((String) tmpMap.get("change_date"))
                        .build();

                coinDTOList.add(upbitCoinDTO);
                i++;
            }

            List<UpbitCoin> upbitCoins = coinDTOList.stream()
                    .map(dto -> UpbitCoin.builder()
                            .market(dto.getMarket())
                            .seq(dto.getSeq())
                            .korean_name(dto.getKorean_name())
                            .english_name(dto.getEnglish_name())
                            .add_date(dto.getAdd_date())
                            .change_date(dto.getChange_date())
                            .build()).collect(Collectors.toList());

            upbitRepository.deleteAll();
            upbitCoins.forEach(entity -> upbitRepository.save(entity));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doGetUpbitCoinDayPrice(String count) {
        try {
            List<UpbitCoinDTO> coinList = upbitService.getAllCoinList();

            List<String> marketList =
                    coinList.stream().map(UpbitCoinDTO::getMarket).collect(Collectors.toList());

            // 내부에 등록된 코인리스트 List 가공처리 및 순회요청
            int i = 0;
            for (String market : marketList) {

                Map<String, String> paramsMap = new HashMap<>();

                paramsMap.put("market", market);
                paramsMap.put("count", count);
                paramsMap.put("ALL_COUNT", String.valueOf(marketList.size()));
                paramsMap.put("PROGRESS", String.valueOf(i));

                List<Map<String, Object>> resultList = upbitAPI.getCandleByDay(paramsMap);

                List<Map<String, String>> convertList = new ArrayList<>();
                convertList = upbitUtils.convertPirceData(resultList);

                // DTO 리스트 생성
                List<UpbitCoinDayPriceDTO> priceDTOList = new ArrayList<>();

                // 특정코인정보에 대한 가격정보 List를 DTO로 변환
                for (Map<String, String> tmpMap : convertList) {
                    UpbitCoinDayPriceDTO upbitCoinPriceDTO = UpbitCoinDayPriceDTO.builder()
                            .market(tmpMap.get("market"))
                            .candle_date_time_kst(tmpMap.get("candle_date_time_kst"))
                            .candle_date_time_utc(tmpMap.get("candle_date_time_utc"))
                            .opening_price(tmpMap.get("opening_price"))
                            .high_price(tmpMap.get("high_price"))
                            .low_price(tmpMap.get("low_price"))
                            .trade_price(tmpMap.get("trade_price"))
                            .timestamp(tmpMap.get("timestamp"))
                            .candle_acc_trade_price(tmpMap.get("candle_acc_trade_price"))
                            .candle_acc_trade_volume(tmpMap.get("candle_acc_trade_volume"))
                            .prev_closing_price(tmpMap.get("prev_closing_price"))
                            .change_price(tmpMap.get("change_price"))
                            .change_rate(tmpMap.get("change_rate"))
                            .build();

                    // DTO 리스트에 추가
                    priceDTOList.add(upbitCoinPriceDTO);
                }

                // Entity로 변환하고 저장
                List<UpbitCoinDayPrice> upbitCoinDayPrices = priceDTOList.stream()
                        .map(this::DTOtoEntity)
                        .collect(Collectors.toList());

                upbitCoinDayPriceRepository.saveAll(upbitCoinDayPrices);
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doGetUpbitCoinMinutePrice(String count){
        try {
            List<UpbitCoinDTO> coinList = upbitService.getAllCoinList();

            List<String> marketList =
                    coinList.stream().map(UpbitCoinDTO::getMarket).collect(Collectors.toList());

            // 내부에 등록된 코인리스트 List 가공처리 및 순회요청
            int i = 0;
            for (String market : marketList) {

                Map<String, String> paramsMap = new HashMap<>();

                paramsMap.put("market", market);
                paramsMap.put("count", count);
                paramsMap.put("ALL_COUNT", String.valueOf(marketList.size()));
                paramsMap.put("PROGRESS", String.valueOf(i));

                List<Map<String, Object>> resultList = upbitAPI.getCandleByMinute(paramsMap);

                List<Map<String, String>> convertList = new ArrayList<>();
                convertList = upbitUtils.convertPirceData(resultList);

                // DTO 리스트 생성
                List<UpbitCoinMinutePriceDTO> priceDTOList = new ArrayList<>();

                // 특정코인정보에 대한 가격정보 List를 DTO로 변환
                for (Map<String, String> tmpMap : convertList) {
                    UpbitCoinMinutePriceDTO upbitCoinPriceDTO = UpbitCoinMinutePriceDTO.builder()
                            .market(tmpMap.get("market"))
                            .candle_date_time_kst(tmpMap.get("candle_date_time_kst"))
                            .candle_date_time_utc(tmpMap.get("candle_date_time_utc"))
                            .opening_price(tmpMap.get("opening_price"))
                            .high_price(tmpMap.get("high_price"))
                            .low_price(tmpMap.get("low_price"))
                            .trade_price(tmpMap.get("trade_price"))
                            .timestamp(tmpMap.get("timestamp"))
                            .candle_acc_trade_price(tmpMap.get("candle_acc_trade_price"))
                            .candle_acc_trade_volume(tmpMap.get("candle_acc_trade_volume"))
                            .unit(tmpMap.get("unit"))
                            .build();

                    // DTO 리스트에 추가
                    priceDTOList.add(upbitCoinPriceDTO);
                }

                // Entity로 변환하고 저장
                List<UpbitCoinMinutePrice> upbitCoinMinutePrices = priceDTOList.stream()
                        .map(this::DTOtoEntity)
                        .collect(Collectors.toList());

                upbitCoinMinutePriceRepository.saveAll(upbitCoinMinutePrices);
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

