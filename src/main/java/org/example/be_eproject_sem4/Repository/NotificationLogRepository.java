package org.example.be_eproject_sem4.Repository;

import org.example.be_eproject_sem4.Entity.NotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {
    
    // Đếm xem một thông báo đã có bao nhiêu người đọc
    long countByNotificationIdAndIsReadTrue(Long notificationId);
    
    // Kiểm tra xem User cụ thể đã đọc thông báo đó chưa (để tránh đếm trùng)
    boolean existsByNotificationIdAndUserId(Long notificationId, Long userId);
    long countByNotification_IdAndIsReadTrue(Long notificationId);

    void deleteByNotificationId(Long id);
}