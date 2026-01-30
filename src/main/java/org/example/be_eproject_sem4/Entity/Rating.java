package org.example.be_eproject_sem4.Entity;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name= "ratings")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "request_id", nullable = false)
    private ReadingSession request;
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;
    @ManyToOne
    @JoinColumn(name = "reader_id", nullable = false)
    private User reader;
    @Column(name = "rating", nullable = false)
    private Integer ratingValue;
    @Column(name = "review", columnDefinition = "TEXT")
    private String comment;
    @CreationTimestamp
    private Instant createdAt;
    
}
