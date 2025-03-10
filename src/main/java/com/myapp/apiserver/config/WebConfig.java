package com.myapp.apiserver.config;

import com.myapp.apiserver.component.ClientIpInterceptor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.*;

import java.util.concurrent.TimeUnit;

@Configuration
@Log4j2
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private ClientIpInterceptor clientIpInterceptor;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .maxAge(500)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS")
                .allowedOrigins("http://localhost:3000", "http://localhost:8080", "http://59.16.228.93:18080", "http://joy2islab.ddns.net/")
                .allowCredentials(true);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(clientIpInterceptor)
                .addPathPatterns("/**");  // 모든 요청에 대해 인터셉터 실행
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // React 라우트 요청이 들어오면 index.html을 반환하도록 설정
        registry.addViewController("/filter-popup").setViewName("forward:/index.html");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 정적 리소스에 대해 캐시 제어 헤더를 추가 (예: 1시간 동안 캐시)
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .setCacheControl(CacheControl.maxAge(1, TimeUnit.HOURS).cachePublic());
    }


}
