package org.example.be_eproject_sem4.Entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "subscriptions")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reader_id", nullable = false)
    private User reader;

    @ManyToOne
    @JoinColumn(name = "package_id", nullable = false)
    private VipPackage vipPackage;

    @Column(name = "start_date", nullable = false)
    private Instant startDate;

    @Column(name = "end_date", nullable = false)
    private Instant endDate;

    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status = SubscriptionStatus.ACTIVE;

    @Column(name = "remaining_jobs")
    private Integer remainingJobs; // Số lượt còn lại trong ngày hôm nay

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();
}
