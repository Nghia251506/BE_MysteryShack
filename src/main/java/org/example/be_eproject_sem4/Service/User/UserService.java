package org.example.be_eproject_sem4.Service.User;

import org.example.be_eproject_sem4.Entity.User;
import org.example.be_eproject_sem4.Repository.UserRepository;
import org.example.be_eproject_sem4.Service.FCM.NotificationManager;
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
    @Autowired
    private NotificationManager notificationManager;

    // Lấy ngẫu nhiên 1 trong những người giỏi nhất (Dùng cho khách vãng lai)
    public User getRandomTopReader() {
        List<User> topReaders = userRepository.findTop10ByRoleOrderByEloScoreDesc(User.Role.READER);
        return pickRandom(topReaders);
    }

    // Lấy ngẫu nhiên 1 người giỏi nhưng loại trừ ID cụ thể (Tránh hiện lại chính
    // mình)
    public User findRandomReader(List<Long> excludedIds, Long currentCustomerId) {
        // 1. Xử lý danh sách loại trừ để tránh lỗi SQL
        List<Long> finalExcludes = (excludedIds == null || excludedIds.isEmpty())
                ? List.of(-1L)
                : excludedIds;

        // 2. Lấy danh sách Reader khả dụng
        List<User> readers = userRepository.findAvailableReadersForMatching(finalExcludes);
        if (readers.isEmpty()) {
            return null;
        }

        // 3. Thuật toán Weighted Random dựa trên Elo
        double totalWeight = 0.0;
        for (User r : readers) {
            totalWeight += Math.pow(r.getEloScore() / 1000.0, 2);
        }

        double randomValue = new Random().nextDouble() * totalWeight;
        double countWeight = 0.0;
        User matchedReader = null;

        for (User r : readers) {
            countWeight += Math.pow(r.getEloScore() / 1000.0, 2);
            if (countWeight >= randomValue) {
                matchedReader = r;
                break;
            }
        }

        if (matchedReader == null) matchedReader = readers.get(0);

        // 4. Bắn Notification "vỗ vai" Reader ngay tại Service
        if (readers.isEmpty()) return null;

        for (User r : readers) {
            countWeight += Math.pow(r.getEloScore() / 1000.0, 2);
            if (countWeight >= randomValue) {
                matchedReader = r;
                break;
            }
        }
        // Backup nếu vòng lặp có vấn đề
        if (matchedReader == null) matchedReader = readers.get(0);

        // Bắn thông báo cho khách: "Đã tìm thấy người tương thích!"
        notificationManager.notifyReaderMatched(currentCustomerId, matchedReader.getFullName());

        return matchedReader;
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
