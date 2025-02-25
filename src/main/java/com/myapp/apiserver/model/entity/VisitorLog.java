package com.myapp.apiserver.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@Table(name = "visitor_logs")

public class VisitorLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ipAddress;
    private String userAgent;
    private String referer;
    private String visitedPage;
    private String sessionId;

    private LocalDate visitDate; // YYYY-MM-DD
    private LocalTime visitTime; // HH:MM:SS

    @PrePersist
    public void setVisitTimestamp() {
        this.visitDate = LocalDate.now();
        this.visitTime = LocalTime.now();
    }
}
