package org.example.be_eproject_sem4.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "reader_test_attempts")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ReaderTestAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reader_id")
    private User reader;

    private Double score;
    private Integer correctCount;
    private Integer totalQuestions;

    private String status; // PASSED hoặc FAILED

    private Integer attemptNumber = 1;

    @CreationTimestamp
    private Instant createdAt;
}
