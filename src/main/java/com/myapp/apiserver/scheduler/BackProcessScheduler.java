package com.myapp.apiserver.scheduler;

import com.myapp.apiserver.service.ExternalUpbitService;
import com.myapp.apiserver.service.UpbitService;
import jdk.jfr.Description;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@RequiredArgsConstructor 
public class BackProcessScheduler {

    private final ExternalUpbitService externalUpbitService;
    private final UpbitService upbitService;

    @Scheduled(cron = "0 0 9 * * ?")
    @Description("업비트 코인리스트 동기화 / 업비트 코인리스트 가격 동기화 :: 이전날짜의 정보들을 이관")
    public void performTaskAtNineAm() {

        externalUpbitService.doGetUpbitCoinList();
        log.warn("BackProcessScheduler >> UPBIT COIN LIST SYNC COMPLETE");

        externalUpbitService.doGetUpbitCoinPrice("200");
        log.warn("BackProcessScheduler >> UPBIT COIN_PRICE SYNC COMPLETE");
    }

    @Scheduled(cron = "0 10 9 * * ?")
    @Description("0초 10분 9시 DB동기화 이후 캐시재갱신")
    public void updateCoinCache() {
        upbitService.getAllCoinsWithCache();
    }

    @Scheduled(cron = "0 0/10 * * * ?")
    @Description("0초 정각/10분마다 모든시간 * * *")
    public void refreshPrice() {
        externalUpbitService.doGetUpbitCoinPrice("1");
        log.warn("BackProcessScheduler >> refreshPrice() COMPLETE");
    }
}

