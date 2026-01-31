package org.example.be_eproject_sem4.Service.User;

import jakarta.persistence.criteria.Predicate;
import org.example.be_eproject_sem4.Entity.User;
import org.example.be_eproject_sem4.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.*;

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

    public Page<User> getUsersByRole(User.Role role, int page, int size, String sortBy, String direction) {
        // Kiểm tra hướng sắp xếp (ASC hoặc DESC)
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        return userRepository.findByRole(role, (java.awt.print.Pageable) pageable);
    }

    public Page<User> getAdvancedSearch(
            User.Role role, String keyword, Boolean isActive,
            LocalDateTime startDate, LocalDateTime endDate,
            Integer minElo, Integer maxElo,
            int page, int size, String sortBy, String direction) {

        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<User> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Lọc theo Role
            predicates.add(cb.equal(root.get("role"), role));

            // 2. Search keyword (Tên hoặc Email)
            if (keyword != null && !keyword.isEmpty()) {
                String pattern = "%" + keyword.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("fullName")), pattern),
                        cb.like(cb.lower(root.get("email")), pattern)
                ));
            }

            // 3. Lọc theo trạng thái hoạt động
            if (isActive != null) {
                predicates.add(cb.equal(root.get("isActive"), isActive));
            }

            // 4. Lọc theo khoảng ngày tạo
            if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), startDate));
            }
            if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), endDate));
            }

            // 5. Lọc theo khoảng Elo Score (Mới bổ sung)
            if (minElo != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("eloScore"), minElo));
            }
            if (maxElo != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("eloScore"), maxElo));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return userRepository.findAll(spec, pageable);
    }
}
