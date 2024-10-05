package com.myapp.apiserver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpbitCoinDTO {

    private String market;

    private Long seq;

    private String korean_name;

    private String english_name;

    private String del_flag;

    private String add_date;

    private String change_date;
}
