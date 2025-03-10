package com.myapp.apiserver.controller;

import com.myapp.apiserver.model.dto.FilterRequestDTO;
import com.myapp.apiserver.model.dto.UpbitAllDataResponseDTO;
import com.myapp.apiserver.model.dto.UpbitCoinDTO;
import com.myapp.apiserver.service.UpbitService;
import jdk.jfr.Description;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@Log4j2
@RequiredArgsConstructor
public class UpbitController {

    private final UpbitService upbitService;

    @GetMapping("/api/upbit/mklist")
    @Description("업비트 코인리스트")
    public List<UpbitCoinDTO> getAllCoinList() {
        return upbitService.getAllCoinList();
    }

    @GetMapping("/api/upbit/priceList")
    @Description("업비트 가격리스트")
    public List<UpbitAllDataResponseDTO> getAllCoinAndPrice() {
        return upbitService.getAllCoinAndPrice();
    }

    @PostMapping("/api/upbit/filterData")
    @Description("업비트 필터 데이터 요청")
    public List<Map<String, String>> filterCoinData(@RequestBody FilterRequestDTO filterRequest) {
        return upbitService.findFilterCoinList(filterRequest.getConditionType(), filterRequest);
    }
}


//fetchPriceAndSync
// Ackey : bAHvpGQ7rJR8PxsF0FHuxtaUzZY9B6da80epyPYh
// Skey : IqzUE8gqb1ZGCNR80niVSDACgEVXyD7THoKkJQNt