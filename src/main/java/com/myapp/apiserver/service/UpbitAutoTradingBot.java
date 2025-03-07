package com.myapp.apiserver.service;

import jakarta.transaction.Transactional;

import java.util.List;

@Transactional
public interface UpbitAutoTradingBot {

    void startMonitoring();

    void stopMonitoring();

}
