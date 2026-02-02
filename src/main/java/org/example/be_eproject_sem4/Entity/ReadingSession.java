package org.example.be_eproject_sem4.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.type.Decimal;
import jakarta.persistence.*;
import lombok.Data;

import org.example.be_eproject_sem4.Converter.SelectedCardsConverter;
import org.example.be_eproject_sem4.Dto.SelectedCardDto;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.example.be_eproject_sem4.Entity.ReadingStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "reading_sessions")
@Data
public class ReadingSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id")
    private User customer;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reader_id")
    private User reader;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "question_id")
    private TopicQuestion question;

    @OneToOne
    @JoinColumn(name = "history_id", referencedColumnName = "id")
    @JsonIgnore // Ngắt vòng lặp JSON khi API trả về
    private History history;

    @Convert(converter = SelectedCardsConverter.class)
    @Column(name = "selected_cards", columnDefinition = "JSON")
    private List<SelectedCardDto> selectedCards;
    @Column(name = "accepted_at")
    private Instant acceptedAt;
    @Column(name = "matched_at")
    private Instant matchedAt;

    private String status;
    @Column(name = "full_name", nullable = true)
    private String fullName;
    @Column(name = "birth_date", nullable = true)
    private LocalDate birthDate;
    @Column(name = "is_rated", nullable = false, columnDefinition = "boolean default false")
    private Boolean isRated = false;
    private Instant submitedAt;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    private Instant completedAt;
    @Column(name = "amount", columnDefinition = "decimal(10,2)")
    private BigDecimal amount;

    @Column(name = "response_time")
    private Double responseTime;

    // Hàm lấy response time để truyền vào EloService
    public Double calculateResponseTime() {
        if (createdAt == null || acceptedAt == null) return 999.0;

        // Chuyển LocalDateTime (createdAt) sang Instant để cùng kiểu với acceptedAt
        // Giả định hệ thống dùng múi giờ Việt Nam (UTC+7)
        Instant createdInstant = createdAt.atZone(java.time.ZoneId.systemDefault()).toInstant();

        long diffInSeconds = java.time.Duration.between(createdInstant, acceptedAt).getSeconds();
        return (double) diffInSeconds / 60.0;
    }
    public void completeSession(String completedStatus) {
        this.status = completedStatus; // "COMPLETED"
        this.completedAt = Instant.now();
    }

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Long> rejectedReaderIds = new HashSet<>();
}
