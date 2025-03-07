package com.myapp.apiserver.repository;

import com.myapp.apiserver.model.entity.UpbitCoinDayPrice;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class UpbitCoinDayPriceSpecification {

    // candle_date_time_kst 필드가 startDate와 endDate 사이인 조건
    public static Specification<UpbitCoinDayPrice> candleDateBetween(String startDate, String endDate) {
        return (Root<UpbitCoinDayPrice> root, CriteriaQuery<?> query, CriteriaBuilder cb) ->
                cb.between(root.get("candle_date_time_kst"), startDate, endDate);
    }

    public static Specification<UpbitCoinDayPrice> marketStartsWithAny(java.util.List<String> prefixes) {
        return (Root<UpbitCoinDayPrice> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            if (prefixes == null || prefixes.isEmpty()) {
                return cb.conjunction(); // 조건 없음: 전체 조회
            }
            java.util.List<Predicate> predicates = new java.util.ArrayList<>();
            for (String prefix : prefixes) {
                // "market" 필드가 "prefix-"로 시작하는 조건 추가
                predicates.add(cb.like(root.get("market"), prefix + "-%"));
            }
            return cb.or(predicates.toArray(new Predicate[0]));
        };
    }
}
