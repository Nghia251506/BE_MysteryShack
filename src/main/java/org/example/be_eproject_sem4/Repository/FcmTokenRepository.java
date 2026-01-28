package org.example.be_eproject_sem4.Repository;

import java.util.List;
import java.util.Optional;

import org.example.be_eproject_sem4.Entity.FcmToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {
    // Tìm tất cả token của 1 user (để gửi thông báo đến mọi thiết bị họ đang dùng)
    List<FcmToken> findByUserId(Long userId);
    
    // Tìm token cụ thể để tránh lưu trùng lặp
    List<FcmToken> findByToken(String token);
}
