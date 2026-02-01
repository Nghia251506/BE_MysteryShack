package org.example.be_eproject_sem4.Dto.Rate;

import lombok.*;

@Data
@Builder
public class ReaderStatsDTO {
    private Double averageRatingMonth;
    private Integer totalReviewsMonth;
    private Integer totalStarsMonth;
    // Sau này có thể thêm field: "Hạng của tháng" hoặc "Tỷ lệ tăng trưởng"
}
