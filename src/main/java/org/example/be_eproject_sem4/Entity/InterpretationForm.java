package org.example.be_eproject_sem4.Entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "interpretation_forms")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterpretationForm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id")
    private ReadingSession requestId;
    @OneToOne
    @JoinColumn(name = "history_id", referencedColumnName = "id")
    @JsonIgnore // Ngắt vòng lặp JSON khi API trả về
    private History history;
    @Column(name = "interpretation_1", columnDefinition = "TEXT")
    private String interpretation1; // Luận giải cho lá 1

    @Column(name = "interpretation_2", columnDefinition = "TEXT")
    private String interpretation2; // Luận giải cho lá 2

    @Column(name = "interpretation_3", columnDefinition = "TEXT")
    private String interpretation3; // Luận giải cho lá 3
    @Column(name = "qr_payment_url")
    private String qrPayment;
    @Column(name = "advice")
    private String advice;
    @Column(name = "status")
    private InterpretationStatus status;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
