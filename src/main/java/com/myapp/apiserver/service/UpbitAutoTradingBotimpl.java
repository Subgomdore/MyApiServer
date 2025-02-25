package com.myapp.apiserver.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Log4j2
@RequiredArgsConstructor
public class UpbitAutoTradingBotimpl implements UpbitAutoTradingBot {

    @Override
    public void autoTradingBot() {
        LocalDateTime now = LocalDateTime.now();

        // 포맷 지정 (예: "yyyy-MM-dd HH:mm:ss")
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // 포맷 적용하여 출력
        String formattedNow = now.format(formatter);

        System.out.println("AutoTradingBot Run -- " +formattedNow);

    }
}
