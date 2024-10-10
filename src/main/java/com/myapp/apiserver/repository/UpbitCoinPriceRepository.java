package com.myapp.apiserver.repository;

import com.myapp.apiserver.model.entity.UpbitCoinPrice;
import com.myapp.apiserver.model.entity.UpbitCoinPriceId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UpbitCoinPriceRepository extends JpaRepository<UpbitCoinPrice, UpbitCoinPriceId> {

}
