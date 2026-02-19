package org.example.be_eproject_sem4.Controller;

import org.example.be_eproject_sem4.Dto.Chart.DashboardAnalyticsDTO;
import org.example.be_eproject_sem4.Dto.ReaderStatsDTO;
import org.example.be_eproject_sem4.Service.ReaderStatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reader/statistics")
@RequiredArgsConstructor
public class ReaderStatsController {

    private final ReaderStatsService statsService;

    @GetMapping("/dashboard")
    public ResponseEntity<ReaderStatsDTO> getDashboardStats() {
        return ResponseEntity.ok(statsService.getDashboardStats());
    }

    @GetMapping("/dashboard-analytics")
    public ResponseEntity<DashboardAnalyticsDTO> getDashboardAnalytics() {
        return ResponseEntity.ok(statsService.getFullAnalytics());
    }
}
