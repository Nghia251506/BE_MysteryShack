package org.example.be_eproject_sem4.Entity;

import java.time.Instant;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "reading_disputes")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReadingDispute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "session_id", nullable = false)
    private ReadingSession session;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reader_id", nullable = false)
    private User reader;

    @Column(columnDefinition = "TEXT")
    private String reason; // Lý do khiếu nại

    @Column(columnDefinition = "JSON")
    private List<String> evidenceImages; // Link ảnh bằng chứng

    private String status; // PENDING, RESOLVED, REJECTED

    @Column(columnDefinition = "TEXT")
    private String adminNote; // Admin giải quyết thế nào

    private Instant resolvedAt;

    @CreationTimestamp
    private Instant createdAt;
}
