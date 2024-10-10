package com.myapp.apiserver.UpbitUtill;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.myapp.apiserver.component.CustomJsonDeserializer;
import jdk.jfr.Description;
import lombok.extern.log4j.Log4j2;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Log4j2
@Component
public class UpbitAPI {

    @Description("전체코인목록조회 및 등록")
    public List<Map<String, Object>> doRegisterAllCoinList() {
        Response response = null;
        List<Map<String, Object>> coinListMap = null;

        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://api.upbit.com/v1/market/all?isDetails=false")
                    .get()
                    .addHeader("accept", "application/json")
                    .build();
            response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                coinListMap = new Gson().fromJson(responseBody, List.class);
            } else {
                log.error("Error: " + response.code());
            }
        } catch (Exception e) {
            log.error(e);
            e.printStackTrace();

        } finally {
            if (response != null) {
                response.close();
            }

            return coinListMap;
        }
    }

    @Description("코인가격 동기화")
    public List<Map<String, Object>> doFetchPriceAndSync(Map<String, String> paramsMap) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Response response = null;
        List<Map<String, Object>> coinPriceList = new ArrayList<>();
        String remainingReq = "";
        String url = "";
        int min = 0;
        int sec = 0;


        try {
            // 필수 매개변수
            if (paramsMap.get("market").isEmpty()) {
                Logger.errInfo();
                throw new Exception("The request parameters are invalid.");
            }

            String market = paramsMap.get("market"); // 마켓 코드
            String to = MapUtils.getString(paramsMap, "to",
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "T09:00:00");
            String count = MapUtils.getString(paramsMap, "count", "200");  // count가 없으면 기본값 "200"
            String convertingPriceUnit = MapUtils.getString(paramsMap, "convertingPriceUnit", "KRW"); // 종가 환산 화폐 단위, 생략 가능

            boolean reqFlag = true;

            while (reqFlag) {
                // URL 구성
                url = String.format("https://api.upbit.com/v1/candles/days?market=%s&to=%s&count=%s&convertingPriceUnit=%s",
                        market, to, count, convertingPriceUnit);

                // 요청 생성
                Request request = new Request.Builder()
                        .url(url)
                        .get()
                        .addHeader("accept", "application/json")
                        .build();

                response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    CustomJsonDeserializer customJson = new CustomJsonDeserializer();

                    // 남은 요청횟수 계산
                    remainingReq = response.header("Remaining-Req");
                    Map<String, Integer> remainingReqCount = calculateRemainingCount(remainingReq);
                    if (!remainingReqCount.isEmpty()) {
                        min = remainingReqCount.get("min");
                        sec = remainingReqCount.get("sec");
                    }

                    // STATUS 200 이지만 ResponseData가 없을경우 => 너무 예전기간 조회
                    JsonElement jsonElement = JsonParser.parseString(responseBody);
                    if (jsonElement.isJsonArray()) {
                        JsonArray jsonArray = jsonElement.getAsJsonArray();
                        if (jsonArray.size() == 0) {
                            reqFlag = false;
                        } else {
                            coinPriceList.addAll(customJson.parseJsonArray(responseBody));
                            String changeTo = (String) coinPriceList.get(coinPriceList.size() - 1).get("candle_date_time_kst") + ":00:00";
                            if (to.equals(changeTo)) {
                                reqFlag = false;
                            } else {
                                to = changeTo;
                            }
                        }
                    }
                } else {
                    String err = "fetchPriceAndSync Request failed >> " + response.code();
                    throw new Exception(err);
                }

                if (min < 1 || sec < 1){
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
            if (response != null) {
                response.close();
            }

            return coinPriceList;
        }
    }

    private Map<String, Integer> calculateRemainingCount(String remainingReq) {
        Map<String, Integer> resultMap = new HashMap<>();
        try {
            if (remainingReq  == null && remainingReq.isBlank()) {
                throw new Exception("Response header data is null! Please check the parameters.");
            }

            String[] parts = remainingReq.split(";");

            String minValue = "";
            String secValue = "";

            for (String part : parts) {
                part = part.trim();
                if (part.startsWith("min=")) { // Array가 순서보장이 안되기때문에 조건문으로 값을 구한다.
                    minValue = part.substring(4);
                } else if (part.startsWith("sec=")) {
                    secValue = part.substring(4);
                }
            }

            resultMap.put("min", Integer.valueOf(minValue));
            resultMap.put("sec", Integer.valueOf(secValue));

            return resultMap;
        } catch (Exception e) {
            log.error(e);
            e.printStackTrace();
            return null;
        }
    }
}
