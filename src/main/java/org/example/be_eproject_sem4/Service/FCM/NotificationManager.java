package org.example.be_eproject_sem4.Service.FCM;

import jakarta.servlet.http.HttpServletRequest;
import org.example.be_eproject_sem4.Entity.FcmToken;
import org.example.be_eproject_sem4.Repository.FcmTokenRepository;
import org.example.be_eproject_sem4.Repository.UserRepository;
import org.example.be_eproject_sem4.Security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NotificationManager {
    @Autowired
    private FCMService fcmService;
    @Autowired
    private FcmTokenRepository tokenRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtTokenProvider jwtService; // Tiêm JwtService vào
    @Autowired
    private HttpServletRequest request; // Để bốc Token từ header

    // Hàm Helper lấy tên thằng đang thực hiện (Người gửi) từ Token
    private String getSenderNameFromToken() {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return jwtService.getFullnameFromToken(token);
        }
        return "Người dùng";
    }

    // 1. READER: Nhận yêu cầu mới (Tên khách bốc từ Token khách)
    public void notifyReaderNewRequest(Long readerId, Long sessionId, String customerName) {
        userRepository.findById(readerId).ifPresent(user -> {
            if (user.getRole().toString().equals("READER") && user.isActive()) {
                // Ưu tiên customerName truyền vào, nếu ko có thì bốc từ Token
                String finalCustomerName = (customerName != null) ? customerName : getSenderNameFromToken();

                Map<String, String> data = new HashMap<>();
                data.put("type", "NEW_MATCH_REQUEST");
                data.put("sessionId", sessionId.toString());
                data.put("customerName", finalCustomerName);
                data.put("message", "Bạn có yêu cầu mới từ khách hàng " + finalCustomerName);
                data.put("sound", "notification.mp3");
                data.put("timeout", "30");

                sendDataToUser(readerId, data);
            }
        });
    }

    // 2. CUSTOMER: Reader đã Accept (Tên Reader bốc từ Token Reader)
    public void notifyCustomerAccepted(Long customerId, String readerName) {
        userRepository.findById(customerId).ifPresent(user -> {
            String finalReaderName = (readerName != null) ? readerName : getSenderNameFromToken();

            Map<String, String> data = new HashMap<>();
            data.put("type", "READER_ACCEPTED");
            data.put("readerName", finalReaderName);
            data.put("message", "Reader " + finalReaderName + " đã chấp nhận yêu cầu của bạn!");
            data.put("sound", "notification.mp3");

            sendDataToUser(customerId, data);
        });
    }

    // 3. READER: Khách báo đã thanh toán (Tên khách bốc từ Token khách)
    public void notifyReaderPaymentSent(Long readerId, Long sessionId, String customerName) {
        userRepository.findById(readerId).ifPresent(user -> {
            String finalCustomerName = (customerName != null) ? customerName : getSenderNameFromToken();

            Map<String, String> data = new HashMap<>();
            data.put("type", "PAYMENT_SENT");
            data.put("sessionId", sessionId.toString());
            data.put("customerName", finalCustomerName);
            data.put("message", "Khách hàng " + finalCustomerName + " báo đã chuyển tiền.");
            data.put("sound", "notification.mp3");

            sendDataToUser(readerId, data);
        });
    }

    // 4. READER: Có đánh giá mới (Tên khách từ Token khách)
    public void notifyReaderNewRating(Long readerId, Integer ratingValue, String comment, String customerName) {
        userRepository.findById(readerId).ifPresent(user -> {
            String finalCustomerName = (customerName != null) ? customerName : getSenderNameFromToken();

            Map<String, String> data = new HashMap<>();
            data.put("type", "NEW_RATING");
            data.put("ratingValue", String.valueOf(ratingValue));
            data.put("customerName", finalCustomerName);
            data.put("comment", comment != null ? comment : "");
            data.put("message", "Bạn nhận được " + ratingValue + " sao từ " + finalCustomerName);

            sendDataToUser(readerId, data);
        });
    }

    // 5. CUSTOMER: Reader đã xong bài luận (Tên Reader từ Token Reader)
    public void notifyReadingFinished(Long customerId, Long sessionId, String readerName) {
        userRepository.findById(customerId).ifPresent(user -> {
            String finalReaderName = (readerName != null) ? readerName : getSenderNameFromToken();

            Map<String, String> data = new HashMap<>();
            data.put("type", "READING_FINISHED");
            data.put("sessionId", sessionId.toString());
            data.put("readerName", finalReaderName);
            data.put("message", "Reader " + finalReaderName + " đã hoàn thành bài luận của bạn!");

            sendDataToUser(customerId, data);
        });
    }

    // --- Các hàm notify hệ thống không cần lấy tên người gửi từ Token ---
    public void notifyCustomerSearching(Long customerId) {
        userRepository.findById(customerId).ifPresent(user -> {
            Map<String, String> data = new HashMap<>();
            data.put("type", "SEARCHING_READER");
            data.put("message", "Hệ thống đang tìm Reader phù hợp...");
            sendDataToUser(customerId, data);
        });
    }

    private void sendDataToUser(Long userId, Map<String, String> data) {
        List<FcmToken> tokens = tokenRepository.findByUserId(userId);
        if (!tokens.isEmpty()) {
            tokens.forEach(t -> fcmService.sendDataMessage(t.getToken(), data));
        }
    }
}