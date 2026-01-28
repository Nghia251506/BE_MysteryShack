package org.example.be_eproject_sem4.Service.FCM;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

@Service
public class FCMService {

    public void sendPushNotification(String token, String title, String body, Map<String, String> data) {
        Message message = Message.builder()
            .setToken(token)
            .setNotification(Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build())
            .putAllData(data) // Gửi kèm ID phiên để FE fetch dữ liệu
            .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("Gửi thông báo thành công: " + response);
        } catch (FirebaseMessagingException e) {
            System.err.println("Lỗi gửi thông báo: " + e.getMessage());
        }
    }
}
