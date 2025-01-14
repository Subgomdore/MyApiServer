package com.myapp.apiserver.model.dto.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CandleDayRequestDTO {

    @NotBlank(message = "Market code is required.")
    private String market;  // 마켓 코드 (ex. KRW-BTC)

    @Pattern(
            regexp = "^\\d{4}-\\d{2}-\\d{2}[T\\s]\\d{2}:\\d{2}:\\d{2}([+-]\\d{2}:\\d{2})?$",
            message = "Invalid 'to' format. It should follow ISO8601 format (yyyy-MM-dd'T'HH:mm:ss'Z' or yyyy-MM-dd HH:mm:ss)."
    )
    private String to;  // 마지막 캔들 시각 ISO 8061 포맷

    @Positive(message = "Count must be a positive integer.")
    private String count = "200";  // 캔들 개수 최대 200개까지 요청 가능

    private String convertingPriceUnit = "KRW";  // 종가 환산 화폐 단위 생략 가능
}
