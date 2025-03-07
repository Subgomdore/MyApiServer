package com.myapp.apiserver.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClientIpInterceptor implements HandlerInterceptor {

    private static final Set<String> EXCLUDED_PATHS = Set.of(
            "/api/sse/ping"
    );
}
