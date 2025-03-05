package com.myapp.apiserver.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FilterConditionDTO {
    @JsonProperty("indicator")
    private String indicator;

    @JsonProperty("operator")
    private String operator ;

    @JsonProperty("value")
    private Object value;

    @Override
    public String toString() {
        return "FilterConditionDTO{" +
                "id=" + indicator +
                ", type='" + operator + '\'' +
                ", value=" + value +
                '}';
    }
}
