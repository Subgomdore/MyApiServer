package com.myapp.apiserver.controller;

import org.springframework.web.bind.annotation.GetMapping;

public class ViewController {

    @GetMapping("/{path:[^\\.]*}")  // 정적 파일이 아닌 모든 경로 처리
    public String redirect() {
        return "forward:/index.html";
    }
}
