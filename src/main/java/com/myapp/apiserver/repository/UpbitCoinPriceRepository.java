package com.myapp.apiserver.repository;

import com.myapp.apiserver.model.entity.UpbitCoinPrice;
import com.myapp.apiserver.model.entity.UpbitCoinPriceId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UpbitCoinPriceRepository extends JpaRepository<UpbitCoinPrice, UpbitCoinPriceId> {

    List<UpbitCoinPrice> findByMarket(String market);


}
