package org.example.be_eproject_sem4.Service.User;

import org.example.be_eproject_sem4.Entity.User;
import org.example.be_eproject_sem4.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
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

    // Lấy ngẫu nhiên 1 người giỏi nhưng loại trừ ID cụ thể (Tránh hiện lại chính mình)
    public User getRandomTopReaderExcludingMe(Long currentUserId) {
        List<User> topReaders = userRepository.findTop10ByRoleAndIdNotOrderByEloScoreDesc(User.Role.READER, currentUserId);
        return pickRandom(topReaders);
    }

    // Hàm phụ để xáo trộn và lấy người đầu tiên
    private User pickRandom(List<User> readers) {
        if (readers == null || readers.isEmpty()) {
            return null;
        }
        // Xáo trộn danh sách Top 10 để mỗi lần gọi là 1 người khác nhau trong nhóm giỏi nhất
        Collections.shuffle(readers);
        return readers.get(0);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
}
