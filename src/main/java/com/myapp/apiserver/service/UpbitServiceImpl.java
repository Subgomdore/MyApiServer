package com.myapp.apiserver.service;

import com.myapp.apiserver.UpbitUtill.UpbitAPI;
import com.myapp.apiserver.UpbitUtill.UpbitUtils;
import com.myapp.apiserver.domain.UpbitCoin;
import com.myapp.apiserver.domain.UpbitCoinPrice;
import com.myapp.apiserver.dto.UpbitCoinDTO;
import com.myapp.apiserver.dto.UpbitCoinPriceDTO;
import com.myapp.apiserver.repository.UpbitCoinPriceRepository;
import com.myapp.apiserver.repository.UpbitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class UpbitServiceImpl implements UpbitService {

    private final UpbitRepository upbitRepository;

    private final UpbitCoinPriceRepository upbitCoinPriceRepository;

    private final UpbitAPI upbitAPI;

    @Override
    public List<UpbitCoinDTO> getALlCoinList() {
        List<UpbitCoin> upbitCoin = upbitRepository.findAll(Sort.by(Sort.Direction.ASC, "seq"));
        List<UpbitCoinDTO> upbitCoinDTOList = upbitCoin.stream().map(map -> entityToDTO(map)).collect(Collectors.toList());
        return upbitCoinDTOList;
    }

    @Override
    public Map<String, String> fetchAndSync() {
        Map<String, String> resultMap = new HashMap<String, String>();

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

            // DTO -> Entity
            List<UpbitCoin> upbitCoins = coinDTOList.stream()
                    .map(dto -> DTOtoEntity(dto)).collect(Collectors.toList());

            // Entity -> Save
            upbitCoins.forEach(entity -> upbitRepository.save(entity));

            resultMap.put("RESULT", "성공");
        } catch (Exception e) {
            resultMap.put("RESULT", "실패");
            resultMap.put("ERR", e.toString());
        } finally {
            return resultMap;
        }

    }

    @Override
    public Map<String, String> fetchPriceAndSync() {


        try {
            List<UpbitCoinDTO> coinList = getALlCoinList();

            List<String> marketList =
                    coinList.stream().map(UpbitCoinDTO::getMarket).collect(Collectors.toList());

            for (String market : marketList) {
                Map<String, String> paramsMap = new HashMap<String, String>();

                paramsMap.put("market", market);
//                paramsMap.put("to", "aa");

                List<Map<String, Object>> resultList = upbitAPI.doFetchPriceAndSync(paramsMap);

                List<Map<String, String>> convertList = new ArrayList<Map<String, String>>();
                for (Map<String, Object> rowMap : resultList) {
                    Map<String, String> convertMap = new HashMap<String, String>();
                    for (Entry<String, Object> entry : rowMap.entrySet()) {
                        convertMap.put(entry.getKey(), entry.getValue().toString());
                    }
                    convertList.add(convertMap);
                }

                // DTO 리스트 생성
                List<UpbitCoinPriceDTO> priceDTOList = new ArrayList<>();

                // resultList => DTO
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

                Thread.sleep(1000);
            }

            return Map.of("result", "Price synchronization completed successfully");

        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("error", "Price synchronization failed due to an IOException.");
        }
    }
}
