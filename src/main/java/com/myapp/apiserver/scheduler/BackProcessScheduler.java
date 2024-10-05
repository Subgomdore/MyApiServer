package com.myapp.apiserver.scheduler;

import com.myapp.apiserver.service.ExternalUpbitService;
import com.myapp.apiserver.service.UpbitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Component
@Log4j2
@RequiredArgsConstructor // 생성자 자동 생성
public class BackProcessScheduler {

    private final UpbitService upbitService;
    private final ExternalUpbitService externalUpbitService;

    @Scheduled(cron = "0 0 9 * * ?")
    public void performTaskAtNineAm() {

        externalUpbitService.doGetUpbitCoinList();
        log.info("BackProcessScheduler >> UPBIT COIN LIST SYNC COMPLETE");

        externalUpbitService.doGetUpbitCoinPrice();
        log.info("BackProcessScheduler >> UPBIT COIN_PRICE SYNC COMPLETE");
    }

    // 5초마다 실행되는 작업
    @Scheduled(fixedRate = 5000)
    public void runScheduledTask() {

        log.info("Running background task with @Scheduled...");
    }
}

