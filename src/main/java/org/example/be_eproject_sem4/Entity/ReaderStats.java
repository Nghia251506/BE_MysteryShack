package org.example.be_eproject_sem4.Entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "reader_stats")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReaderStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "reader_id", nullable = false, unique = true)
    private User reader;

    @Column(name = "total_stars_month")
    private Integer totalStarsMonth = 0; // Tổng số sao tích lũy tháng này

    @Column(name = "total_reviews_month")
    private Integer totalReviewsMonth = 0; // Tổng số lượt rate tháng này

    @Column(name = "average_rating_month")
    private Double averageRatingMonth = 0.0; // Tính sẵn để FE lấy luôn

    // Hàm cập nhật nhanh để dùng trong Service
    public void updateRating(Integer newStars) {
        this.totalReviewsMonth += 1;
        this.totalStarsMonth += newStars;
        this.averageRatingMonth = (double) this.totalStarsMonth / this.totalReviewsMonth;
    }
}
