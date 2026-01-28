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
    
    // Đổi sang dùng Repository mới tạo
    @Autowired private FcmTokenRepository tokenRepository; 

    // 1. Thông báo cho Reader khi có Match
    public void notifyReaderNewRequest(Long readerId, Long sessionId) {
        // Lấy danh sách tất cả token của Reader này
        List<FcmToken> tokens = tokenRepository.findByUserId(readerId);
        
        if (!tokens.isEmpty()) {
            tokens.forEach(t -> {
                fcmService.sendPushNotification(
                    t.getToken(), 
                    "Yêu cầu mới!", 
                    "Bạn vừa được kết nối với một khách hàng.", 
                    Map.of("type", "NEW_MATCH", "sessionId", sessionId.toString())
                );
            });
        }
    }

    // 2. Thông báo cho Khách khi Reader Accept
    public void notifyCustomerAccepted(Long customerId) {
        List<FcmToken> tokens = tokenRepository.findByUserId(customerId);
        
        if (!tokens.isEmpty()) {
            tokens.forEach(t -> {
                fcmService.sendPushNotification(
                    t.getToken(), 
                    "Đã chấp nhận!", 
                    "Reader đã bắt đầu xem bài cho bạn.", 
                    Map.of("type", "READER_ACCEPTED")
                );
            });
        }
    }

    // 3. Thông báo cho Khách khi đã có Luận giải
    public void notifyReadingFinished(Long customerId, Long sessionId) {
        List<FcmToken> tokens = tokenRepository.findByUserId(customerId);
        
        if (!tokens.isEmpty()) {
            tokens.forEach(t -> {
                fcmService.sendPushNotification(
                    t.getToken(), 
                    "Luận giải đã sẵn sàng!", 
                    "Bài luận của bạn đã hoàn tất. Hãy vào xem và thanh toán ngay.", 
                    Map.of("type", "READING_FINISHED", "sessionId", sessionId.toString())
                );
            });
        }
    }
}
