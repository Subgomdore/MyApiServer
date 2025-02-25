package com.myapp.apiserver.controller;

import com.myapp.apiserver.websocket.UpbitWebSocketClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/sse/")
public class UpbitSseController {

    private final UpbitWebSocketClient webSocketClient;

    // SSE Emitter를 받아 데이터를 전송하는 기능을 수행하는 메서드
    private void accept(String newPriceData, SseEmitter emitter) {
        try {
            emitter.send(SseEmitter.event().name("price").data(newPriceData));
        } catch (IOException e) {
            log.error("Error sending SSE data", e);
            e.printStackTrace();
            emitter.completeWithError(e);
            webSocketClient.removeSseListener(data -> accept(data, emitter));
        }
    }

    @GetMapping("/ping")
    public ResponseEntity<Void> ping() {
        // ping 요청을 받았을 때 아무 작업도 하지 않음
        return ResponseEntity.ok().build();
    }

    @GetMapping("/price")
    public SseEmitter streamPrices() {
        SseEmitter emitter = new SseEmitter(60_000L); // 무제한 타임아웃
        ExecutorService executor = Executors.newSingleThreadExecutor();

        // SSE 리스너 등록
        Consumer<String> priceListener = newPriceData -> accept(newPriceData, emitter);
        webSocketClient.addSseListener(priceListener);

        // SSE 연결 종료 시 처리
        emitter.onCompletion(() -> {
            log.info("SSE connection completed.");
            webSocketClient.removeSseListener(priceListener);  // 리스너 제거
            executor.shutdown();
        });

        try {
            // 최초 연결 시 빈 이벤트 전송 → 클라이언트가 연결을 유지할 수 있도록 함
            emitter.send(SseEmitter.event().name("connected").data("CONNECTED"));
        } catch (IOException e) {
            emitter.complete();
        }

        emitter.onTimeout(() -> {
            log.info("SSE connection timed out.");
            emitter.complete();
            webSocketClient.removeSseListener(priceListener);  // 리스너 제거
            executor.shutdown();
        });

        emitter.onError(e -> {
            log.error("SSE connection error: ", e);
            emitter.completeWithError(e);
            webSocketClient.removeSseListener(priceListener);  // 리스너 제거
            executor.shutdown();
        });

        return emitter;
    }

}
