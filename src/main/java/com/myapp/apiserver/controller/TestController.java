package com.myapp.apiserver.controller;

import com.myapp.apiserver.UpbitUtill.UpbitAPI;
import jdk.jfr.Description;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/upbit")
public class TestController {

    private final UpbitAPI upbitAPI;

    @GetMapping("/mytest")
    @Description("단일 호출테스트")
    public List<Map<String, Object>> test(){
        // http://localhost:8080/api/upbit/mytest
        return upbitAPI.getCurrentPriceByTicker(new ArrayList<>(List.of("KRW-BTC", "KRW-ETH")));
    }
}
