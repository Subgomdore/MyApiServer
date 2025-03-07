package com.myapp.apiserver.repository;

import com.myapp.apiserver.model.entity.UpbitCoinDayPrice;
import com.myapp.apiserver.model.entity.UpbitCoinDayPriceId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UpbitCoinDayPriceRepository extends JpaRepository<UpbitCoinDayPrice, UpbitCoinDayPriceId>
        , JpaSpecificationExecutor<UpbitCoinDayPrice> {

}
