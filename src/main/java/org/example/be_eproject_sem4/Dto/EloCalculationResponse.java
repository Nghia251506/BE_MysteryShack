package org.example.be_eproject_sem4.Dto;

import lombok.Data;

@Data
public class EloCalculationResponse {
    private double actualScore;      // Điểm A
    private double expectedScore;    // Điểm E
    private double newElo;           // Elo sau khi cập nhật
    private String evaluation;       // Đánh giá từ hệ thống
}
