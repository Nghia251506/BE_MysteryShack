package org.example.be_eproject_sem4.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "vip_packages")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class VipPackage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "duration_days", nullable = false)
    private Integer durationDays;

    @Column(columnDefinition = "TEXT")
    private String benefits;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    // Gợi ý: Thêm trường này để sau này lọc Job
    @Column(name = "max_jobs_per_day")
    private Integer maxJobsPerDay;
}
