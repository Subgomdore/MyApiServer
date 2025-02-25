package com.myapp.apiserver.component;

import com.myapp.apiserver.model.entity.VisitorLog;
import com.myapp.apiserver.repository.VisitorLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClientIpInterceptor implements HandlerInterceptor {

    private final VisitorLogRepository visitorLogRepository;

    private static final Set<String> EXCLUDED_PATHS = Set.of(
            "/api/sse/ping"
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String visitedPage = request.getRequestURI();

        // 특정 요청 제외
        if (EXCLUDED_PATHS.contains(visitedPage)) {
            return true; // 로그 저장 없이 요청 통과
        }

        // 🌟 메인 페이지(`/`) 방문 시에만 로그 기록
        if (!visitedPage.equals("/")) {
            return true;
        }

        // IP 주소 가져오기 (X-Forwarded-For 사용, 여러 개일 경우 첫 번째만 사용)
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress != null && !ipAddress.isEmpty()) {
            ipAddress = ipAddress.split(",")[0].trim();
        } else {
            ipAddress = request.getRemoteAddr();
        }

        String userAgent = request.getHeader("User-Agent") != null ? request.getHeader("User-Agent") : "Unknown";
        String referer = request.getHeader("Referer") != null ? request.getHeader("Referer") : "Direct Access";

        // 세션 ID 생성 (쿠키 기반 방문자 추적)
        String sessionId = (String) request.getSession().getAttribute("VISITOR_SESSION_ID");
        if (sessionId == null) {
            sessionId = UUID.randomUUID().toString();
            request.getSession().setAttribute("VISITOR_SESSION_ID", sessionId);
        }

        // 방문자 로그 객체 생성
        VisitorLog log = new VisitorLog();
        log.setIpAddress(ipAddress);
        log.setUserAgent(userAgent);
        log.setReferer(referer);
        log.setVisitedPage(visitedPage);
        log.setSessionId(sessionId);
        log.setVisitDate(LocalDate.now()); // 날짜 저장
        log.setVisitTime(LocalTime.now()); // 시간 저장

        // 로그 저장
        visitorLogRepository.save(log);

        return true;
    }
}
