package org.example.be_eproject_sem4.Entity;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
@Entity
@Table(name = "notification_logs")
@Getter
@Setter
public class NotificationLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // Dùng LAZY để khi nào cần mới load, tránh chậm app
    @JoinColumn(name = "notification_id", nullable = false) 
    private Notification notification;
    private Long userId;         // ID người nhận
    private boolean isRead = false;
    private LocalDateTime readAt;
}
