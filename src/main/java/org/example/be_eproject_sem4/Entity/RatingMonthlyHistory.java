package org.example.be_eproject_sem4.Entity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Data
@Entity
@Table(name = "rating_monthly_history")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RatingMonthlyHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reader_id", nullable = false)
    private User reader;

    private Integer month;
    private Integer year;

    private Double finalAvgRating;
    private Integer totalReviews;

    @CreationTimestamp
    private Instant archivedAt;
    
}
