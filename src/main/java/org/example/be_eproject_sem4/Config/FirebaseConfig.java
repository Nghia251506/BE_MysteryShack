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

    @Value("${JSON_FCM}") // Đảm bảo biến này khớp với file env/properties
    private String fcmJsonBase64;

    @Bean
    public FirebaseApp initializeFirebase() throws IOException {
        // Đọc trực tiếp từ thư mục resources
        ClassPathResource resource = new ClassPathResource("firebase-service-account.json");

        if (!resource.exists()) {
            throw new IllegalStateException(
                    "LỖI: Không tìm thấy file firebase-service-account.json trong folder resources!");
        }

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(resource.getInputStream()))
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.initializeApp(options);
        }
        return FirebaseApp.getInstance();
    }
}
