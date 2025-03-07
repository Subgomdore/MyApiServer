package com.myapp.apiserver.service;

import com.myapp.apiserver.UpbitUtill.UpbitAPI;
import com.myapp.apiserver.UpbitUtill.UpbitUtils;
import com.myapp.apiserver.model.dto.FilterRequestDTO;
import com.myapp.apiserver.model.dto.UpbitAllDataResponseDTO;
import com.myapp.apiserver.model.dto.UpbitCoinDTO;
import com.myapp.apiserver.model.dto.UpbitCoinDayPriceDTO;
import com.myapp.apiserver.model.entity.UpbitCoin;
import com.myapp.apiserver.model.entity.UpbitCoinDayPrice;
import com.myapp.apiserver.repository.UpbitCoinDayPriceSpecification;
import com.myapp.apiserver.repository.UpbitCoinDayPriceRepository;
import com.myapp.apiserver.repository.UpbitRepository;
import jdk.jfr.Description;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class UpbitServiceImpl implements UpbitService {

    private final UpbitRepository upbitRepository;

    private final UpbitCoinDayPriceRepository upbitCoinDayPriceRepository;

    private final UpbitUtils upbitUtils;

    private final ModelMapper modelMapper;

    private final UpbitAPI upbitAPI;


    @Override
    @Description("코인 리스트만 가져오기")
    public List<UpbitCoinDTO> getAllCoinList() {
        List<UpbitCoin> upbitCoins = upbitRepository.findAll(Sort.by(Sort.Direction.ASC, "seq"));
        return upbitCoins.stream().map(map -> entityToDTO(map)).collect(Collectors.toList());
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

            upbitCoinDayPriceRepository.saveAll(updateCoinPriceList);
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
                    map.put("indicator", String.valueOf(filter.getIndicator()));
                    map.put("operator", filter.getOperator());
                    map.put("value", filter.getValue().toString());
                    return map;
                })
                .collect(Collectors.toList());

        // 타겟시장 변환 BTC KRW USDT
        List<String> marketList = filterRequest.getMarkets().stream()
                .toList();

        // TRAGET MARKET CODE를 가져오고
        List<UpbitCoin> upbitCoins = upbitRepository.findByMarketPrefixes(marketList);
        List<String> coinList = upbitCoins.stream()
                .map(e -> e.getMarket())
                .collect(Collectors.toList());

        // 업비트에서 TARGET 대상만 실시간 데이터를 가져오고
        List<Map<String, Object>> currentPriceList = upbitAPI.getCurrentPriceByTicker(coinList);

        // 데이터 형변환
        List<Map<String, String>> convertCurrentPriceList = upbitUtils.convertPirceData(currentPriceList);

        // 실제간데이터를 연산이 가능하도록 기존형태와 똑같이 형변환해줌
        List<Map<String, String>> resultPriceList = new ArrayList<>();
        for (Map<String, String> rowMap : convertCurrentPriceList) {
            String dateStr = rowMap.get("trade_date_kst");
            String formattedDate = dateStr.substring(0, 4) + "-" + dateStr.substring(4, 6) + "-" + dateStr.substring(6, 8);

            Map<String, String> inputMap = new HashMap<>();
            inputMap.put("market", rowMap.get("market"));
            inputMap.put("candle_date_time_kst", formattedDate);
            inputMap.put("trade_price", rowMap.get("trade_price"));

            resultPriceList.add(inputMap);
        }

        // 필터에 맞는 연산값을 모두 저장할수있도록 한다.
        List<List<Map<String, String>>> allIndicatorResults = new ArrayList<>();

        for (Map<String, String> filterMap : filterList) {

            String indicator = filterMap.get("indicator").toUpperCase();  // 예: "MA", "RSI"
            String operator = filterMap.get("operator").toUpperCase(); // ABOVE(이상) , BELOW(이하)
            int period = Integer.valueOf(filterMap.get("value"));  // 기준값 혹은 기간 등

            LocalDate currentDay = LocalDate.now();

            String startDate = String.valueOf(currentDay.minusDays(period));
            String endDate = String.valueOf(currentDay.minusDays(1));

            List<UpbitCoinDayPrice> result = upbitCoinDayPriceRepository.findAll(
                    Specification.where(UpbitCoinDayPriceSpecification.candleDateBetween(startDate, endDate))
                            .and(UpbitCoinDayPriceSpecification.marketStartsWithAny(marketList))
            );

            List<Map<String, String>> resultMapList = result.stream()
                    .map(dayPrice -> {
                        Map<String, String> map = new HashMap<>();
                        map.put("market", dayPrice.getMarket());
                        map.put("candle_date_time_kst", dayPrice.getCandle_date_time_kst());
                        map.put("trade_price", dayPrice.getTrade_price());
                        return map;
                    })
                    .collect(Collectors.toList());

            resultMapList.addAll(resultPriceList);

            // 여기까지 BTC , KRW , USDT 및 날짜조회에 대한 데이터조회검증완료
            // -----------------------------------------------------------

            List<Map<String, String>> indicatorResult = new ArrayList<>();
            switch (indicator) {
                case "MA":
                    if ("ABOVE".equals(operator)) {
                        indicatorResult = applyMovingAverageFilter(resultMapList, period, "ABOVE");
                    } else if ("BELOW".equals(operator)) {
                        indicatorResult = applyMovingAverageFilter(resultMapList, period, "BELOW");
                    }
                    break;
                case "RSI":
                    if ("ABOVE".equals(operator)) {
                        indicatorResult = applyRSIFilter(resultMapList, period, "ABOVE");
                    } else if ("BELOW".equals(operator)) {
                        indicatorResult = applyRSIFilter(resultMapList, period, "BELOW");
                    }
                    break;
                default:
                    // indicator에 해당하지 않으면 빈 리스트 처리
                    indicatorResult = new ArrayList<>();
                    break;
            }

            allIndicatorResults.add(indicatorResult);

        }

        List<Map<String, String>> finalResult = aggregateFilterResultsByOperator(allIndicatorResults, conditionType);

        return finalResult;
    }

    private List<Map<String, String>> aggregateFilterResultsByOperator(List<List<Map<String, String>>> allFilterResults, String conditionType) {
        List<Map<String, String>> finalResult = new ArrayList<>();

        if ("OR".equalsIgnoreCase(conditionType)) {
            // OR 조건: 모든 필터 결과의 합집합 (기존 방식)
            Set<Map<String, String>> unionSet = new HashSet<>();
            for (List<Map<String, String>> filterResult : allFilterResults) {
                unionSet.addAll(filterResult);
            }
            finalResult.addAll(unionSet);
        } else if ("AND".equalsIgnoreCase(conditionType)) {
            if (!allFilterResults.isEmpty()) {
                // 각 필터 결과에서 market 키만 추출하여 집합으로 만듦
                Set<String> intersectionMarkets = new HashSet<>(allFilterResults.get(0).stream()
                        .map(map -> map.get("market"))
                        .collect(Collectors.toSet()));
                for (int i = 1; i < allFilterResults.size(); i++) {
                    Set<String> currentMarkets = allFilterResults.get(i).stream()
                            .map(map -> map.get("market"))
                            .collect(Collectors.toSet());
                    intersectionMarkets.retainAll(currentMarkets);
                }
                // 최종 결과에 intersectionMarkets에 해당하는 market만 추가 (추가 정보는 필요에 따라 선택)
                for (String market : intersectionMarkets) {
                    Map<String, String> resultMap = new HashMap<>();
                    resultMap.put("market", market);
                    finalResult.add(resultMap);
                }
            }
        }
        return finalResult;
    }


    /**
     * 주어진 데이터에서 이동평균 조건(이상/이하)을 만족하는 항목만 필터링합니다.
     *
     * @param resultMapList 원본 데이터 리스트 (각 Map은 코인 데이터)
     * @param period        기준값
     * @param operator      이상/이하 조건
     * @return 이동평균 조건을 만족하는 데이터 리스트
     */
    public List<Map<String, String>> applyMovingAverageFilter(List<Map<String, String>> resultMapList, int period, String operator) {
        List<Map<String, String>> filteredResults = new ArrayList<>();

        // 그룹핑: 마켓별로 데이터를 모음
        Map<String, List<Map<String, String>>> marketGroups = new HashMap<>();
        for (Map<String, String> record : resultMapList) {
            String market = record.get("market");
            marketGroups.computeIfAbsent(market, k -> new ArrayList<>()).add(record);
        }

        // 날짜 파싱을 위한 포맷터 (데이터의 candle_date_time_kst가 "yyyy-MM-dd" 형식이라고 가정)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // 각 마켓 그룹별로 처리
        for (Map.Entry<String, List<Map<String, String>>> entry : marketGroups.entrySet()) {
            List<Map<String, String>> records = entry.getValue();

            // 날짜 오름차순으로 정렬 (오래된 날짜부터 최신 날짜 순)
            records.sort(Comparator.comparing(r -> LocalDate.parse(r.get("candle_date_time_kst"), formatter)));

            // 지정된 기간의 데이터가 충분하지 않으면 건너뜀
            if (records.size() < period) {
                continue;
            }

            // 마지막 period 개의 레코드에 대해 이동평균 계산
            BigDecimal sum = BigDecimal.ZERO;
            for (int i = records.size() - period; i < records.size(); i++) {
                BigDecimal price = new BigDecimal(records.get(i).get("trade_price"));
                sum = sum.add(price);
            }
            BigDecimal movingAverage = sum.divide(BigDecimal.valueOf(period), 8, RoundingMode.HALF_UP);

            // 최신 레코드의 가격 (가장 마지막 레코드)
            Map<String, String> latestRecord = records.get(records.size() - 1);
            BigDecimal currentPrice = new BigDecimal(latestRecord.get("trade_price"));

            // operator에 따라 조건 검사
            boolean conditionSatisfied = false;
            if ("ABOVE".equalsIgnoreCase(operator)) {
                conditionSatisfied = currentPrice.compareTo(movingAverage) > 0;
            } else if ("BELOW".equalsIgnoreCase(operator)) {
                conditionSatisfied = currentPrice.compareTo(movingAverage) < 0;
            }

            if (conditionSatisfied) {
                // 평균가와 현재가의 차이를 백분율로 계산
                BigDecimal difference = currentPrice.subtract(movingAverage);
                BigDecimal percentageChange = difference.divide(movingAverage, 8, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
                // 결과에 추가할 정보를 새로운 Map에 담음 (불필요한 소숫점 제거)
                Map<String, String> resultMap = new HashMap<>();
                resultMap.put("market", entry.getKey());
                resultMap.put("currentPrice", currentPrice.stripTrailingZeros().toPlainString());
                resultMap.put("movingAverage", movingAverage.stripTrailingZeros().toPlainString());
                resultMap.put("percentageChange", percentageChange.stripTrailingZeros().toPlainString() + "%");
                filteredResults.add(resultMap);
            }
        }

        return filteredResults;
    }

    public List<Map<String, String>> applyRSIFilter(List<Map<String, String>> resultMapList, int rsiThreshold, String operator) {
        List<Map<String, String>> filteredResults = new ArrayList<>();

        // RSI 계산에 사용할 기간 (일반적으로 14일)
        int rsiPeriod = 14;

        // 그룹핑: 마켓별로 데이터를 모음
        Map<String, List<Map<String, String>>> marketGroups = new HashMap<>();
        for (Map<String, String> record : resultMapList) {
            String market = record.get("market");
            marketGroups.computeIfAbsent(market, k -> new ArrayList<>()).add(record);
        }

        // 날짜 파싱을 위한 포맷터 (데이터의 candle_date_time_kst가 "yyyy-MM-dd" 형식이라고 가정)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // 각 마켓 그룹별로 처리
        for (Map.Entry<String, List<Map<String, String>>> entry : marketGroups.entrySet()) {
            List<Map<String, String>> records = entry.getValue();

            // 날짜 오름차순으로 정렬 (오래된 날짜부터 최신 날짜 순)
            records.sort(Comparator.comparing(r -> LocalDate.parse(r.get("candle_date_time_kst"), formatter)));

            // RSI 계산을 위해 최소한 (rsiPeriod + 1)개의 데이터 필요
            if (records.size() < rsiPeriod + 1) {
                continue;
            }

            // RSI 계산: 최근 rsiPeriod개의 변화량을 기반으로 평균 상승과 평균 하락을 계산
            BigDecimal gainSum = BigDecimal.ZERO;
            BigDecimal lossSum = BigDecimal.ZERO;
            for (int i = records.size() - rsiPeriod; i < records.size(); i++) {
                BigDecimal previousPrice = new BigDecimal(records.get(i - 1).get("trade_price"));
                BigDecimal currentPrice = new BigDecimal(records.get(i).get("trade_price"));
                BigDecimal change = currentPrice.subtract(previousPrice);
                if (change.compareTo(BigDecimal.ZERO) > 0) {
                    gainSum = gainSum.add(change);
                } else {
                    lossSum = lossSum.add(change.abs());
                }
            }
            BigDecimal avgGain = gainSum.divide(BigDecimal.valueOf(rsiPeriod), 8, RoundingMode.HALF_UP);
            BigDecimal avgLoss = lossSum.divide(BigDecimal.valueOf(rsiPeriod), 8, RoundingMode.HALF_UP);
            BigDecimal rsi;
            if (avgLoss.compareTo(BigDecimal.ZERO) == 0) {
                rsi = BigDecimal.valueOf(100);
            } else {
                BigDecimal rs = avgGain.divide(avgLoss, 8, RoundingMode.HALF_UP);
                rsi = BigDecimal.valueOf(100).subtract(
                        BigDecimal.valueOf(100).divide(BigDecimal.ONE.add(rs), 8, RoundingMode.HALF_UP)
                );
            }

            // 최신 레코드의 가격 (가장 마지막 레코드)
            Map<String, String> latestRecord = records.get(records.size() - 1);
            BigDecimal currentPrice = new BigDecimal(latestRecord.get("trade_price"));

            // operator에 따라 조건 검사: 현재 RSI가 임계치보다 높거나 낮은지 비교
            boolean conditionSatisfied = false;
            if ("ABOVE".equalsIgnoreCase(operator)) {
                conditionSatisfied = rsi.compareTo(BigDecimal.valueOf(rsiThreshold)) > 0;
            } else if ("BELOW".equalsIgnoreCase(operator)) {
                conditionSatisfied = rsi.compareTo(BigDecimal.valueOf(rsiThreshold)) < 0;
            }

            if (conditionSatisfied) {
                // 결과에 추가할 정보를 새로운 Map에 담음 (불필요한 소숫점 제거)
                Map<String, String> resultMap = new HashMap<>();
                resultMap.put("market", entry.getKey());
                resultMap.put("currentPrice", currentPrice.stripTrailingZeros().toPlainString());
                resultMap.put("RSI", rsi.stripTrailingZeros().toPlainString());
                filteredResults.add(resultMap);
            }
        }

        return filteredResults;
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
                if (coin.equals(obj[0])) {
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

            if (bigDecimalList.size() == priceRange) { // priceRange는 오늘날짜를 제외하기때문에 +1해서 검색기간값을 맞춘다.

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

        for (Map<String, String> avgPrice : priceListData) {
            for (Map.Entry<String, String> entry : avgPrice.entrySet()) {

                String coinName = entry.getKey();
                for (Map<String, String> currentPrice : convertCurrentPriceList) {
                    if (coinName.equals(currentPrice.get("market"))) {
                        BigDecimal currentValue = new BigDecimal(currentPrice.get("trade_price"));
                        BigDecimal avgValue = new BigDecimal(entry.getValue());

                        if (avgValue.compareTo(currentValue) < 0) {
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
