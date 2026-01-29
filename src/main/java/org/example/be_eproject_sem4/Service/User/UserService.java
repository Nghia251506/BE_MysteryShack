package org.example.be_eproject_sem4.Service.User;

import org.example.be_eproject_sem4.Entity.User;
import org.example.be_eproject_sem4.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    // Lấy ngẫu nhiên 1 trong những người giỏi nhất (Dùng cho khách vãng lai)
    public User getRandomTopReader() {
        List<User> topReaders = userRepository.findTop10ByRoleOrderByEloScoreDesc(User.Role.READER);
        return pickRandom(topReaders);
    }

    // Lấy ngẫu nhiên 1 người giỏi nhưng loại trừ ID cụ thể (Tránh hiện lại chính
    // mình)
    public User getRandomTopReaderExcludingMe(Long currentUserId) {
        List<User> topReaders = userRepository.findTop10ByRoleAndIdNotOrderByEloScoreDesc(User.Role.READER,
                currentUserId);
        return pickRandom(topReaders);
    }

    // Hàm phụ để xáo trộn và lấy người đầu tiên
    private User pickRandom(List<User> readers) {
        if (readers == null || readers.isEmpty()) {
            return null;
        }
        // Xáo trộn danh sách Top 10 để mỗi lần gọi là 1 người khác nhau trong nhóm giỏi
        // nhất
        Collections.shuffle(readers);
        return readers.get(0);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Transactional
    public User toggleStatus(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Đảo ngược trạng thái: !true = false, !false = true
        user.setActive(!user.isActive());

        return userRepository.save(user);
    }

    @Transactional
    public User updateFullnameAndBirthdate(Long id, String fullName, Date birthDate) {
        // 1. Tìm User trực tiếp từ database
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + id));

        // 2. Cập nhật thủ công (Manual Update) thay vì dùng Mapper
        // Kiểm tra null để tránh ghi đè dữ liệu cũ bằng null nếu client không gửi lên
        if (fullName != null && !fullName.trim().isEmpty()) {
            user.setFullName(fullName);
        }

        if (birthDate != null) {
            user.setBirthDate(birthDate);
        }

        // 3. Lưu thực thể đã cập nhật
        // Spring Data JPA sẽ tự động hiểu đây là lệnh update nhờ vào @Id
        return userRepository.save(user);
    }
}
