package com.myapp.apiserver.service;

import com.myapp.apiserver.UpbitUtill.UpbitAPI;
import com.myapp.apiserver.repository.UpbitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Log4j2
@RequiredArgsConstructor
public class UpbitAutoTradingBotimpl implements UpbitAutoTradingBot {

    private final UpbitAPI upbitAPI;
    private final UpbitRepository upbitRepository;
    //private final UpbitCoinPriceRepository upbitCoinPriceRepository;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Map<String, BigDecimal> lastKnownPrices = new ConcurrentHashMap<>();
    private boolean running = true;

    @Override
    public void startMonitoring() {
        log.info("자동매매 감시 시작...");

        executorService.execute(() -> {
            while (running) {
                try {
                    // 1. 사용자 설정값(5일, 10일) 가져오기
                    List<Integer> periods = getUserTradingPeriods();

                    // 2. 전체 코인 리스트 조회
                    List<String> coinList = upbitRepository.findAllCoinCodes();

                    // 3. 실시간 가격 가져오기
                    List<Map<String, Object>> currentPriceData = upbitAPI.getCurrentPriceByTicker(coinList);
                    Map<String, BigDecimal> currentPrices = convertToPriceMap(currentPriceData);

                    // 4. 가격 변동 감지 및 매매 조건 체크
                    handlePriceChanges(currentPrices, periods);

                    // 5. 3초 대기 후 다시 감시
                    Thread.sleep(3000);

                } catch (Exception e) {
                    log.error("감시 중 오류 발생", e);
                }
            }
        });
    }

    private List<Integer> getUserTradingPeriods() {
        // 🔹 현재는 DB에서 가져오지 않고 5일, 10일 고정값 사용
        return Arrays.asList(5, 10);

        // 🔹 나중에 사용자 설정 DB에서 가져오려면 아래 코드 사용
        // return tradingSettingRepository.findActivePeriods();
    }

    private Map<String, BigDecimal> convertToPriceMap(List<Map<String, Object>> priceData) {
        Map<String, BigDecimal> priceMap = new HashMap<>();
        for (Map<String, Object> data : priceData) {
            String market = (String) data.get("market");
            BigDecimal tradePrice = new BigDecimal(data.get("trade_price").toString());
            priceMap.put(market, tradePrice);
        }
        return priceMap;
    }

    private void handlePriceChanges(Map<String, BigDecimal> currentPrices, List<Integer> periods) {
        for (Map.Entry<String, BigDecimal> entry : currentPrices.entrySet()) {
            String coin = entry.getKey();
            BigDecimal newPrice = entry.getValue();

            // 기존 가격과 비교하여 일정 변동이 발생한 경우만 업데이트
            BigDecimal lastPrice = lastKnownPrices.getOrDefault(coin, BigDecimal.ZERO);
            BigDecimal priceChange = newPrice.subtract(lastPrice).abs();

            if (lastPrice.compareTo(BigDecimal.ZERO) == 0 ||
                    priceChange.divide(lastPrice, 4, RoundingMode.HALF_UP).compareTo(BigDecimal.valueOf(0.01)) > 0) {

                log.info("가격 변동 감지: {} | 기존: {} → 신규: {}", coin, lastPrice, newPrice);

                // 🔹 이동평균선 및 매매 조건 계산
                checkTradingConditions(coin, newPrice, periods);

                // 🔹 최신 가격 업데이트
                lastKnownPrices.put(coin, newPrice);
            }
        }
    }

    private void checkTradingConditions(String coin, BigDecimal currentPrice, List<Integer> periods) {
        for (Integer period : periods) {
            BigDecimal maValue = calculateMovingAverage(coin, period);

            if (maValue != null) {
                if (currentPrice.compareTo(maValue) > 0) {
                    log.info("{}일 이동평균선 돌파 감지! 코인: {} | 현재가: {} | MA{}: {}", period, coin, currentPrice, period, maValue);
                    executeTrade(coin, "BUY", currentPrice);
                }
            }
        }
    }

    private BigDecimal calculateMovingAverage(String coin, int period) {
        List<Object[]> resultObj = upbitRepository.findDataWithinInterval(String.valueOf(period - 1));
        List<BigDecimal> priceList = new ArrayList<>();

        for (Object[] obj : resultObj) {
            if (coin.equals(obj[0])) {
                priceList.add(new BigDecimal(obj[2].toString()));
            }
        }

        if (priceList.size() < period) return null;
        return priceList.stream().reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(period), 8, RoundingMode.HALF_UP);
    }

    private void executeTrade(String coin, String type, BigDecimal price) {
        log.info("{} 주문 실행: 코인={}, 가격={}", type, coin, price);
        // TODO: 실제 매매 로직 구현 (가상 거래 또는 실거래 API 연동)
    }

    @Override
    public void stopMonitoring() {
        log.info("자동매매 감시 중지...");
        running = false;
        executorService.shutdown();
    }
}
