package com.myapp.apiserver.controller;

import org.springframework.web.bind.annotation.GetMapping;

public class ViewController {

    @GetMapping("/{path:[^\\.]*}")  // 정적 파일이 아닌 모든 경로 처리
    public String redirect() {
        return "forward:/index.html";
    }
    
    // 같은도메인을 사용하지만 내부 프록시설정으로 3000번과 8080이 모두 작동중임
    // 주소에 8080 을 호출하게되면 서버를 호출하게되면서 잘못된 요청과 응답이오고, 팝업창이 열리지 않는 이슈로 추가함
}
