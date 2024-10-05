package com.myapp.apiserver.service;

import com.myapp.apiserver.UpbitUtill.UpbitAPI;
import com.myapp.apiserver.model.dto.UpbitAllDataResponseDTO;
import com.myapp.apiserver.model.dto.UpbitCoinDTO;
import com.myapp.apiserver.model.entity.UpbitCoin;
import com.myapp.apiserver.model.entity.UpbitCoinPrice;
import com.myapp.apiserver.repository.UpbitCoinPriceRepository;
import com.myapp.apiserver.repository.UpbitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class UpbitServiceImpl implements UpbitService {

    private final UpbitRepository upbitRepository;

    private final UpbitCoinPriceRepository upbitCoinPriceRepository;

    @Override
    public List<UpbitCoinDTO> getALlCoinList() {
        List<UpbitCoin> upbitCoins = upbitRepository.findAll(Sort.by(Sort.Direction.ASC, "seq"));
        List<UpbitCoinDTO> upbitCoinDTOList = upbitCoins.stream().map(map -> entityToDTO(map)).collect(Collectors.toList());
        return upbitCoinDTOList;
    }

    @Override
    public List<UpbitAllDataResponseDTO> getAllCoinAndPriceList() {
        // 1. 모든 코인 정보 한 번에 가져오기
        List<UpbitCoin> upbitCoins = upbitRepository.findAll(Sort.by(Sort.Direction.ASC, "seq"));

        // 2. 모든 가격 정보 한 번에 가져오기
        List<UpbitCoinPrice> upbitCoinPrices = upbitCoinPriceRepository.findAll();  // 필요시 필터링

        // 3. 가격 정보를 market 기준으로 매핑
        Map<String, UpbitCoinPrice> priceMap = upbitCoinPrices.stream()
                .collect(Collectors.toMap(
                        price -> price.getMarket() + "_" + price.getCandle_date_time_kst(),  // 복합 키 (market + kst날짜)
                        price -> price,  // 값을 그대로 사용
                        (existing, replacement) -> existing  // 중복 시 기존 값 사용
                ));


        // 4. 코인 정보와 가격 정보를 매핑하여 DTO로 변환
        return upbitCoins.stream().map(coin -> {
            UpbitCoinPrice coinPrice = priceMap.get(coin.getMarket());  // market 기준으로 가격 정보 찾기
            return entityToDTO(coin, coinPrice);  // DTO 변환
        }).collect(Collectors.toList());
    }
}
