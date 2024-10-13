package com.myapp.apiserver.UpbitUtill;

import com.myapp.apiserver.model.dto.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Log4j2
public class ApiClientUtil {

    private OkHttpClient client = new OkHttpClient();  // OkHttpClient 초기화

    // 공통적인 GET 요청 처리 메소드
    public ApiResponse executeGetRequest(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("accept", "application/json")  // "accept" 헤더 추가
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {

                // Body
                String responseBody = response.body().string();

                // Heaader
                Map<String, String> responseHeaders = new HashMap<>();
                for (String headerName : response.headers().names()) {
                    responseHeaders.put(headerName, response.header(headerName));
                }
                return new ApiResponse(responseBody, responseHeaders);
            } else {
                log.error("Request URL >> " + url);
                throw new IOException("Request failed: " + response.code());
            }
        }
    }
}

