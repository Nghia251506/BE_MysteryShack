package org.example.be_eproject_sem4.Config;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.google.api.client.util.Value;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

@Configuration
public class FirebaseConfig {

    // Lấy trực tiếp nội dung JSON từ biến môi trường
    @org.springframework.beans.factory.annotation.Value("${JSON_FCM:}") 
    private String fcmJson;

    @Bean
    public FirebaseApp initializeFirebase() throws IOException {
        FirebaseOptions options;

        // Ưu tiên đọc từ biến môi trường (Dùng cho Cloud)
        if (fcmJson != null && !fcmJson.isEmpty()) {
            options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(new ByteArrayInputStream(fcmJson.getBytes())))
                    .build();
        } 
        // Nếu không có biến môi trường thì tìm file vật lý (Dùng cho Local)
        else {
            ClassPathResource resource = new ClassPathResource("firebase-service-account.json");
            if (!resource.exists()) {
                throw new IllegalStateException("LỖI: Không tìm thấy cả biến JSON_FCM lẫn file vật lý!");
            }
            options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(resource.getInputStream()))
                    .build();
        }

        if (FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.initializeApp(options);
        }
        return FirebaseApp.getInstance();
    }
}
