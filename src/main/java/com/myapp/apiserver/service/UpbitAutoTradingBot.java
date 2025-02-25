package com.myapp.apiserver.service;

import jakarta.transaction.Transactional;

@Transactional
public interface UpbitAutoTradingBot {

    void autoTradingBot();


}
