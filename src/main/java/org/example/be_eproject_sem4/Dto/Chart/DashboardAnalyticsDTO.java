package org.example.be_eproject_sem4.Dto.Chart;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DashboardAnalyticsDTO {
    private BigDecimal todayIncome;
    private BigDecimal monthIncome;
    private BigDecimal totalIncome;
    private Long totalSessions;
    private List<?> incomeChart;
    private List<PerformancePoint> performanceChart;
}