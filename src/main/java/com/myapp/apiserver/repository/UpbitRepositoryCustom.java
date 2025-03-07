package com.myapp.apiserver.repository;

import com.myapp.apiserver.model.entity.UpbitCoin;
import java.util.List;

public interface UpbitRepositoryCustom {
    /**
     * 전달받은 접두어(prefixes) 목록에 해당하는 코인 정보를 조회합니다.
     * 각 접두어에 대해, market이 "prefix-%" 형태로 시작하는 데이터를 찾습니다.
     *
     * @param prefixes 코인 접두어 목록 (예: ["KRW", "BTC"])
     * @return 조건에 맞는 UpbitCoin 리스트
     */
    List<UpbitCoin> findByMarketPrefixes(List<String> prefixes);
}