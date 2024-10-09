package com.myapp.apiserver.service;

import com.myapp.apiserver.UpbitUtill.UpbitAPI;
import com.myapp.apiserver.UpbitUtill.UpbitUtils;
import com.myapp.apiserver.model.dto.UpbitCoinDTO;
import com.myapp.apiserver.model.dto.UpbitCoinPriceDTO;
import com.myapp.apiserver.model.entity.UpbitCoin;
import com.myapp.apiserver.model.entity.UpbitCoinPrice;
import com.myapp.apiserver.repository.UpbitCoinPriceRepository;
import com.myapp.apiserver.repository.UpbitRepository;
import com.myapp.apiserver.websocket.UpbitWebSocketClient;
import jakarta.annotation.PostConstruct;
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
    private final UpbitCoinPriceRepository upbitCoinPriceRepository;
    private final UpbitWebSocketClient upbitWebSocketClient;

    @Override
    public void doGetUpbitCoinList() {
        try {
            List<UpbitCoinDTO> coinDTOList = new ArrayList<>();
            List<Map<String, Object>> resultList = upbitAPI.doRegisterAllCoinList();

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

            upbitCoins.forEach(entity -> upbitRepository.save(entity));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doGetUpbitCoinPrice(String count) {
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

                List<Map<String, Object>> resultList = upbitAPI.doFetchPriceAndSync(paramsMap);

                List<Map<String, String>> convertList = new ArrayList<>();
                for (Map<String, Object> rowMap : resultList) {
                    Map<String, String> convertMap = new HashMap<>();
                    for (Map.Entry<String, Object> entry : rowMap.entrySet()) {
                        convertMap.put(entry.getKey(), entry.getValue().toString());
                    }
                    convertList.add(convertMap);
                }

                // DTO 리스트 생성
                List<UpbitCoinPriceDTO> priceDTOList = new ArrayList<>();

                // 특정코인정보에 대한 가격정보 List를 DTO로 변환
                for (Map<String, String> tmpMap : convertList) {
                    UpbitCoinPriceDTO upbitCoinPriceDTO = UpbitCoinPriceDTO.builder()
                            .market(tmpMap.get("market"))
                            .candle_date_time_kst(UpbitUtils.parseLocalDateTime(tmpMap.get("candle_date_time_kst") + ":00:00").toString())
                            .candle_date_time_utc(UpbitUtils.parseLocalDateTime(tmpMap.get("candle_date_time_utc") + ":00:00").toString())
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
                List<UpbitCoinPrice> upbitCoinPrices = priceDTOList.stream()
                        .map(this::DTOtoEntity)  // DTO를 Entity로 변환
                        .collect(Collectors.toList());

                upbitCoinPriceRepository.saveAll(upbitCoinPrices);
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
