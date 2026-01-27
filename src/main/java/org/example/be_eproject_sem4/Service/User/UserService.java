package org.example.be_eproject_sem4.Service.User;

import org.example.be_eproject_sem4.Entity.User;
import org.example.be_eproject_sem4.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User getRandomTopReader() {
        // 1. Lấy danh sách 10 Reader cao điểm nhất
        List<User> topReaders = userRepository.findTop10ByRoleOrderByEloScoreDesc(User.Role.READER);

        if (topReaders.isEmpty()) {
            return null;
        }

        // 2. Lấy ngẫu nhiên 1 trong số các phần tử của danh sách
        Random rand = new Random();
        return topReaders.get(rand.nextInt(topReaders.size()));
    }

    public User getRandomTopReaderExcludingMe(Long currentUserId) {
        // Lấy danh sách Top Reader cao điểm nhất nhưng trừ ID của tôi ra
        List<User> topReaders = userRepository.findTop11ByRoleAndIdNotOrderByEloScoreDesc(User.Role.READER, currentUserId);

        if (topReaders.isEmpty()) {
            return null;
        }

        // Giới hạn lại danh sách chỉ lấy tối đa 10 người sau khi đã loại trừ
        int limit = Math.min(topReaders.size(), 10);
        List<User> subList = topReaders.subList(0, limit);

        // Chọn ngẫu nhiên 1 người
        Random rand = new Random();
        return subList.get(rand.nextInt(subList.size()));
    }
}
