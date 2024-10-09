package com.myapp.apiserver.websocket;

import com.google.gson.Gson;
import com.myapp.apiserver.service.UpbitService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import okhttp3.*;
import okio.ByteString;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;

@Component
@Log4j2
@RequiredArgsConstructor
public class UpbitWebSocketClient {

    private final OkHttpClient client = new OkHttpClient();
    private WebSocket webSocket;
    private final List<Consumer<String>> sseListeners = Collections.synchronizedList(new ArrayList<>());
    private final UpbitService upbitService;

    // 애플리케이션 시작 시 WebSocket 연결을 유지
    @PostConstruct
    public void startWebSocket() {
        // WebSocket URL 설정
        Request request = new Request.Builder().url("wss://api.upbit.com/websocket/v1").build();
        webSocket = client.newWebSocket(request, new UpbitWebSocketListener());
    }

    // SSE로 데이터를 보내기 위한 리스너 추가
    public void addSseListener(Consumer<String> sseListener) {
        sseListeners.add(sseListener);
    }

    // SSE 연결이 끊어졌을 때 리스너 제거
    public void removeSseListener(Consumer<String> sseListener) {
        sseListeners.remove(sseListener);
    }

    private String createSubscriptionMessage(List<String> coinCodes) {
        List<Map<String, Object>> jsonObj = new ArrayList<>();

        // 티켓 정보 추가
        Map<String, Object> ticketMap = new HashMap<>();
        ticketMap.put("ticket", UUID.randomUUID().toString());
        jsonObj.add(ticketMap);

        // 구독 타입과 코인 코드 리스트 추가
        Map<String, Object> typeAndCode = new HashMap<>();
        typeAndCode.put("type", "ticker");
        typeAndCode.put("codes", coinCodes);
        jsonObj.add(typeAndCode);

        // JSON 형식으로 변환
        return new Gson().toJson(jsonObj);
    }

    private class UpbitWebSocketListener extends WebSocketListener {
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            log.info("WebSocket connection opened: " + response.message());

            List<String> coinCodes = upbitService.getAllCoinsWithCache();
            String subscriptionMessage = createSubscriptionMessage(coinCodes);

            // 구독 메시지 전송
            webSocket.send(subscriptionMessage);
        }

        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            String message = bytes.string(StandardCharsets.UTF_8);
            // SSE 연결이 있는 경우에만 데이터를 전송
            synchronized (sseListeners) {
                Iterator<Consumer<String>> iterator = sseListeners.iterator();
                while (iterator.hasNext()) {
                    Consumer<String> sseListener = iterator.next();
                    try {
                        sseListener.accept(message);
                    } catch (Exception e) {
                        log.error("Failed to send message to SSE listener", e);
                        iterator.remove();  // 실패한 리스너는 제거
                    }
                }
            }
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            log.error("[CUSTOM_LOG] >> WebSocket error", t);
        }

    }
}
