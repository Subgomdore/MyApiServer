package com.myapp.apiserver.controller;

import com.myapp.apiserver.model.entity.VisitorLog;
import com.myapp.apiserver.repository.VisitorLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/visitors")
@RequiredArgsConstructor
public class VisitorLogController {

    private final VisitorLogRepository visitorLogRepository;

    @GetMapping("/today/count")
    public long getTodayVisitorCount() {
        LocalDate today = LocalDate.now();
        return visitorLogRepository.countTodayVisitors(today);
    }

    @GetMapping("/today")
    public List<VisitorLog> getTodayVisitors() {
        LocalDate today = LocalDate.now();
        return visitorLogRepository.findByVisitDate(today);
    }
}
