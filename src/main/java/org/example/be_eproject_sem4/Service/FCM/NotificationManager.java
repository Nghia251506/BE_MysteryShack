package org.example.be_eproject_sem4.Service.FCM;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.example.be_eproject_sem4.Entity.FcmToken;
import org.example.be_eproject_sem4.Repository.FcmTokenRepository;
import org.example.be_eproject_sem4.Repository.UserRepository;
import org.example.be_eproject_sem4.Security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class NotificationManager {
    @Autowired
    private FCMService fcmService;
    @Autowired
    private FcmTokenRepository tokenRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtTokenProvider jwtService;
    @Autowired
    private HttpServletRequest request;

    // Helper bốc tên người đang thực hiện hành động từ Token
    private String getSenderNameFromToken() {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                return jwtService.getFullnameFromToken(token);
            }
        } catch (Exception e) {
            System.out.println("DEBUG: Không lấy được tên từ Token: " + e.getMessage());
        }
        return "Người dùng";
    }

    // 1. READER: Nhận yêu cầu mới
    public void notifyReaderNewRequest(Long readerId, Long sessionId, String customerName) {
        userRepository.findById(readerId).ifPresent(user -> {
            if (user.getRole().toString().equals("READER") && user.isActive()) {
                String sender = (customerName != null) ? customerName : getSenderNameFromToken();
                Map<String, String> data = new HashMap<>();
                data.put("type", "NEW_MATCH_REQUEST");
                data.put("sessionId", sessionId.toString());
                data.put("customerName", sender);
                data.put("message", "Bạn có yêu cầu mới từ khách hàng " + sender);
                data.put("timeout", "30");
                data.put("sound", "notification.mp3");
                sendDataToUser(readerId, data);
            }
        });
    }

    // 2. CUSTOMER: Hệ thống đang tìm Reader
    public void notifyCustomerSearching(Long customerId) {
        userRepository.findById(customerId).ifPresent(user -> {
            Map<String, String> data = new HashMap<>();
            data.put("type", "SEARCHING_READER");
            data.put("message", "Hệ thống đang tìm Reader phù hợp cho bạn...");
            data.put("sound", "notification.mp3");
            sendDataToUser(customerId, data);
        });
    }

    // 3. CUSTOMER: Reader TỪ CHỐI
    public void notifyCustomerReaderRejected(Long customerId, String readerName) {
        userRepository.findById(customerId).ifPresent(user -> {
            String rName = (readerName != null) ? readerName : getSenderNameFromToken();
            Map<String, String> data = new HashMap<>();
            data.put("type", "READER_REJECTED");
            data.put("readerName", rName);
            data.put("message", "Rất tiếc! Reader " + rName + " đã từ chối. Hệ thống đang tìm người khác...");
            data.put("sound", "notification.mp3");
            sendDataToUser(customerId, data);
        });
    }

    // 4. CUSTOMER: Reader đã CHẤP NHẬN
    public void notifyCustomerAccepted(Long customerId, String readerName) {
        userRepository.findById(customerId).ifPresent(user -> {
            String rName = (readerName != null) ? readerName : getSenderNameFromToken();
            Map<String, String> data = new HashMap<>();
            data.put("type", "READER_ACCEPTED");
            data.put("readerName", rName);
            data.put("message", "Reader " + rName + " đã chấp nhận yêu cầu!");
            data.put("action", "START_READING");
            data.put("sound", "notification.mp3");
            sendDataToUser(customerId, data);
        });
    }

    // 5. CUSTOMER: Đã xong bài luận
    public void notifyReadingFinished(Long customerId, Long sessionId, String readerName) {
        userRepository.findById(customerId).ifPresent(user -> {
            String rName = (readerName != null) ? readerName : getSenderNameFromToken();
            Map<String, String> data = new HashMap<>();
            data.put("type", "READING_FINISHED");
            data.put("sessionId", sessionId.toString());
            data.put("readerName", rName);
            data.put("message", "Reader " + rName + " đã gửi kết quả luận giải!");
            data.put("sound", "success_ding.mp3");
            sendDataToUser(customerId, data);
        });
    }

    // 6. READER: Khách báo đã thanh toán
    public void notifyReaderPaymentSent(Long readerId, Long sessionId, String customerName) {
        userRepository.findById(readerId).ifPresent(user -> {
            String cName = (customerName != null) ? customerName : getSenderNameFromToken();
            Map<String, String> data = new HashMap<>();
            data.put("type", "PAYMENT_SENT");
            data.put("sessionId", sessionId.toString());
            data.put("customerName", cName);
            data.put("message", "Khách hàng " + cName + " báo đã chuyển khoản.");
            data.put("sound", "tingting.mp3");
            sendDataToUser(readerId, data);
        });
    }

    // 7. CUSTOMER: Reader xác nhận nhận tiền
    public void notifyCustomerPaymentConfirmed(Long customerId, Long sessionId) {
        userRepository.findById(customerId).ifPresent(user -> {
            Map<String, String> data = new HashMap<>();
            data.put("type", "PAYMENT_CONFIRMED");
            data.put("sessionId", sessionId.toString());
            data.put("message", "Thanh toán thành công! Bạn đã có thể xem toàn bộ bài luận.");
            data.put("sound", "notification.mp3");
            sendDataToUser(customerId, data);
        });
    }

    // 8. CUSTOMER: Đã tìm thấy Reader thành công
    public void notifyReaderMatched(Long customerId, String readerName) {
        userRepository.findById(customerId).ifPresent(user -> {
            Map<String, String> data = new HashMap<>();
            data.put("type", "READER_MATCHED_SUCCESS");
            data.put("readerName", readerName);
            data.put("message", "Đã tìm thấy Reader " + readerName + " phù hợp!");
            data.put("sound", "success_ding.mp3");
            sendDataToUser(customerId, data);
        });
    }

    // 9. READER: Đánh giá mới
    public void notifyReaderNewRating(Long readerId, Integer ratingValue, String comment, String customerName) {
        userRepository.findById(readerId).ifPresent(user -> {
            String cName = (customerName != null) ? customerName : getSenderNameFromToken();
            Map<String, String> data = new HashMap<>();
            data.put("type", "NEW_RATING");
            data.put("ratingValue", String.valueOf(ratingValue));
            data.put("customerName", cName);
            data.put("comment", comment != null ? comment : "");
            data.put("message", "Bạn nhận được " + ratingValue + " sao từ " + cName);
            data.put("sound", "success_ding.mp3");
            sendDataToUser(readerId, data);
        });
    }

    // 10. Gửi thông báo CHUNG (Dùng cho Tab "Gửi thông báo" ở Admin)
    public void notifyBroadcast(Long userId, String title, String content, String type, String link, String btnText) {
        userRepository.findById(userId).ifPresent(user -> {
            Map<String, String> data = new HashMap<>();
            data.put("type", type != null ? type : "GENERAL_SYSTEM");
            data.put("title", title);
            data.put("message", content);
            if (link != null) data.put("link", link);
            if (btnText != null) data.put("btnText", btnText);
            data.put("sound", "notification.mp3");
            
            sendDataToUser(userId, data);
        });
    }

    // 11. Thông báo KHUYẾN MÃI (Promotion)
    public void notifyPromotion(Long userId, String title, String content, String promoCode) {
        userRepository.findById(userId).ifPresent(user -> {
            Map<String, String> data = new HashMap<>();
            data.put("type", "PROMOTION");
            data.put("title", title);
            data.put("message", content);
            if (promoCode != null) data.put("promoCode", promoCode);
            data.put("sound", "success_ding.mp3");
            
            sendDataToUser(userId, data);
        });
    }

    // 12. Thông báo KỶ LUẬT/BLOCK (Bắn phát cuối trước khi bị logout hoặc bị chặn)
    public void notifyAccountBlocked(Long userId, String reason) {
        userRepository.findById(userId).ifPresent(user -> {
            Map<String, String> data = new HashMap<>();
            data.put("type", "ACCOUNT_BLOCKED");
            data.put("title", "Thông báo tài khoản");
            data.put("message", "Tài khoản của bạn đã bị khóa. Lý do: " + (reason != null ? reason : "Vi phạm tiêu chuẩn cộng đồng."));
            data.put("action", "FORCE_LOGOUT"); // FE có thể dùng cái này để đá user ra
            data.put("sound", "warning.mp3");
            
            sendDataToUser(userId, data);
        });
    }

    // 13. Cập nhật BẢO TRÌ (Maintenance)
    public void notifyMaintenance(Long userId, String startTime, String duration) {
        userRepository.findById(userId).ifPresent(user -> {
            Map<String, String> data = new HashMap<>();
            data.put("type", "MAINTENANCE");
            data.put("title", "Hệ thống bảo trì");
            data.put("message", "Hệ thống sẽ bảo trì từ " + startTime + " trong khoảng " + duration + ". Vui lòng quay lại sau.");
            data.put("sound", "notification.mp3");
            
            sendDataToUser(userId, data);
        });
    }

    // Helper cho việc gửi toàn hệ thống (Broadcast All)
    // Method này Admin dùng để loop gửi cho tất cả user
    public void sendToAllUsers(String title, String content, String type, String link, String btnText) {
        userRepository.findAll().forEach(user -> {
            if (user.isActive()) {
                notifyBroadcast(user.getId(), title, content, type, link, btnText);
            }
        });
    }

    private void sendDataToUser(Long userId, Map<String, String> data) {
        List<FcmToken> tokens = tokenRepository.findByUserId(userId);
        if (!tokens.isEmpty()) {
            tokens.forEach(t -> fcmService.sendDataMessage(t.getToken(), data));
        }
    }
}