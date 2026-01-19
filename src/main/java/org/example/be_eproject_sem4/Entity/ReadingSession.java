package org.example.be_eproject_sem4.Entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "reading_sessions")
@Data
public class ReadingSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long customerId;

    private Long questionId;

    @Column(columnDefinition = "TEXT")
    private String customerQuestion;

    @Column(columnDefinition = "JSON")
    private String selectedCards; // Có thể dùng Map hoặc Object tùy cấu hình Hibernate JSON

    @Column(columnDefinition = "TEXT")
    private String summaryMeaning;

    private String status;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
