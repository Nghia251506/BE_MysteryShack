package org.example.be_eproject_sem4.Entity;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import org.example.be_eproject_sem4.Entity.ReadingStatus;
import org.example.be_eproject_sem4.Entity.TopicQuestion;

@Data
@Entity
@Table(name= "histories")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class History {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- MỐI QUAN HỆ (CẦU NỐI) ---

    // 1. Khách hàng (Người gửi yêu cầu)
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User customer;

    // 2. Reader (Người thực hiện trải bài)
    // Nullable = true vì lúc khách mới gửi yêu cầu thì chưa chắc đã có Reader nhận ngay
    @ManyToOne
    @JoinColumn(name = "reader_id", nullable = true)
    private User reader;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "question_id")
    private TopicQuestion question;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "request_id")
    private ReadingSession request;

    @OneToOne(mappedBy = "history", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private InterpretationForm interpretationForm;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReadingStatus status; // PENDING -> IN_PROGRESS -> COMPLETED -> CANCELED

    // --- ĐÁNH GIÁ (FEEDBACK) ---
    // Dùng để Reader xem lại uy tín của mình và Khách xem lịch sử
    @Column
    private Integer rating; // 1 đến 5 sao

    @Column(columnDefinition = "TEXT")
    private String feedback; // Nhận xét của khách sau khi nhận kết quả

    // --- THỜI GIAN ---

    @Column(name = "created_at")
    private LocalDateTime createdAt; // Lúc khách gửi

    @Column(name = "completed_at")
    private LocalDateTime completedAt; // Lúc Reader trả kết quả

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = ReadingStatus.PENDING; // Mặc định là đang chờ
        }
    }
}