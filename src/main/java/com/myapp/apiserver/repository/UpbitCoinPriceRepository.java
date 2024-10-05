package com.myapp.apiserver.repository;

import com.myapp.apiserver.domain.UpbitCoinPrice;
import com.myapp.apiserver.domain.UpbitCoinPriceId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UpbitCoinPriceRepository extends JpaRepository<UpbitCoinPrice, UpbitCoinPriceId> {

}
