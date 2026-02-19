package org.example.be_eproject_sem4.Repository;

import org.example.be_eproject_sem4.Entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    // Lấy danh sách thông báo, cái nào mới nhất thì hiện lên đầu (Tab History)
    Page<Notification> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    // Nếu ông muốn filter theo loại thông báo (System, Promotion...) ở thanh Search
    Page<Notification> findByTypeAndTitleContainingOrderByCreatedAtDesc(String type, String title, Pageable pageable);

    
}