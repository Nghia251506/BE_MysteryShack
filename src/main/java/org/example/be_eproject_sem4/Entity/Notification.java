package org.example.be_eproject_sem4.Entity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title; // Tiêu đề (formData.title)

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content; // Nội dung (formData.content)

    @Column(length = 50)
    private String type; // Loại: System, Promotion, Warning... (formData.type)

    @Column(name = "recipient_group")
    private String recipientGroup; // Nhóm: All, AllReaders, Specific... (formData.recipientGroup)

    @Column(name = "specific_id")
    private String specificId; // ID cụ thể nếu chọn Specific (formData.specificId)

    private String link; // Link hành động (formData.link)

    @Column(name = "btn_text")
    private String btnText; // Văn bản nút (formData.btnText)

    @Enumerated(EnumType.STRING)
    private NotificationStatus status; // Sent, Draft, Scheduled

    @Column(name = "total_count")
    private Integer totalCount = 0; // Tổng số người nhận (Để hiện readCount/totalCount)

    @Column(name = "read_count")
    private Integer readCount = 0; // Số người đã đọc

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt; // Thời gian gửi (Dùng để hiện ở Tab History)

    // Enum nội bộ để quản lý trạng thái cho chuyên nghiệp
    public enum NotificationStatus {
        SENT, DRAFT, SCHEDULED
    }
}
