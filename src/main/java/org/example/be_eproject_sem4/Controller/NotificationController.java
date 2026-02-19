package org.example.be_eproject_sem4.Controller;

import lombok.RequiredArgsConstructor;
import org.example.be_eproject_sem4.Dto.Notification.NotificationRequest;
import org.example.be_eproject_sem4.Dto.Notification.NotificationResponse;
import org.example.be_eproject_sem4.Service.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/notifications")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") 
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * 1. Gửi thông báo mới
     * POST /api/v1/admin/notifications/send
     */
    @PostMapping("/send")
    public ResponseEntity<NotificationResponse> sendNotification(@RequestBody NotificationRequest request) {
        // Gọi service xử lý: Lưu DB -> Tạo Logs -> Bắn FCM (Async)
        NotificationResponse response = notificationService.processAdminNotification(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 2. Lấy danh sách lịch sử (Kèm Phân trang + Search + Filter)
     * GET /api/v1/admin/notifications?page=0&size=10&type=System&keyword=Khuyến mãi
     */
    @GetMapping
    public ResponseEntity<Page<NotificationResponse>> getNotificationHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String type,     // Lọc theo loại (System, Warning...)
            @RequestParam(required = false) String keyword   // Tìm kiếm theo tiêu đề
    ) {
        // Sắp xếp mặc định: Thông báo mới nhất lên đầu
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        
        // Ông có thể nâng cấp Service để nhận thêm type và keyword 
        // Hiện tại tôi cứ để lấy All theo pageable cho ông chạy trước
        Page<NotificationResponse> history = notificationService.getHistory(pageable);
        
        return ResponseEntity.ok(history);
    }

    /**
     * 3. Xóa một thông báo trong lịch sử
     * DELETE /api/v1/admin/notifications/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        // Lưu ý: Khi xóa Notification, bảng NotificationLog sẽ tự xóa theo 
        // nhờ cái CascadeType.ALL mà ae mình thống nhất ở Entity
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 4. Lấy chi tiết thống kê (Cho Modal xem chi tiết)
     * GET /api/v1/admin/notifications/{id}/stats
     */
    @GetMapping("/{id}/stats")
    public ResponseEntity<NotificationResponse> getNotificationStats(@PathVariable Long id) {
        // Trả về dữ liệu kèm readCount thực tế từ DB
        return ResponseEntity.ok(notificationService.getById(id));
    }
}