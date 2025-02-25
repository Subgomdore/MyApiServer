package com.myapp.apiserver.repository;

import com.myapp.apiserver.model.entity.VisitorLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface VisitorLogRepository extends JpaRepository<VisitorLog, Long> {

    @Query("SELECT COUNT(DISTINCT v.sessionId) FROM VisitorLog v WHERE v.visitDate = :today")
    long countTodayVisitors(@Param("today") LocalDate today);

    List<VisitorLog> findByVisitDate(LocalDate visitDate);
}
