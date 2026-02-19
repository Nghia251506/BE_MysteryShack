package org.example.be_eproject_sem4.Dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReaderStatsDTO {
    private BigDecimal todayIncome;
    private BigDecimal monthIncome;
    private BigDecimal totalIncome;
    private Long totalSessions;
}
