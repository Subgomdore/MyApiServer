package com.myapp.apiserver.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor

@Table(name = "ubpit_coin")
public class UpbitCoin {

    @Id
    private String market;

    private Long seq;

    private String korean_name;

    private String english_name;

    @Column(nullable = false, updatable = false)
    private String del_flag;

    @Column(nullable = false, updatable = false)
    private String add_date;

    private String change_date;

    @PrePersist
    public void prePersist() {
        // DEL_FLAG 기본값 설정
        if (this.del_flag == null) {
            this.del_flag = "N";
        }

        // add_date를 현재 날짜로 설정
        this.add_date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    // 필요한 경우 업데이트 시 change_date도 자동 설정
    @PreUpdate
    public void preUpdate() {

        this.change_date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

}


