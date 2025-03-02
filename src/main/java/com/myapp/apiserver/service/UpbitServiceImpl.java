package com.myapp.apiserver.service;

import com.myapp.apiserver.UpbitUtill.UpbitAPI;
import com.myapp.apiserver.UpbitUtill.UpbitUtils;
import com.myapp.apiserver.model.dto.FilterRequestDTO;
import com.myapp.apiserver.model.dto.UpbitAllDataResponseDTO;
import com.myapp.apiserver.model.dto.UpbitCoinDTO;
import com.myapp.apiserver.model.dto.UpbitCoinDayPriceDTO;
import com.myapp.apiserver.model.entity.UpbitCoin;
import com.myapp.apiserver.model.entity.UpbitCoinDayPrice;
import com.myapp.apiserver.repository.UpbitCoinPriceRepository;
import com.myapp.apiserver.repository.UpbitRepository;
import jdk.jfr.Description;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class UpbitServiceImpl implements UpbitService {

    private final UpbitRepository upbitRepository;

    private final UpbitCoinPriceRepository upbitCoinPriceRepository;

    private final UpbitUtils upbitUtils;

    private final ModelMapper modelMapper;

    private final UpbitAPI upbitAPI;


    @Override
    @Description("코인 리스트만 가져오기")
    public List<UpbitCoinDTO> getAllCoinList() {
        List<UpbitCoin> upbitCoins = upbitRepository.findAll(Sort.by(Sort.Direction.ASC, "seq"));
        List<UpbitCoinDTO> upbitCoinDTOList = upbitCoins.stream().map(map -> entityToDTO(map)).collect(Collectors.toList());
        return upbitCoinDTOList;
    }

    @Override
    @Description("코인 리스트와 가격가져오기")
    public List<UpbitAllDataResponseDTO> getAllCoinAndPrice() {
        // 코인리스트 가져오기
        List<UpbitCoin> upbitCoins = upbitRepository.findAll(Sort.by(Sort.Direction.ASC, "seq"));
        List<UpbitCoinDTO> upbitCoinDTO = upbitCoins.stream()
                .map(e -> modelMapper.map(e, UpbitCoinDTO.class)).collect(Collectors.toList());

        // 초기 가격데이터 가져오기
        String targetDate = upbitUtils.getTargetDate();
        List<UpbitCoinDayPrice> upbitCoinDayPrices = upbitRepository.getTodayPriceList(targetDate);
        List<UpbitCoinDayPriceDTO> upbitCoinDayPriceDTOList = upbitCoinDayPrices.stream()
                .map(e -> modelMapper.map(e, UpbitCoinDayPriceDTO.class)).collect(Collectors.toList());

        // 거래데이터가 있는 코인리스트 데이터 가공
        Set<String> coinsWithPrices = upbitCoinDayPrices.stream()
                .map(UpbitCoinDayPrice::getMarket)
                .collect(Collectors.toSet());

        List<String> coinsWithOutPriceList = upbitCoinDTO.stream()
                .filter(coin -> !coinsWithPrices.contains(coin.getMarket()))
                .map(e -> e.getMarket()).toList();

        log.warn(coinsWithOutPriceList.size());

        int size = 20;
        List<Map<String, String>> currentPriceResult = new ArrayList<>();

        if (coinsWithOutPriceList != null && coinsWithOutPriceList.size() > 0) {
            // 실시간조회가 필요한 코인리스트가 있을경우
            for (int i = 0; i < coinsWithOutPriceList.size(); i += size) {
                List<String> paramList = coinsWithOutPriceList.subList(i, Math.min(i + 20, coinsWithOutPriceList.size()));

                // 20개씩 실시간 데이터 요청
                List<Map<String, Object>> currentPriceList = upbitAPI.getCurrentPriceByTicker(paramList);

                // 데이터가 있을경우 resutList 추가
                if (currentPriceList != null && currentPriceList.size() > 0) {
                    currentPriceResult.addAll(upbitUtils.convertPirceData(currentPriceList));
                }
            }

            List<UpbitCoinDayPrice> updateCoinPriceList = currentPriceResult.stream()
                    .map(priceMap -> {
                        UpbitCoinDayPrice upbitCoinDayPrice = modelMapper.map(priceMap, UpbitCoinDayPrice.class);
                        // candle_date가 null인 경우 오늘 날짜 추가 = PK 값임
                        if (upbitCoinDayPrice.getCandle_date_time_kst() == null) {
                            upbitCoinDayPrice.setCandle_date_time_kst(LocalDate.now().toString()); // 또는 LocalDateTime.now()로 시간까지 설정 가능
                        }
                        return upbitCoinDayPrice;
                    })
                    .collect(Collectors.toList());

            upbitCoinPriceRepository.saveAll(updateCoinPriceList);
        }

        for (Map<String, String> rowMap : currentPriceResult) {
            upbitCoinDayPriceDTOList.add(modelMapper.map(rowMap, UpbitCoinDayPriceDTO.class));
        }

        List<UpbitAllDataResponseDTO> responseDTOList = new ArrayList<>();
        for (UpbitCoinDTO coinDTO : upbitCoinDTO) {
            // 코인의 마켓 정보에 맞는 가격 데이터를 찾는다.
            UpbitCoinDayPriceDTO priceDTO = upbitCoinDayPriceDTOList.stream()
                    .filter(p -> p.getMarket().equals(coinDTO.getMarket()))
                    .findFirst()
                    .orElse(null);

            // 두 DTO를 결합하여 새로운 DTO를 빌더로 생성
            UpbitAllDataResponseDTO responseDTO = UpbitAllDataResponseDTO.builder()
                    .market(coinDTO.getMarket())
                    .seq(coinDTO.getSeq())
                    .korean_name(coinDTO.getKorean_name())
                    .english_name(coinDTO.getEnglish_name())
                    .del_flag(coinDTO.getDel_flag())
                    .add_date(coinDTO.getAdd_date())
                    .change_date(coinDTO.getChange_date())

                    // 가격 정보는 priceDTO에서 가져옴
                    .candle_date_time_kst(priceDTO != null ? priceDTO.getCandle_date_time_kst() : null)
                    .candle_date_time_utc(priceDTO != null ? priceDTO.getCandle_date_time_utc() : null)
                    .opening_price(priceDTO != null ? priceDTO.getOpening_price() : null)
                    .high_price(priceDTO != null ? priceDTO.getHigh_price() : null)
                    .low_price(priceDTO != null ? priceDTO.getLow_price() : null)
                    .trade_price(priceDTO != null ? priceDTO.getTrade_price() : null)
                    .timestamp(priceDTO != null ? priceDTO.getTimestamp() : null)
                    .candle_acc_trade_price(priceDTO != null ? priceDTO.getCandle_acc_trade_price() : null)
                    .candle_acc_trade_volume(priceDTO != null ? priceDTO.getCandle_acc_trade_volume() : null)
                    .prev_closing_price(priceDTO != null ? priceDTO.getPrev_closing_price() : null)
                    .change_price(priceDTO != null ? priceDTO.getChange_price() : null)
                    .change_rate(priceDTO != null ? priceDTO.getChange_rate() : null)
                    .build();

            responseDTOList.add(responseDTO);
        }

        return responseDTOList;
    }

    @Override
    @Cacheable(value = "coinCache")
    public List<String> getAllCoinsWithCache() {
        return upbitRepository.findAllCoinCodes();
    }


    @Override
    public List<Map<String, String>> findFilterCoinList(String conditionType, FilterRequestDTO filterRequest) {

        // DTO → List<Map<String, String>> 변환
        List<Map<String, String>> filterList = filterRequest.getFilters().stream()
                .map(filter -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("id", String.valueOf(filter.getId()));
                    map.put("type", filter.getType());
                    map.put("value", filter.getValue().toString());
                    return map;
                })
                .collect(Collectors.toList());

        for(Map<String, String> filterMap : filterList){

            String findType = filterMap.get("type");
            int findValue = Integer.valueOf(filterMap.get("value"));

        }

        // 임시 변수
        int priceRange = 15;

        // resultObj 는 DB에서 이동평균 기준값에대한 쿼리데이터
        List<Object[]> resultObj = upbitRepository.findDataWithinInterval(String.valueOf(priceRange - 1));

        // 전체코인 리스트를 가져온다음
        List<String> coinList = upbitRepository.findAllCoinCodes();

        // 현재가격을 조회하고
        List<Map<String, Object>> currentPriceList = upbitAPI.getCurrentPriceByTicker(coinList);

        // 형태변환을 시켜준다.
        List<Map<String, String>> convertCurrentPriceList = upbitUtils.convertPirceData(currentPriceList);

        // 내부데이터와 실시간데이터를 합친다.
        for (Map<String, String> row : convertCurrentPriceList) {
            String dateStr = row.get("trade_date_kst");
            String formattedDate = dateStr.substring(0, 4) + "-" + dateStr.substring(4, 6) + "-" + dateStr.substring(6, 8);
            Object[] obj2 = {row.get("market"), formattedDate, row.get("trade_price")};

            resultObj.add(obj2);
        }

        List<Map<String, String>> priceListData = new ArrayList<>();

        //코인명으로 루프시작
        for (String coin : coinList) {
            List<BigDecimal> bigDecimalList = new ArrayList<>();
            Map<String, String> priceMap = new HashMap<>();

            for (Object[] obj : resultObj) {
                if(coin.equals(obj[0])){
                    String value = String.valueOf(obj[2]);
                    BigDecimal replaceValue = new BigDecimal(value).setScale(8, RoundingMode.HALF_UP);
                    bigDecimalList.add(replaceValue); // coin명으로 데이터검색 후 bigDecimalList에 담고
                }
            }

            MathContext mc = new MathContext(30, RoundingMode.HALF_UP);
            int maxScale = bigDecimalList.stream()
                    .map(BigDecimal::stripTrailingZeros)
                    .map(BigDecimal::scale)
                    .max(Integer::compareTo)
                    .orElse(8);

            if(bigDecimalList.size() == priceRange){ // priceRange는 오늘날짜를 제외하기때문에 +1해서 검색기간값을 맞춘다.

                BigDecimal totalPrice = bigDecimalList.stream()
                        .reduce(BigDecimal.ZERO, (a, b) -> a.add(b, mc))
                        .setScale(maxScale, RoundingMode.HALF_UP);

                BigDecimal finalPrice = totalPrice.divide(BigDecimal.valueOf(priceRange), maxScale, RoundingMode.HALF_UP);

                priceMap.put(coin, finalPrice.toPlainString());
            }

            if (!priceMap.isEmpty()) {
                priceListData.add(priceMap);
            }
        }

        // 최종 실시간데이터와 평균값을 비교한다
        List<Map<String, String>> resultPriceData = new ArrayList<>();

        for(Map<String, String> avgPrice : priceListData){
            for(Map.Entry<String, String> entry : avgPrice.entrySet()){

                String coinName = entry.getKey();
                for(Map<String, String> currentPrice : convertCurrentPriceList){
                    if(coinName.equals(currentPrice.get("market"))){
                        BigDecimal currentValue = new BigDecimal(currentPrice.get("trade_price"));
                        BigDecimal avgValue = new BigDecimal(entry.getValue());

                        if(avgValue.compareTo(currentValue) < 0){
                            Map<String, String> resultMap = new HashMap<>();
                            resultMap.put("market", entry.getKey());
                            resultMap.put("currentPrice", currentPrice.get("trade_price"));

                            BigDecimal percentageChange = currentValue.subtract(avgValue) // 비교 가격 - 현재 가격
                                    .divide(avgValue, 10, RoundingMode.HALF_UP) // 현재 가격으로 나누기 (소수점 10자리 유지)
                                    .multiply(BigDecimal.valueOf(100));

                            resultMap.put("percentageChange", percentageChange + "%");
                            resultPriceData.add(resultMap);
                        }
                    }
                }
            }
        }
        return resultPriceData;
    }

    public List<Map<String, String>> findFilterCoinList_two(int priceRange, String volume) {
        // resultObj 는 DB에서 이동평균 기준값에대한 쿼리데이터
        List<Object[]> resultObj = upbitRepository.findDataWithinInterval(String.valueOf(priceRange - 1));

        // 전체코인 리스트를 가져온다음
        List<String> coinList = upbitRepository.findAllCoinCodes();

        // 현재가격을 조회하고
        List<Map<String, Object>> currentPriceList = upbitAPI.getCurrentPriceByTicker(coinList);

        // 형태변환을 시켜준다.
        List<Map<String, String>> convertCurrentPriceList = upbitUtils.convertPirceData(currentPriceList);

        // 내부데이터와 실시간데이터를 합친다.
        for (Map<String, String> row : convertCurrentPriceList) {
            String dateStr = row.get("trade_date_kst");
            String formattedDate = dateStr.substring(0, 4) + "-" + dateStr.substring(4, 6) + "-" + dateStr.substring(6, 8);
            Object[] obj2 = {row.get("market"), formattedDate, row.get("trade_price")};

            resultObj.add(obj2);
        }

        List<Map<String, String>> priceListData = new ArrayList<>();

        //코인명으로 루프시작
        for (String coin : coinList) {
            List<BigDecimal> bigDecimalList = new ArrayList<>();
            Map<String, String> priceMap = new HashMap<>();

            for (Object[] obj : resultObj) {
                if(coin.equals(obj[0])){
                    String value = String.valueOf(obj[2]);
                    BigDecimal replaceValue = new BigDecimal(value).setScale(8, RoundingMode.HALF_UP);
                    bigDecimalList.add(replaceValue); // coin명으로 데이터검색 후 bigDecimalList에 담고
                }
            }

            MathContext mc = new MathContext(30, RoundingMode.HALF_UP);
            int maxScale = bigDecimalList.stream()
                    .map(BigDecimal::stripTrailingZeros)
                    .map(BigDecimal::scale)
                    .max(Integer::compareTo)
                    .orElse(8);

            if(bigDecimalList.size() == priceRange){ // priceRange는 오늘날짜를 제외하기때문에 +1해서 검색기간값을 맞춘다.

                BigDecimal totalPrice = bigDecimalList.stream()
                        .reduce(BigDecimal.ZERO, (a, b) -> a.add(b, mc))
                        .setScale(maxScale, RoundingMode.HALF_UP);

                BigDecimal finalPrice = totalPrice.divide(BigDecimal.valueOf(priceRange), maxScale, RoundingMode.HALF_UP);

                priceMap.put(coin, finalPrice.toPlainString());
            }

            if (!priceMap.isEmpty()) {
                priceListData.add(priceMap);
            }
        }

        // 최종 실시간데이터와 평균값을 비교한다
        List<Map<String, String>> resultPriceData = new ArrayList<>();

        for(Map<String, String> avgPrice : priceListData){
            for(Map.Entry<String, String> entry : avgPrice.entrySet()){

                String coinName = entry.getKey();
                for(Map<String, String> currentPrice : convertCurrentPriceList){
                    if(coinName.equals(currentPrice.get("market"))){
                        BigDecimal currentValue = new BigDecimal(currentPrice.get("trade_price"));
                        BigDecimal avgValue = new BigDecimal(entry.getValue());

                        if(avgValue.compareTo(currentValue) < 0){
                            Map<String, String> resultMap = new HashMap<>();
                            resultMap.put("market", entry.getKey());
                            resultMap.put("currentPrice", currentPrice.get("trade_price"));

                            BigDecimal percentageChange = currentValue.subtract(avgValue) // 비교 가격 - 현재 가격
                                    .divide(avgValue, 10, RoundingMode.HALF_UP) // 현재 가격으로 나누기 (소수점 10자리 유지)
                                    .multiply(BigDecimal.valueOf(100));

                            resultMap.put("percentageChange", percentageChange + "%");
                            resultPriceData.add(resultMap);
                        }
                    }
                }
            }
        }
        return resultPriceData;
    }
}
