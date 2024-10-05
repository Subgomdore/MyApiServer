package com.myapp.apiserver.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "upbit_coin")
public class UpbitCoin {

    @Id
    @Comment("코인 마켓 (예: KRW-BTC)")
    private String market;  // 코인 마켓

    @Comment("코인의 고유 시퀀스")
    private Long seq;  // 시퀀스

    @Comment("코인의 한글 이름")
    private String korean_name;  // 코인의 한글 이름

    @Comment("코인의 영어 이름")
    private String english_name;  // 코인의 영어 이름

    @Column(nullable = false, updatable = false)
    @Comment("삭제 여부 플래그 (기본값 'N')")
    private String del_flag;  // 삭제 여부

    @Column(nullable = false, updatable = false)
    @Comment("추가된 날짜")
    private String add_date;  // 추가된 날짜

    @Comment("변경된 날짜")
    private String change_date;  // 변경된 날짜

    @PrePersist
    public void prePersist() {
        // DEL_FLAG 기본값 설정
        if (this.del_flag == null) {
            this.del_flag = "N";
        }

        // add_date를 현재 날짜로 설정
        this.add_date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    // 업데이트 시 change_date를 자동으로 설정
    @PreUpdate
    public void preUpdate() {
        this.change_date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
