package com.myapp.apiserver.repository;

import com.myapp.apiserver.model.entity.UpbitCoin;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UpbitRepositoryCustomImpl implements UpbitRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<UpbitCoin> findByMarketPrefixes(List<String> prefixes) {
        // CriteriaBuilder와 CriteriaQuery 준비
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<UpbitCoin> query = cb.createQuery(UpbitCoin.class);
        Root<UpbitCoin> coin = query.from(UpbitCoin.class);

        // 접두어 목록에 따른 OR 조건을 구성
        Predicate predicate = cb.disjunction();
        for (String prefix : prefixes) {
            // 각 접두어에 대해, market 컬럼이 "prefix-%" 형태로 시작하는 조건을 추가
            predicate = cb.or(predicate, cb.like(coin.get("market"), prefix + "-%"));
        }
        query.select(coin).where(predicate);

        TypedQuery<UpbitCoin> typedQuery = entityManager.createQuery(query);
        return typedQuery.getResultList();
    }
}