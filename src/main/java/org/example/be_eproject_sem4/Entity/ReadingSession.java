package org.example.be_eproject_sem4.Entity;

import jakarta.persistence.*;
import lombok.Data;

import org.example.be_eproject_sem4.Converter.SelectedCardsConverter;
import org.example.be_eproject_sem4.Dto.SelectedCardDto;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "reading_sessions")
@Data
public class ReadingSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private User customer;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reader_id")
    private User reader;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private TopicQuestion question;

    @Convert(converter = SelectedCardsConverter.class)
    @Column(name = "selected_cards", columnDefinition = "JSON")
    private List<SelectedCardDto> selectedCards;
    @Column(name="match_timeout_at")
    private LocalDateTime matchTimeoutAt;

    private String status;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
