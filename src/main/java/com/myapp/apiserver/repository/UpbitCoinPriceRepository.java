package com.myapp.apiserver.repository;

import com.myapp.apiserver.model.entity.UpbitCoinDayPrice;
import com.myapp.apiserver.model.entity.UpbitCoinDayPriceId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UpbitCoinPriceRepository extends JpaRepository<UpbitCoinDayPrice, UpbitCoinDayPriceId> {

}
