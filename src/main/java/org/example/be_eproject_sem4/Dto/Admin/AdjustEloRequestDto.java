package org.example.be_eproject_sem4.Dto.Admin;

import lombok.Data;

@Data
public class AdjustEloRequestDto {
    private Integer amount; // Số điểm thay đổi (có thể âm hoặc dương)
    private String type;   // BONUS, PENALTY, SYSTEM_ADJUST
    private String reason; // Lý do điều chỉnh
}
