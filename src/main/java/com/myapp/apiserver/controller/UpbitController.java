package com.myapp.apiserver.controller;

import com.myapp.apiserver.model.dto.UpbitAllDataResponseDTO;
import com.myapp.apiserver.model.dto.UpbitCoinDTO;
import com.myapp.apiserver.service.UpbitService;
import jdk.jfr.Description;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/upbit")
public class UpbitController {

    private final UpbitService upbitService;

    @GetMapping("/list")
    @Description("업비트 코인리스트")
    public List<UpbitCoinDTO> getAllCoinList() {
        List<UpbitCoinDTO> resDto = upbitService.getAllCoinList();
        return resDto;
    }

//    @GetMapping("/fetchAndSync")
//    @Description("업비트 코인리스트 동기화")
//    public void doRegisterAllCoinList() {
//        Map<String, String> result = upbitService.fetchAndSyncFromUpbit();
//    }
//
//    @GetMapping("/pricesync")
//    @Description("업비트 코인 가격리스트 동기화")
//    public void fetchPriceAndSync() {
//        Map<String, String> result = upbitService.fetchPriceAndSyncFromUpbit();
//    }


    //fetchPriceAndSync
    // Ackey : bAHvpGQ7rJR8PxsF0FHuxtaUzZY9B6da80epyPYh
    // Skey : IqzUE8gqb1ZGCNR80niVSDACgEVXyD7THoKkJQNt
}
