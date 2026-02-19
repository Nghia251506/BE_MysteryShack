package org.example.be_eproject_sem4.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.example.be_eproject_sem4.Dto.Chart.DashboardAnalyticsDTO;
import org.example.be_eproject_sem4.Dto.Chart.IncomeChartInterface;
import org.example.be_eproject_sem4.Dto.Chart.PerformancePoint;
import org.example.be_eproject_sem4.Dto.ReaderStatsDTO;
import org.example.be_eproject_sem4.Entity.User;
import org.example.be_eproject_sem4.Repository.ReadingSessionRepository;
import org.example.be_eproject_sem4.Repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReaderStatsService {

    private final ReadingSessionRepository sessionRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public ReaderStatsDTO getDashboardStats() {
        // 1. Lấy User từ SecurityContext
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User reader = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Reader not found"));

        // 2. Lấy dữ liệu từ Repo (Xử lý null để tránh lỗi ở FE)
        BigDecimal today = sessionRepository.sumTodayIncome(reader);
        BigDecimal month = sessionRepository.sumMonthIncome(reader);
        BigDecimal total = sessionRepository.sumTotalIncome(reader);
        Long sessions = sessionRepository.countCompletedSessionsByReader(reader, "COMPLETED");

        // 3. Đóng gói vào DTO
        return new ReaderStatsDTO(
                today != null ? today : BigDecimal.ZERO,
                month != null ? month : BigDecimal.ZERO,
                total != null ? total : BigDecimal.ZERO,
                sessions != null ? sessions : 0L
        );
    }

    @Transactional(readOnly = true)
    public DashboardAnalyticsDTO getFullAnalytics() {
        // 1. Lấy Reader đang đăng nhập
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User reader = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Reader"));

        // 2. Lấy 4 con số thống kê chính
        BigDecimal today = sessionRepository.sumTodayIncome(reader);
        BigDecimal month = sessionRepository.sumMonthIncome(reader);
        BigDecimal total = sessionRepository.sumTotalIncome(reader);
        Long sessions = sessionRepository.countCompletedSessionsByReader(reader, "COMPLETED");

        // 3. Lấy dữ liệu biểu đồ thu nhập 7 ngày (Dùng Interface)
        Instant sevenDaysAgo = Instant.now().minus(7, ChronoUnit.DAYS);
        List<IncomeChartInterface> rawIncome = sessionRepository.getIncomeStatsLast7Days(reader, sevenDaysAgo);

        // BIẾN ĐỔI: Chuyển Interface Proxy sang Map đơn giản để sạch JSON
        List<Map<String, Object>> incomeChart = rawIncome.stream().map(item -> {
            Map<String, Object> map = new HashMap<>();
            map.put("label", item.getLabel());
            map.put("value", item.getValue());
            return map;
        }).collect(Collectors.toList());

        // 4. Lấy dữ liệu biểu đồ hiệu suất (Gộp "Khác")
        List<Object[]> statusCounts = sessionRepository.countSessionsByStatus(reader);
        Map<String, PerformancePoint> performanceMap = new HashMap<>();

        for (Object[] row : statusCounts) {
            String status = (row[0] != null) ? row[0].toString() : "UNKNOWN";
            Long count = (row[1] != null) ? ((Number) row[1]).longValue() : 0L;

            String displayName;
            String color;

            switch (status) {
                case "COMPLETED":
                    displayName = "Hoàn thành";
                    color = "#f59e0b";
                    break;
                case "CANCELLED":
                    displayName = "Đã hủy";
                    color = "#ef4444";
                    break;
                case "PROCESSING":
                    displayName = "Đang chạy";
                    color = "#3b82f6";
                    break;
                default:
                    displayName = "Khác";
                    color = "#64748b";
                    break;
            }

            if (performanceMap.containsKey(displayName)) {
                PerformancePoint existing = performanceMap.get(displayName);
                existing.setValue(existing.getValue() + count);
            } else {
                performanceMap.put(displayName, new PerformancePoint(displayName, count, color));
            }
        }

        // 5. Build DTO
        return DashboardAnalyticsDTO.builder()
                .todayIncome(today != null ? today : BigDecimal.ZERO)
                .monthIncome(month != null ? month : BigDecimal.ZERO)
                .totalIncome(total != null ? total : BigDecimal.ZERO)
                .totalSessions(sessions != null ? sessions : 0L)
                .incomeChart(incomeChart) // Lúc này incomeChart là List<Map> nên JSON sẽ cực sạch
                .performanceChart(new ArrayList<>(performanceMap.values()))
                .build();
    }
}
