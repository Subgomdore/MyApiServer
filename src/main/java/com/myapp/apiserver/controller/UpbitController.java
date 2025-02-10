package com.myapp.apiserver.controller;

import com.myapp.apiserver.UpbitUtill.UpbitAPI;
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

@RestController
@Log4j2
@RequiredArgsConstructor
public class UpbitController {

    private final UpbitService upbitService;

    @GetMapping("/api/upbit/list")
    @Description("업비트 코인리스트")
    public List<UpbitCoinDTO> getAllCoinList() {
        List<UpbitCoinDTO> resDto = upbitService.getAllCoinList();
        return resDto;
    }

    @GetMapping("/api/upbit/priceList")
    @Description("업비트 코인리스트")
    public List<UpbitAllDataResponseDTO> getAllCoinAndPrice() {
        log.info("/priceList");
        return upbitService.getAllCoinAndPrice();
    }

    @PostMapping("/api/upbit/filterdata")
    @Description("업비트 필터 데이터 요청")
    public void filterCoinData(@RequestBody FilterRequestDTO filterRequest) {
        log.info("Received filter request: {}", filterRequest);
        String priceRange = filterRequest.getPriceRange();
        String volume = filterRequest.getVolume();

        upbitService.findFilterCoinList(priceRange, volume);


        // 필터링 로직 처리 (서비스 호출)
        //List<UpbitAllDataResponseDTO> filteredData = upbitService.filterCoinData(filterRequest);

        //log.info("Returning filtered data: {}", filteredData);

    }
}





//fetchPriceAndSync
// Ackey : bAHvpGQ7rJR8PxsF0FHuxtaUzZY9B6da80epyPYh
// Skey : IqzUE8gqb1ZGCNR80niVSDACgEVXyD7THoKkJQNt