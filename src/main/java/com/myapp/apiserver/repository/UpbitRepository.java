package com.myapp.apiserver.repository;

import com.myapp.apiserver.domain.UpbitCoin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UpbitRepository extends JpaRepository<UpbitCoin, Long> {

}
