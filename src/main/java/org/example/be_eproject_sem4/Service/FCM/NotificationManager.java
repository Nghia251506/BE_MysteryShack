package org.example.be_eproject_sem4.Service.FCM;

import java.util.List;
import java.util.Map;

import org.example.be_eproject_sem4.Entity.FcmToken;
import org.example.be_eproject_sem4.Repository.FcmTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationManager {
    @Autowired private FCMService fcmService;
    @Autowired private FcmTokenRepository tokenRepository;

    // --- 1. READER: Nhận yêu cầu mới (Popup Grab) ---
    public void notifyReaderNewRequest(Long readerId, Long sessionId, String customerName, String customerAvatar, double customerRating) {
        Map<String, String> data = Map.of(
            "type", "NEW_MATCH_REQUEST",
            "sessionId", sessionId.toString(),
            "customerName", customerName,
            "customerRating", String.valueOf(customerRating),
            "customerAvatar", customerAvatar != null ? customerAvatar : "",
            "timeout", "30",
            "sound", "notification.mp3"
        );
        sendDataToUser(readerId, data);
    }

    // --- 2. CUSTOMER: Hệ thống đang tìm Reader (Sau khi createSession) ---
    public void notifyCustomerSearching(Long customerId) {
        Map<String, String> data = Map.of(
            "type", "SEARCHING_READER",
            "message", "Yêu cầu đã gửi. Hệ thống đang tìm Reader phù hợp cho bạn...",
            "sound", "notification.mp3"
        );
        sendDataToUser(customerId, data);
    }

    // --- 3. CUSTOMER: Reader từ chối (Thông báo chờ người khác) ---
    public void notifyCustomerReaderRejected(Long customerId, String readerName) {
        Map<String, String> data = Map.of(
            "type", "READER_REJECTED",
            "message", "Hệ thống ghi nhận " + readerName + " từ chối. Vui lòng đợi trong giây lát để chúng tôi tìm Reader khác phù hợp.",
            "sound", "notification.mp3"
        );
        sendDataToUser(customerId, data);
    }

    // --- 4. CUSTOMER: Reader đã Accept (Bắt đầu xem bài) ---
    public void notifyCustomerAccepted(Long customerId, String readerName) {
        Map<String, String> data = Map.of(
            "type", "READER_ACCEPTED",
            "readerName", readerName,
            "message", "Reader " + readerName + " đã chấp nhận và đang xem bài cho bạn.",
            "sound", "notification.mp3"
        );
        sendDataToUser(customerId, data);
    }

    // --- 5. CUSTOMER: Reader đã submit luận giải (Popup mở bài) ---
    public void notifyReadingFinished(Long customerId, Long sessionId, String readerName) {
        Map<String, String> data = Map.of(
            "type", "READING_FINISHED",
            "sessionId", sessionId.toString(),
            "readerName", readerName,
            "message", "Luận giải từ " + readerName + " đã sẵn sàng!",
            "action", "VIEW_RESULT",
            "sound", "notification.mp3"
        );
        sendDataToUser(customerId, data);
    }

    // --- 6. READER: Khách báo đã chuyển tiền ---
    public void notifyReaderPaymentSent(Long readerId, Long sessionId, String customerName) {
        Map<String, String> data = Map.of(
            "type", "PAYMENT_NOTIFICATION",
            "sessionId", sessionId.toString(),
            "message", "Khách hàng " + customerName + " báo đã thanh toán. Hãy kiểm tra ngân hàng!",
            "action", "CHECK_BANK_ACCOUNT",
            "sound", "notification.mp3"
        );
        sendDataToUser(readerId, data);
    }

    // --- 7. CUSTOMER: Reader xác nhận đã nhận tiền (Popup Unlock hoàn toàn) ---
    public void notifyCustomerPaymentConfirmed(Long customerId, Long sessionId) {
        Map<String, String> data = Map.of(
            "type", "PAYMENT_CONFIRMED",
            "sessionId", sessionId.toString(),
            "message", "Thanh toán thành công! Bài luận của bạn đã được mở khóa hoàn toàn.",
            "sound", "notification.mp3"
        );
        sendDataToUser(customerId, data);
    }

    // Hàm helper để gửi Data Message tới tất cả token của 1 User
    private void sendDataToUser(Long userId, Map<String, String> data) {
        List<FcmToken> tokens = tokenRepository.findByUserId(userId);
        if (!tokens.isEmpty()) {
            tokens.forEach(t -> fcmService.sendDataMessage(t.getToken(), data));
        }
    }
}