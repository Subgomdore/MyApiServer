package com.myapp.apiserver.service;

import com.myapp.apiserver.UpbitUtill.UpbitAPI;
import com.myapp.apiserver.UpbitUtill.UpbitUtils;
import com.myapp.apiserver.model.dto.UpbitAllDataResponseDTO;
import com.myapp.apiserver.model.dto.UpbitCoinDTO;
import com.myapp.apiserver.model.dto.UpbitCoinDayPriceDTO;
import com.myapp.apiserver.model.entity.UpbitCoin;
import com.myapp.apiserver.model.entity.UpbitCoinDayPrice;
import com.myapp.apiserver.repository.UpbitCoinPriceRepository;
import com.myapp.apiserver.repository.UpbitRepository;
import jdk.jfr.Description;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class UpbitServiceImpl implements UpbitService {

    private final UpbitRepository upbitRepository;

    private final UpbitCoinPriceRepository upbitCoinPriceRepository;

    private final UpbitUtils upbitUtils;

    private final ModelMapper modelMapper;

    private final UpbitAPI upbitAPI;


    @Override
    @Description("코인 리스트만 가져오기")
    public List<UpbitCoinDTO> getAllCoinList() {
        List<UpbitCoin> upbitCoins = upbitRepository.findAll(Sort.by(Sort.Direction.ASC, "seq"));
        List<UpbitCoinDTO> upbitCoinDTOList = upbitCoins.stream().map(map -> entityToDTO(map)).collect(Collectors.toList());
        return upbitCoinDTOList;
    }

    @Override
    @Description("코인 리스트와 가격가져오기")
    public List<UpbitAllDataResponseDTO> getAllCoinAndPrice() {
        // 코인리스트 가져오기
        List<UpbitCoin> upbitCoins = upbitRepository.findAll(Sort.by(Sort.Direction.ASC, "seq"));
        List<UpbitCoinDTO> upbitCoinDTO = upbitCoins.stream()
                .map(e -> modelMapper.map(e, UpbitCoinDTO.class)).collect(Collectors.toList());

        // 초기 가격데이터 가져오기
        String targetDate = upbitUtils.getTargetDate();
        List<UpbitCoinDayPrice> upbitCoinDayPrices = upbitRepository.getTodayPriceList(targetDate);
        List<UpbitCoinDayPriceDTO> upbitCoinDayPriceDTOList = upbitCoinDayPrices.stream()
                .map(e -> modelMapper.map(e, UpbitCoinDayPriceDTO.class)).collect(Collectors.toList());

        // 거래데이터가 있는 코인리스트 데이터 가공
        Set<String> coinsWithPrices = upbitCoinDayPrices.stream()
                .map(UpbitCoinDayPrice::getMarket)
                .collect(Collectors.toSet());

        List<String> coinsWithOutPriceList = upbitCoinDTO.stream()
                .filter(coin -> !coinsWithPrices.contains(coin.getMarket()))
                .map(e -> e.getMarket()).toList();

        log.warn(coinsWithOutPriceList.size());

        int size = 20;
        List<Map<String, String>> currentPriceResult = new ArrayList<>();

        if (coinsWithOutPriceList != null && coinsWithOutPriceList.size() > 0) {
            // 실시간조회가 필요한 코인리스트가 있을경우
            for (int i = 0; i < coinsWithOutPriceList.size(); i += size) {
                List<String> paramList = coinsWithOutPriceList.subList(i, Math.min(i + 20, coinsWithOutPriceList.size()));
                
                // 20개씩 실시간 데이터 요청
                List<Map<String, Object>> currentPriceList = upbitAPI.getCurrentPriceByTicker(paramList);

                // 데이터가 있을경우 resutList 추가
                if (currentPriceList != null && currentPriceList.size() > 0) {
                    currentPriceResult.addAll(upbitUtils.convertPirceData(currentPriceList));
                }
            }

            List<UpbitCoinDayPrice> updateCoinPriceList = currentPriceResult.stream()
                    .map(priceMap -> {
                        UpbitCoinDayPrice upbitCoinDayPrice = modelMapper.map(priceMap, UpbitCoinDayPrice.class);
                        // candle_date가 null인 경우 오늘 날짜 추가 = PK 값임
                        if (upbitCoinDayPrice.getCandle_date_time_kst() == null) {
                            upbitCoinDayPrice.setCandle_date_time_kst(LocalDate.now().toString()); // 또는 LocalDateTime.now()로 시간까지 설정 가능
                        }
                        return upbitCoinDayPrice;
                    })
                    .collect(Collectors.toList());

            upbitCoinPriceRepository.saveAll(updateCoinPriceList);
        }

        for(Map<String, String> rowMap : currentPriceResult){
            upbitCoinDayPriceDTOList.add(modelMapper.map(rowMap, UpbitCoinDayPriceDTO.class));
        }

        List<UpbitAllDataResponseDTO> responseDTOList = new ArrayList<>();
        for (UpbitCoinDTO coinDTO : upbitCoinDTO) {
            // 코인의 마켓 정보에 맞는 가격 데이터를 찾는다.
            UpbitCoinDayPriceDTO priceDTO = upbitCoinDayPriceDTOList.stream()
                    .filter(p -> p.getMarket().equals(coinDTO.getMarket()))
                    .findFirst()
                    .orElse(null);

            // 두 DTO를 결합하여 새로운 DTO를 빌더로 생성
            UpbitAllDataResponseDTO responseDTO = UpbitAllDataResponseDTO.builder()
                    .market(coinDTO.getMarket())
                    .seq(coinDTO.getSeq())
                    .korean_name(coinDTO.getKorean_name())
                    .english_name(coinDTO.getEnglish_name())
                    .del_flag(coinDTO.getDel_flag())
                    .add_date(coinDTO.getAdd_date())
                    .change_date(coinDTO.getChange_date())

                    // 가격 정보는 priceDTO에서 가져옴
                    .candle_date_time_kst(priceDTO != null ? priceDTO.getCandle_date_time_kst() : null)
                    .candle_date_time_utc(priceDTO != null ? priceDTO.getCandle_date_time_utc() : null)
                    .opening_price(priceDTO != null ? priceDTO.getOpening_price() : null)
                    .high_price(priceDTO != null ? priceDTO.getHigh_price() : null)
                    .low_price(priceDTO != null ? priceDTO.getLow_price() : null)
                    .trade_price(priceDTO != null ? priceDTO.getTrade_price() : null)
                    .timestamp(priceDTO != null ? priceDTO.getTimestamp() : null)
                    .candle_acc_trade_price(priceDTO != null ? priceDTO.getCandle_acc_trade_price() : null)
                    .candle_acc_trade_volume(priceDTO != null ? priceDTO.getCandle_acc_trade_volume() : null)
                    .prev_closing_price(priceDTO != null ? priceDTO.getPrev_closing_price() : null)
                    .change_price(priceDTO != null ? priceDTO.getChange_price() : null)
                    .change_rate(priceDTO != null ? priceDTO.getChange_rate() : null)
                    .build();

            responseDTOList.add(responseDTO);
        }

        return responseDTOList;
    }

    @Override
    @Cacheable(value = "coinCache")
    public List<String> getAllCoinsWithCache() {
        return upbitRepository.findAllCoinCodes();
    }

}
