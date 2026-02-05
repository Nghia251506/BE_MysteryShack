package org.example.be_eproject_sem4.Controller;

import lombok.RequiredArgsConstructor;

import org.example.be_eproject_sem4.Dto.Admin.DashboardStatsDTO;
import org.example.be_eproject_sem4.Dto.Admin.SessionDashboardDTO;
import org.example.be_eproject_sem4.Entity.User;
import org.example.be_eproject_sem4.Service.Admin.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    // 1. API lấy các con số thống kê (Stats Cards)
    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsDTO> getDashboardStats() {
        return ResponseEntity.ok(dashboardService.getStats());
    }

    // 2. API lấy danh sách phiên gần đây (Initial load cho cái bảng)
    @GetMapping("/recent-sessions")
    public ResponseEntity<List<SessionDashboardDTO>> getSessions(
            @RequestParam(required = false) String search) {
        if (search != null && !search.trim().isEmpty()) {
            return ResponseEntity.ok(dashboardService.searchSessions(search));
        }
        return ResponseEntity.ok(dashboardService.getRecentSessions());
    }

    // 3. API lấy dữ liệu biểu đồ 24h
    @GetMapping("/hourly-chart")
    public ResponseEntity<List<?>> getHourlyChartData() {
        return ResponseEntity.ok(dashboardService.getHourlyChartData());
    }

    @GetMapping("/top-readers")
    public ResponseEntity<List<User>> getTopReaders() {
        return ResponseEntity.ok(dashboardService.getTopReaders());
    }
}