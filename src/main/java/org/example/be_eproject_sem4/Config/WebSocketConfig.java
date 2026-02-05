package org.example.be_eproject_sem4.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // Cực kỳ quan trọng, thiếu dòng này là lỗi y hệt như trên
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Kích hoạt một "Broker" đơn giản để đẩy tin nhắn tới các prefix bắt đầu bằng /topic
        config.enableSimpleBroker("/topic");
        
        // Các tin nhắn gửi từ Client lên Server sẽ bắt đầu bằng /app
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Đăng ký cổng kết nối cho Frontend
        registry.addEndpoint("/ws-dashboard")
                .setAllowedOrigins("http://localhost:5173") // URL của React
                .withSockJS(); // Để hỗ trợ các trình duyệt không hỗ trợ WebSocket thuần
    }
}