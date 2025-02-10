package com.myapp.apiserver.UpbitUtill;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.myapp.apiserver.component.CustomJsonDeserializer;
import com.myapp.apiserver.model.dto.api.ApiResponse;
import jdk.jfr.Description;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@Component
@RequiredArgsConstructor
public class UpbitAPI {

    private final ApiClientUtil apiClientUtil;

    @Description("시세 종목 조회 > 종목 코드조회")
    public List<Map<String, Object>> getMarketCode() {
        String url = "https://api.upbit.com/v1/market/all?isDetails=false";
        String jsonResponse;
        List<Map<String, Object>> coinListMap = null;

        try {
            ApiResponse apiResponse = apiClientUtil.executeGetRequest(url);
            // DTO를 RES했으니까 getter를 통해 getBody()값을 가져온다.
            jsonResponse = apiResponse.getBody();
            coinListMap = new Gson().fromJson(jsonResponse, List.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return coinListMap;
    }

    @Description("시세 캔들 조회 > 분(Minute) 캔들")
    public void getCandleByMinute() {

    }

    @Description("시세 캔들조회 > 일(Day) 캔들")
    public void getCandleByDay() {

    }

    @Description("시세 캔들조회 > 주(Week) 캔들")
    public void getCandleByWeek() {

    }

    @Description("시세 캔들조회 > 일(Day) 캔들")
    public List<Map<String, Object>> getCandleByDay(Map<String, String> paramsMap) throws IOException {

        String market = paramsMap.get("market"); // 마켓 코드
        String to = MapUtils.getString(paramsMap, "to", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "T09:00:00");
        String count = MapUtils.getString(paramsMap, "count", "200");  // count가 없으면 기본값 "200"
        String convertingPriceUnit = MapUtils.getString(paramsMap, "convertingPriceUnit", "KRW"); // 종가 환산 화폐 단위, 생략 가능
        String url = null;


        int min = 0;
        int sec = 0;

        List<Map<String, Object>> coinPriceList = new ArrayList<>();
        try {
            // 필수 매개변수
            if (paramsMap.get("market").isEmpty()) {
                Logger.errInfo();
                throw new Exception("The request parameters are invalid.");
            }

            boolean reqFlag = true;
            while (reqFlag) {
                url = String.format("https://api.upbit.com/v1/candles/days?market=%s&to=%s&count=%s&convertingPriceUnit=%s",
                        market, to, count, convertingPriceUnit);
                ApiResponse apiResponse = apiClientUtil.executeGetRequest(url);
                if (!apiResponse.getBody().isBlank()) {

                    String jsonResponse = apiResponse.getBody();
                    CustomJsonDeserializer customJson = new CustomJsonDeserializer();

                    Map<String, String> headerMap = apiResponse.getHeaders();
                    String requestLimit = headerMap.get("remaining-req");

                    Map<String, Integer> remainingReqCount = calculateRemainingCount(requestLimit);
                    if (!remainingReqCount.isEmpty()) {
                        min = remainingReqCount.get("min");
                        sec = remainingReqCount.get("sec");
                    }
                    // JsonString 파싱
                    JsonElement jsonElement = JsonParser.parseString(jsonResponse);

                    if (jsonElement.isJsonArray()) {
                        JsonArray jsonArray = jsonElement.getAsJsonArray();

                        coinPriceList.addAll(customJson.parseJsonArray(jsonResponse));

                        // 다시 조회할 데이터를 갱신하고
                        String changeTo = (String) coinPriceList.get(coinPriceList.size() - 1).get("candle_date_time_kst") + ":00:00";
                        if (to.equals(changeTo)) {
                            // 더이상 마지막 날짜의 데이터가, 똑같을경우 마지막데이터까지 가져왔으니 flag 변경
                            reqFlag = false;
                        } else {
                            // 날짜가 다를경우 데이터 갱신후 while문 동작
                            log.info(to);
                            log.info(changeTo);
                            to = changeTo;
                        }
                    }
                }

                if (min < 1 || sec < 1) {
                    Thread.sleep(1000);
                }

                log.warn("MIN> " + min + " / " + "SEC> " + sec);
                log.warn("[ " + paramsMap.get("ALL_COUNT") + " / " + paramsMap.get("PROGRESS") + " ] >> Market : " + market);
            }
        } catch (Exception e) {
            log.error(e);
            log.error(url);
            e.printStackTrace();
        } finally {

            return coinPriceList;
        }
    }

    @Description("시세 체결 조회 > 최근 체결 내역")
    public void getRecentSettlements() {

    }

    @Description("시세 현자가(Ticker) 조회 > 종목단위 현재가 정보")
    public List<Map<String, Object>> getCurrentPriceByTicker(List<String> coinList) {
        String markets = "";

        if (coinList != null && coinList.size() > 0) {
            markets = String.join(",", coinList);
        }

        String url = "https://api.upbit.com/v1/ticker?markets=" + markets;
        log.info(url);
        List<Map<String, Object>> coinListMap = null;
        try {
            ApiResponse apiResponse = apiClientUtil.executeGetRequest(url);
            String jsonResponse = apiResponse.getBody();
            Map<String, String> headerMap = apiResponse.getHeaders();
            coinListMap = new Gson().fromJson(jsonResponse, List.class);

            String requestLimit = headerMap.get("remaining-req");
            Map<String, Integer> remainingReqCount = calculateRemainingCount(requestLimit);

            if (remainingReqCount.get("min") < 1 || remainingReqCount.get("sec") < 1) {
                Thread.sleep(1000);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return coinListMap;
    }

    @Description("시세 현자가(Ticker) 조회 > 마켓 단위 현재가 정보")
    public void getCurrentPriceByMarket() {

    }


    @Description("API 요청 회수 계산")
    private Map<String, Integer> calculateRemainingCount(String remainingReq) {
        Map<String, Integer> resultMap = new HashMap<>();
        if (remainingReq == null || remainingReq.isBlank()) {
            log.error("Response header data is null! Please check the parameters.");
            return resultMap;
        }

        try {
            String[] parts = remainingReq.split(";");
            for (String part : parts) {
                part = part.trim();
                if (part.startsWith("min=")) {
                    resultMap.put("min", Integer.valueOf(part.substring(4)));
                } else if (part.startsWith("sec=")) {
                    resultMap.put("sec", Integer.valueOf(part.substring(4)));
                }
            }
        } catch (Exception e) {
            log.error("Error parsing remaining request header: {}", e.getMessage());
            e.printStackTrace();
        }

        return resultMap;
    }
}
