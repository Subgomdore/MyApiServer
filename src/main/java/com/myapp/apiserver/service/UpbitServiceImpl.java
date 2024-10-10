package com.myapp.apiserver.service;

import com.myapp.apiserver.model.dto.UpbitAllDataResponseDTO;
import com.myapp.apiserver.model.dto.UpbitCoinDTO;
import com.myapp.apiserver.model.dto.UpbitCoinPriceDTO;
import com.myapp.apiserver.model.entity.UpbitCoin;
import com.myapp.apiserver.model.entity.UpbitCoinPrice;
import com.myapp.apiserver.repository.UpbitCoinPriceRepository;
import com.myapp.apiserver.repository.UpbitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class UpbitServiceImpl implements UpbitService {

    private final UpbitRepository upbitRepository;

    private final UpbitCoinPriceRepository upbitCoinPriceRepository;

    private final ModelMapper modelMapper;

    @Override
    public List<UpbitCoinDTO> getAllCoinList() {
        List<UpbitCoin> upbitCoins = upbitRepository.findAll(Sort.by(Sort.Direction.ASC, "seq"));
        List<UpbitCoinDTO> upbitCoinDTOList = upbitCoins.stream().map(map -> entityToDTO(map)).collect(Collectors.toList());
        return upbitCoinDTOList;
    }

    @Override
    public Map<String, Object> getAllCoinAndPrice() {
        List<UpbitCoin> upbitCoins = upbitRepository.findAll(Sort.by(Sort.Direction.ASC, "seq"));

        List<UpbitCoinDTO> upbitCoinDTO = upbitCoins.stream()
                .map(e -> modelMapper.map(e, UpbitCoinDTO.class)).collect(Collectors.toList());

        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        // 업비트 거래종료 오전9시: 비교일 생성
        LocalTime upbitTime = LocalTime.of(9, 0);

        // 오전9시 이전일경우 (00 ~ 08:59)
        if (currentTime.isBefore(upbitTime)) {
            // 오늘날짜 -1일이 조회타겟대상
            currentDate= currentDate.minusDays(1);
        }

        String date = currentDate.toString();

        List<UpbitCoinPrice> upbitCoinPrices = upbitRepository.getTodayPriceList(date);
        List<UpbitCoinPriceDTO> upbitCoinPriceDTO = upbitCoinPrices.stream()
                .map(e -> modelMapper.map(e, UpbitCoinPriceDTO.class)).collect(Collectors.toList());

        Map<String, Object> resMap = new HashMap<>();
        resMap.put("COIN", upbitCoinDTO);
        resMap.put("PRICE", upbitCoinPriceDTO);

        return resMap;
    }


    @Override
    @Cacheable(value = "coinCache")
    public List<String> getAllCoinsWithCache() {
        return upbitRepository.findAllCoinCodes();
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
