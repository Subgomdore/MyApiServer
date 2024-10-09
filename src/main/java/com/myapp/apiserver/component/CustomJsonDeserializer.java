package com.myapp.apiserver.component;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@Component
public class CustomJsonDeserializer {

    public List<Map<String, Object>> parseJsonArray(String jsonString) {
        List<Map<String, Object>> resultList = new ArrayList<>();

        jsonString = jsonString.trim();
        if (jsonString.startsWith("[")) {
            jsonString = jsonString.substring(1, jsonString.length() - 1);
        }

        String[] jsonObjects = jsonString.split("\\},\\{");
        for (String jsonObject : jsonObjects) {
            jsonObject = jsonObject.replaceAll("[{}]", "").trim();
            Map<String, Object> map = parseJsonObject(jsonObject);
            resultList.add(map);
        }
        return resultList;
    }

    private Map<String, Object> parseJsonObject(String jsonObject) {
        Map<String, Object> resultMap = new HashMap<>();

        String[] keyValuePairs = jsonObject.split(",");
        for (String pair : keyValuePairs) {
            String[] entry = pair.split(":");
            String key = entry[0].trim().replaceAll("\"", ""); // key는 항상 문자열
            String value = entry[1].trim().replaceAll("\"", ""); // value는 문자열 또는 숫자

            Object parsedValue = parseValue(value);
            resultMap.put(key, parsedValue);
        }

        return resultMap;
    }

    private Object parseValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return value;  // null이나 빈 문자열이면 그대로 반환
        }

        // 숫자인지 확인하고 BigDecimal로 변환
        try {
            BigDecimal decimalValue = new BigDecimal(value.trim());
            return decimalValue.toString();  // BigDecimal 값을 String으로 변환
        } catch (NumberFormatException e) {
            // 숫자로 변환할 수 없는 경우 그대로 문자열로 반환
            return value;
        }
    }

}