package com.myapp.apiserver.model.dto;

import lombok.Data;

@Data
public class FilterRequestDTO {
    private String priceRange; // 가격 범위 (예: 500 이하)
    private String volume;     // 거래량 (예: 100 이상)
}