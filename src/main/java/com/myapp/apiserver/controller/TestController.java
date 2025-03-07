package com.myapp.apiserver.controller;

import com.myapp.apiserver.UpbitUtill.UpbitAPI;
import com.myapp.apiserver.service.ExternalUpbitService;
import jdk.jfr.Description;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Log4j2
@RequiredArgsConstructor
public class TestController {

    private final UpbitAPI upbitAPI;
    private final ExternalUpbitService externalUpbitService;

    @GetMapping("/test/dev")
    @Description("단일 호출테스트")
    public String dev(){
        //externalUpbitService.doGetUpbitCoinMinutePrice("200");
        return "END : SUCCESS";
    }

    @GetMapping("/test/dev2")
    @Description("단일 호출테스트")
    public void dev2(){
        //externalUpbitService.getUpbitTradeInfo();
    }
}
