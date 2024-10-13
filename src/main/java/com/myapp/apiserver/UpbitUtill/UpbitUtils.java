package com.myapp.apiserver.UpbitUtill;

import com.myapp.apiserver.component.EntityMapper;
import com.myapp.apiserver.model.entity.UpbitCoin;
import com.myapp.apiserver.model.entity.UpbitCoinDayPrice;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Log4j2
public class UpbitUtils {

    private final ModelMapper modelMapper;
    private final EntityMapper entityMapper;

    public static String getTargetDate() {

        String targetDate = "";

        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        // 업비트 거래종료 오전9시: 비교일 생성
        LocalTime upbitTime = LocalTime.of(9, 0);

        // 오전9시 이전일경우 (00 ~ 08:59)
        if (currentTime.isBefore(upbitTime)) {
            // 오늘날짜 -1일이 조회타겟대상
            currentDate = currentDate.minusDays(1);
        }
        targetDate = currentDate.toString();

        return targetDate;
    }

    // 제네릭 메소드: 여러 타입의 데이터를 처리
    public <T> List<T> dataConvertProcess(List<T> entities) {
        // 여기서 T는 CoinEntity, CoinPrice 등 다양한 엔티티 타입일 수 있음
        return entities.stream()
                .map(entity -> processEntityData(entity))  // 동적 데이터 처리
                .collect(Collectors.toList());
    }

    // 제네릭 타입을 처리하는 메소드
    private <T> T processEntityData(T entity) {
        if (entity instanceof UpbitCoin) {
            UpbitCoin upbitCoin = (UpbitCoin) entity;
            Map<String, Object> upbitCoinMap = entityMapper.convertToMap(upbitCoin);

        } else if (entity instanceof UpbitCoinDayPrice) {
            UpbitCoinDayPrice upbitCoinDayPrice = (UpbitCoinDayPrice) entity;
            Map<String, Object> upbitCoinPriceMap = entityMapper.convertToMap(upbitCoinDayPrice);

            log.info(upbitCoinPriceMap.get("market"));
            log.info( upbitCoinPriceMap.get("trade_price"));

        }
        return entity;
    }


    public List<Map<String, String>> convertPirceData(List<Map<String, Object>> resultList){

        List<Map<String, String>> convertList = new ArrayList<>();

        for (Map<String, Object> rowMap : resultList) {
            Map<String, String> convertMap = new HashMap<>();
            for (Map.Entry<String, Object> entry : rowMap.entrySet()) {
                if(entry.getValue().equals("null") || entry.getValue() == null) {
                    convertMap.put(entry.getKey(), "0");
                }else {
                    convertMap.put(entry.getKey(), entry.getValue().toString());
                }
            }
            convertList.add(convertMap);
        }

        // 가격데이터 업비트 형태와 유사하게 가공처리
        for (Map<String, String> rowMap : convertList) {
            for (Map.Entry<String, String> entry : rowMap.entrySet()) {
                if (entry.getKey().endsWith("_price")) {
                    double number = Double.parseDouble(entry.getValue());
                    String formattedNumber;
                    if (number < 1) {
                        // 소수점 5번째 자리 이후 0일 경우 제거, 최소 4자리까지 표시
                        formattedNumber = new DecimalFormat("0.0000######").format(number);
                    } else if (number > 1 && number < 1000) {
                        // 1000원이하 가격들은 소숫점 1자리 표시
                        formattedNumber = new DecimalFormat("#.0").format(number);
                    } else if (number >= 1000) {
                        // 1000 이상인 경우 소수점 제거
                        formattedNumber = new DecimalFormat("#").format(number);
                    } else {
                        formattedNumber = String.valueOf(number);
                    }
                    rowMap.put(entry.getKey(), formattedNumber);
                }else if (entry.getKey().startsWith("candle_date")){
                    // 날짜형태 컨버팅
                    rowMap.put(entry.getKey(), entry.getValue().replaceAll("T.*", ""));
                }
            }
        }

        return convertList;
    }
}
