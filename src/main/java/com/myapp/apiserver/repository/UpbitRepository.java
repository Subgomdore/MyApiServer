package com.myapp.apiserver.repository;

import com.myapp.apiserver.model.entity.UpbitCoin;
import com.myapp.apiserver.model.entity.UpbitCoinPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UpbitRepository extends JpaRepository<UpbitCoin, Long> {

    // 모든 코인 코드만 조회하는 메서드
    @Query("SELECT market FROM UpbitCoin")
    List<String> findAllCoinCodes();

    @Query("SELECT u FROM UpbitCoinPrice u WHERE u.candle_date_time_kst = :date")
    List<UpbitCoinPrice> getTodayPriceList(@Param("date") String date);
}
