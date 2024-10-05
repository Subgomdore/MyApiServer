package com.myapp.apiserver.repository;

import com.myapp.apiserver.model.entity.UpbitCoin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UpbitRepository extends JpaRepository<UpbitCoin, Long> {

}
