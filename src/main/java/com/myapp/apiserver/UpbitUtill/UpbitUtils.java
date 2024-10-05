package com.myapp.apiserver.UpbitUtill;

import java.time.LocalDateTime;

public class UpbitUtils {

    // String을 LocalDateTime으로 변환
    public static LocalDateTime parseLocalDateTime(String dateTimeStr) {
        if (dateTimeStr.length() == 13) {
            dateTimeStr += ":00";  // '2024-10-03T09' 형식에 대해 분을 추가하여 '2024-10-03T09:00'로 변환
        }
        return LocalDateTime.parse(dateTimeStr);  // 기본 ISO-8601 형식으로 파싱
    }

    // Object를 Double로 변환
    public static Double parseDouble(Object value) {
        if (value instanceof String) {
            return Double.parseDouble((String) value);
        } else if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return null;
    }

    // Object를 Long으로 변환
    public static Long parseLong(Object value) {
        if (value instanceof String) {
            return Long.parseLong((String) value);
        } else if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return null;
    }
}