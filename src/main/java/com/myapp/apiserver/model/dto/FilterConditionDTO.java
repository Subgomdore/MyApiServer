package com.myapp.apiserver.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FilterConditionDTO {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("type")
    private String type;

    @JsonProperty("value")
    private Object value;

    @Override
    public String toString() {
        return "FilterConditionDTO{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", value=" + value +
                '}';
    }
}
