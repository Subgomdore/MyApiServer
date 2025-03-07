package com.myapp.apiserver.repository;

import com.myapp.apiserver.model.entity.UpbitCoin;
import com.myapp.apiserver.model.entity.UpbitCoinDayPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UpbitRepository extends JpaRepository<UpbitCoin, Long>, UpbitRepositoryCustom {

    // 모든 코인 코드만 조회하는 메서드
    @Query("SELECT market FROM UpbitCoin")
    List<String> findAllCoinCodes();

    @Query("SELECT u FROM UpbitCoinDayPrice u WHERE u.candle_date_time_kst = :date")
    List<UpbitCoinDayPrice> getTodayPriceList(@Param("date") String date);


    @Query(value = """
        SELECT udp.market, udp.candle_date_time_kst, udp.trade_price
        FROM upbit_day_price udp
        WHERE udp.candle_date_time_kst BETWEEN (CURRENT_DATE - INTERVAL :intervalValue DAY)
        AND (CURRENT_DATE - INTERVAL 1 DAY)
        ORDER BY udp.market ASC, udp.candle_date_time_kst DESC
        """, nativeQuery = true)
    List<Object[]> findDataWithinInterval(@Param("intervalValue") String intervalValue);
}
