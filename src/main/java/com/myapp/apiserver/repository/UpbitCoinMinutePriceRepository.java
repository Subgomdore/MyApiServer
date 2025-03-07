package com.myapp.apiserver.repository;

import com.myapp.apiserver.model.entity.UpbitCoinMinutePrice;
import com.myapp.apiserver.model.entity.UpbitCoinMinutePriceId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UpbitCoinMinutePriceRepository
        extends JpaRepository<UpbitCoinMinutePrice, UpbitCoinMinutePriceId>,
        JpaSpecificationExecutor<UpbitCoinMinutePrice> {
}
