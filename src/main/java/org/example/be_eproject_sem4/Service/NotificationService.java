package org.example.be_eproject_sem4.Service;

import org.example.be_eproject_sem4.Dto.Notification.NotificationRequest;
import org.example.be_eproject_sem4.Dto.Notification.NotificationResponse;
import org.example.be_eproject_sem4.Entity.Notification;
import org.example.be_eproject_sem4.Entity.NotificationLog;
import org.example.be_eproject_sem4.Mapper.NotificationMapper;
import org.example.be_eproject_sem4.Repository.NotificationLogRepository;
import org.example.be_eproject_sem4.Repository.NotificationRepository;
import org.example.be_eproject_sem4.Repository.UserRepository;
import org.example.be_eproject_sem4.Service.FCM.NotificationManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationLogRepository notificationLogRepository; // Thêm repo này
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;
    private final NotificationManager notificationManager;

    // 1. Lấy lịch sử (Giữ nguyên)
    public Page<NotificationResponse> getHistory(Pageable pageable) {
        return notificationRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(notificationMapper::toResponse);
    }

    // 2. Xử lý logic gửi thông báo từ Admin
    @Transactional
    public NotificationResponse processAdminNotification(NotificationRequest request) {
        Notification notification = notificationMapper.toEntity(request);
        notification.setStatus(Notification.NotificationStatus.SENT);

        long totalRecipient = countTotalRecipients(request.getRecipientGroup(), request.getSpecificId());
        notification.setTotalCount((int) totalRecipient);

        // Lưu cái "gốc" trước
        Notification savedNoti = notificationRepository.save(notification);

        // Bắn Async để rải bản ghi Log và gửi FCM
        sendAsyncNotification(savedNoti, request);

        return notificationMapper.toResponse(savedNoti);
    }

    @Async
    protected void sendAsyncNotification(Notification savedNoti, NotificationRequest request) {
        String group = request.getRecipientGroup();

        if ("Specific".equalsIgnoreCase(group)) {
            Long targetId = Long.parseLong(request.getSpecificId());
            // TẠO LOG: Lưu bản ghi chờ User đọc
            saveLog(savedNoti, targetId);

            // BẮN FCM
            notificationManager.notifyBroadcast(targetId, request.getTitle(),
                    request.getContent(), request.getType(), request.getLink(), request.getBtnText());
        } else {
            // Lưu log và bắn FCM cho cả nhóm
            userRepository.findAll().forEach(user -> {
                if (shouldReceive(user, group)) {
                    // TẠO LOG: Cho từng người trong nhóm
                    saveLog(savedNoti, user.getId());

                    // BẮN FCM
                    notificationManager.notifyBroadcast(user.getId(), request.getTitle(),
                            request.getContent(), request.getType(), request.getLink(), request.getBtnText());
                }
            });
        }
    }

    @Transactional
    public void deleteNotification(Long id) {
        if (!notificationRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy thông báo với ID: " + id);
        }
        // Vì đã cấu hình CascadeType.ALL ở Entity nên xoá ở đây là xoá sạch Log liên quan
        notificationLogRepository.deleteByNotificationId(id);
        notificationRepository.deleteById(id);
    }

    // 4. Lấy chi tiết thông báo kèm số liệu readCount thực tế
    public NotificationResponse getById(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông báo!"));
        
        // Tính toán lại readCount thực tế từ bảng Log trước khi trả về
        long actualReadCount = notificationLogRepository.countByNotificationIdAndIsReadTrue(id);
        notification.setReadCount((int) actualReadCount);
        
        return notificationMapper.toResponse(notification);
    }

    // Helper lưu bản ghi Log vào DB
    private void saveLog(Notification notification, Long userId) {
        NotificationLog log = new NotificationLog();
        log.setNotification(notification);
        log.setUserId(userId);
        log.setRead(false); // Mặc định là chưa đọc
        notificationLogRepository.save(log);
    }

    // Helper: Đếm số người sẽ nhận để làm thống kê
    private long countTotalRecipients(String group, String specificId) {
        if ("Specific".equalsIgnoreCase(group)) {
            return 1;
        }
        if ("All".equalsIgnoreCase(group)) {
            return userRepository.countByIsActiveTrue();
        }
        if ("AllReaders".equalsIgnoreCase(group)) {
            return userRepository.countByRoleAndIsActiveTrue("READER");
        }
        if ("AllCustomers".equalsIgnoreCase(group)) {
            return userRepository.countByRoleAndIsActiveTrue("CUSTOMER");
        }
        return 0;
    }

    // Helper: Kiểm tra User có thuộc nhóm nhận không
    private boolean shouldReceive(org.example.be_eproject_sem4.Entity.User user, String group) {
        if (!user.isActive()) {
            return false;
        }
        if ("All".equalsIgnoreCase(group)) {
            return true;
        }
        if ("AllReaders".equalsIgnoreCase(group) && "READER".equals(user.getRole().toString())) {
            return true;
        }
        if ("AllCustomers".equalsIgnoreCase(group) && "CUSTOMER".equals(user.getRole().toString())) {
            return true;
        }
        return false;
    }

}
