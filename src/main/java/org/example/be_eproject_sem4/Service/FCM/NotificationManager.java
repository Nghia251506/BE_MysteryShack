package org.example.be_eproject_sem4.Service.FCM;

import java.util.List;
import java.util.Map;

import org.example.be_eproject_sem4.Entity.FcmToken;
import org.example.be_eproject_sem4.Repository.FcmTokenRepository;
import org.example.be_eproject_sem4.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationManager {
    @Autowired
    private FCMService fcmService;
    @Autowired
    private FcmTokenRepository tokenRepository;
    @Autowired
    private UserRepository userRepository;

    // --- 1. READER: Nhận yêu cầu mới (Popup Grab) ---
    public void notifyReaderNewRequest(Long readerId, Long sessionId, String customerName, String customerAvatar,
            double customerRating) {
        userRepository.findById(readerId).ifPresent(user -> {
            boolean isActive = user.isActive();
            if ("READER".equals(user.getRole()) && isActive) {
                Map<String, String> data = Map.of(
                        "type", "NEW_MATCH_REQUEST",
                        "sessionId", sessionId.toString(),
                        "customerName", customerName,
                        "customerRating", String.valueOf(customerRating),
                        "customerAvatar", customerAvatar != null ? customerAvatar : "",
                        "timeout", "30",
                        "sound", "notification.mp3");
                sendDataToUser(readerId, data);
            } else {
                System.out.println("DEBUG: User " + readerId + " không phải Reader hoặc đang bận, không bắn FCM.");
            }
        });
    }

    // --- 2. CUSTOMER: Hệ thống đang tìm Reader (Sau khi createSession) ---
    public void notifyCustomerSearching(Long customerId) {
        userRepository.findById(customerId).ifPresent(user -> {
            if ("CUSTOMER".equals(user.getRole())) {
                Map<String, String> data = Map.of(
                        "type", "SEARCHING_READER",
                        "message", "Yêu cầu đã gửi. Hệ thống đang tìm Reader phù hợp cho bạn...",
                        "sound", "notification.mp3");
                sendDataToUser(customerId, data);
            }else{
                System.out.println("DEBUG: User " + customerId + " không phải Customer, không bắn FCM.");
            }
        });
    }

    // --- 3. CUSTOMER: Reader từ chối (Thông báo chờ người khác) ---
    public void notifyCustomerReaderRejected(Long customerId, String readerName) {
        userRepository.findById(customerId).ifPresent(user -> {
            if ("CUSTOMER".equals(user.getRole())) {
                Map<String, String> data = Map.of(
                        "type", "READER_REJECTED",
                        "readerName", readerName,
                        "message", "Rất tiếc! Reader " + readerName + " đã từ chối yêu cầu của bạn. Hệ thống sẽ tiếp tục tìm Reader khác...",
                        "sound", "notification.mp3");
                sendDataToUser(customerId, data);
            }else{
                System.out.println("DEBUG: User " + customerId + " không phải Customer, không bắn FCM.");
            }
        });
    }

    // --- 4. CUSTOMER: Reader đã Accept (Bắt đầu xem bài) ---
    public void notifyCustomerAccepted(Long customerId, String readerName) {
        userRepository.findById(customerId).ifPresent(user -> {
            if ("CUSTOMER".equals(user.getRole())) {
                Map<String, String> data = Map.of(
                        "type", "READER_ACCEPTED",
                        "readerName", readerName,
                        "message", "Tuyệt vời! Reader " + readerName + " đã chấp nhận yêu cầu của bạn. Hãy vào trang chi tiết kết quả và chờ Reader " + readerName + " luận giải thôi nào!",
                        "action", "START_READING",
                        "sound", "notification.mp3");
                sendDataToUser(customerId, data);
            }else{
                System.out.println("DEBUG: User " + customerId + " không phải Customer, không bắn FCM.");
            }
        });
    }

    // --- 5. CUSTOMER: Reader đã submit luận giải (Popup mở bài) ---
    public void notifyReadingFinished(Long customerId, Long sessionId, String readerName) {
        userRepository.findById(customerId).ifPresent(user -> {
            if ("CUSTOMER".equals(user.getRole())) {
                Map<String, String> data = Map.of(
                        "type", "READING_FINISHED",
                        "sessionId", sessionId.toString(),
                        "message", "Reader " + readerName + " đã hoàn thành luận giải cho bạn! Hãy vào trang chi tiết để xem kết quả và hoàn thành thủ tục thanh toán.",
                        "action", "VIEW_READING",
                        "sound", "notification.mp3");
                sendDataToUser(customerId, data);
            }else{
                System.out.println("DEBUG: User " + customerId + " không phải Customer, không bắn FCM.");
            }
        });
    }

    // --- 6. READER: Khách báo đã chuyển tiền ---
    public void notifyReaderPaymentSent(Long readerId, Long sessionId, String customerName) {
        userRepository.findById(readerId).ifPresent(user -> {
            if ("READER".equals(user.getRole())) {
                Map<String, String> data = Map.of(
                        "type", "PAYMENT_SENT",
                        "sessionId", sessionId.toString(),
                        "message", "Khách hàng " + customerName + " đã xác nhận chuyển tiền cho bạn. Vui lòng kiểm tra và mở khóa luận giải cho khách hàng.",
                        "action", "VIEW_SESSION",
                        "sound", "notification.mp3");
                sendDataToUser(readerId, data);
            }else{
                System.out.println("DEBUG: User " + readerId + " không phải Reader, không bắn FCM.");
            }
        });
    }

    // --- 7. CUSTOMER: Reader xác nhận đã nhận tiền (Popup Unlock hoàn toàn) ---
    public void notifyCustomerPaymentConfirmed(Long customerId, Long sessionId) {
        userRepository.findById(customerId).ifPresent(user -> {
            if ("CUSTOMER".equals(user.getRole())) {
                Map<String, String> data = Map.of(
                        "type", "PAYMENT_CONFIRMED",
                        "sessionId", sessionId.toString(),
                        "message", "Reader đã xác nhận nhận được tiền từ bạn! Bây giờ bạn có thể xem toàn bộ luận giải.",
                        "action", "VIEW_FULL_READING",
                        "sound", "notification.mp3");
                sendDataToUser(customerId, data);
            }else{
                System.out.println("DEBUG: User " + customerId + " không phải Customer, không bắn FCM.");
            }
        });
    }

    // Hàm helper để gửi Data Message tới tất cả token của 1 User
    private void sendDataToUser(Long userId, Map<String, String> data) {
        List<FcmToken> tokens = tokenRepository.findByUserId(userId);
        if (!tokens.isEmpty()) {
            tokens.forEach(t -> fcmService.sendDataMessage(t.getToken(), data));
        }
    }
}