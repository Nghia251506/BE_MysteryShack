package org.example.be_eproject_sem4.Dto;

import lombok.Data;

@Data
public class EloCalculationRequest {
    private double currentElo;       // Elo hiện tại của Reader
    private double userReputation;   // Reputation của Khách hàng
    private int kFactor;             // Hệ số K (40 cho người mới, thấp hơn cho người cũ) [cite: 15, 16]

    // Các thông số phiên tư vấn
    private double responseTime;      // Giây (để tính P) [cite: 31, 32]
    private boolean isCompleted;      // Trạng thái hoàn thành (để tính C) [cite: 41, 42]
    private int stars;                // Số sao khách chấm (1-5) [cite: 19]
    public double getPositiveRate() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getPositiveRate'");
    }
}
