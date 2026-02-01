package org.example.be_eproject_sem4.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EloCalculationRequest {
    private double currentElo;       // Elo hiện tại của Reader
    private double userReputation;   // Reputation của Khách hàng
    private int kFactor;             // Hệ số K

    private double responseTime;     // Số phút (để tính P)
    private boolean isCompleted;     // Trạng thái hoàn thành (để tính C)
    private double stars;            // Số sao khách chấm (dùng double để chia cho chuẩn)
    private double positiveRate;     // Tỷ lệ tích cực (Stats.averageRatingMonth / 5)
}