package org.example.be_eproject_sem4.Service;

import lombok.RequiredArgsConstructor;

import org.example.be_eproject_sem4.Repository.ReadingSessionRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class MatchingService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ReadingSessionRepository readingSessionRepository;

    // Key prefix để phân biệt với các app khác dùng chung Redis
    private final String QUEUE_KEY = "TAROT:MATCHING_QUEUE";

    /**
     * 1. Đẩy khách vào hàng đợi (Khi không tìm được Reader)
     */
    public void pushToQueue(Long sessionId) {
        // Chỉ cho phép hàng chờ tối đa 1000 phần tử để bảo vệ RAM 30MB
        Long currentSize = redisTemplate.opsForList().size(QUEUE_KEY);
        if (currentSize != null && currentSize > 1000) {
            // Nếu quá tải, xóa bớt thằng cũ nhất (bên trái) để nhường chỗ cho thằng mới
            redisTemplate.opsForList().leftPop(QUEUE_KEY);
        }

        redisTemplate.opsForList().rightPush(QUEUE_KEY, sessionId.toString());
        // Backup thêm TTL 24h cho cả List nếu lỡ App sập không dọn dẹp được
        redisTemplate.expire(QUEUE_KEY, 24, TimeUnit.HOURS);
    }

    /**
     * 2. Lấy khách ra khỏi hàng đợi (Khi có Reader rảnh)
     */
    public Long popFromQueue() {
        // Lấy thằng ở bên trái (thằng đứng đầu hàng - FIFO)
        String sessionIdStr = (String) redisTemplate.opsForList().leftPop(QUEUE_KEY);

        if (sessionIdStr != null) {
            return Long.parseLong(sessionIdStr);
        }
        return null;
    }

    /**
     * 3. Xóa một khách cụ thể khỏi hàng đợi (Ví dụ khách hủy không tìm nữa)
     */
    public void removeFromQueue(Long sessionId) {
        redisTemplate.opsForList().remove(QUEUE_KEY, 1, sessionId.toString());
    }

    /**
     * 4. Xem hiện có bao nhiêu khách đang chờ
     */
    public Long getQueueSize() {
        return redisTemplate.opsForList().size(QUEUE_KEY);
    }

    @Scheduled(cron = "0 0 * * * *") // Chạy đúng vào đầu mỗi giờ (ví dụ: 1:00, 2:00...)
    public void autoCleanupRedisQueue() {
        System.out.println(">>> [AUTO-CLEANUP] Bắt đầu quét hàng chờ Redis...");

        // 1. Lấy toàn bộ danh sách ID trong hàng chờ
        List<Object> sessionIds = redisTemplate.opsForList().range(QUEUE_KEY, 0, -1);

        if (sessionIds == null || sessionIds.isEmpty()) {
            System.out.println(">>> [AUTO-CLEANUP] Hàng chờ trống. Bỏ qua.");
            return;
        }

        for (Object idObj : sessionIds) {
            try {
                Long sessionId = Long.parseLong(idObj.toString());

                // 2. Kiểm tra thực tế trong Database
                readingSessionRepository.findById(sessionId).ifPresentOrElse(
                        session -> {
                            // Nếu khách không còn ở trạng thái PENDING thì xóa khỏi hàng chờ ngay
                            if (!"PENDING".equals(session.getStatus())) {
                                redisTemplate.opsForList().remove(QUEUE_KEY, 0, idObj.toString());
                                System.out.println(">>> [REMOVED] Session #" + sessionId + " đã có trạng thái: "
                                        + session.getStatus());
                            }
                        },
                        () -> {
                            // Nếu Session bị xóa khỏi DB vì lý do gì đó, dọn luôn ở Redis
                            redisTemplate.opsForList().remove(QUEUE_KEY, 0, idObj.toString());
                            System.out.println(">>> [REMOVED] Session #" + sessionId + " không tồn tại trong DB.");
                        });
            } catch (Exception e) {
                // Tránh việc lỗi 1 record làm hỏng cả vòng lặp
                System.err.println(">>> [ERROR] Lỗi khi dọn dẹp ID: " + idObj);
            }
        }
        System.out.println(">>> [AUTO-CLEANUP] Hoàn tất dọn dẹp định kỳ.");
    }
}