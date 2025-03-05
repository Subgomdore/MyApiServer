package com.myapp.apiserver.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class FilterRequestDTO {
    @JsonProperty("conditionType")  // JSON 필드명과 DTO 필드를 정확하게 매핑
    private String conditionType;

    @JsonProperty("filters")
    private List<FilterConditionDTO> filters;

    @JsonProperty
    private List<String> markets;

    @Override
    public String toString() {
        return "FilterRequestDTO{" +
                "conditionType='" + conditionType + '\'' +
                ", filters=" + filters +
                ", markets=" + markets +
                '}';
    }
}