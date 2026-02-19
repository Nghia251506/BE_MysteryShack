package org.example.be_eproject_sem4.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.example.be_eproject_sem4.Entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u "
            + "WHERE u.username = :username")
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    List<User> findAllByRoleAndIsVerifiedOrderByEloScoreDesc(User.Role role, boolean isVerified);

    Optional<User> findByEmail(String email);

    List<User> findTop10ByRoleOrderByEloScoreDesc(User.Role role);

    // Dùng cho Matching thông minh
    @Query("SELECT u FROM User u WHERE u.role = 'READER' "
            + "AND u.isActive = true "
            + "AND u.isVerified = true "
            + "AND u.isBlocked = false "
            + "AND u.isBusy = false "
            + "AND u.id NOT IN :excludedIds")
    List<User> findAvailableReadersForMatching(@Param("excludedIds") List<Long> excludedIds);

    @Query("SELECT u FROM User u WHERE u.role = 'READER'")
    List<User> findAllReader();

    // 1. Đếm số Reader đang hoạt động (Online)
    long countByRoleAndIsActive(User.Role role, boolean isActive);

    // 2. Đếm số Khách hàng mới đăng ký trong vòng X ngày
    long countByRoleAndCreatedAtAfter(User.Role role, LocalDateTime dateTime);

    // Tiện tay làm luôn hàm lấy Top Readers cho Dashboard nếu cần
    List<User> findTop3ByRoleAndIsActiveOrderByEloScoreDesc(User.Role role, boolean isActive);

    @Query(value = "SELECT HOUR(created_at) as hour, COUNT(*) as count "
            + "FROM reading_sessions "
            + "WHERE created_at >= CURRENT_DATE "
            + "GROUP BY HOUR(created_at) "
            + "ORDER BY hour", nativeQuery = true)
    List<Object[]> getHourlyStatsNative();

    @Query("SELECT u FROM User u WHERE u.role = 'CUSTOMER' AND "
            + "(:keyword IS NULL OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
            + "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
            + "u.phone LIKE CONCAT('%', :keyword, '%'))")
    Page<User> findAllCustomers(@Param("keyword") String keyword, Pageable pageable);

    // Tìm một Customer cụ thể theo ID để đảm bảo role đúng là Customer
    Optional<User> findByIdAndRole(Long id, User.Role role);

    // Đếm tổng số user đang hoạt động để gửi thông báo "All"
    long countByIsActiveTrue();

// Đếm số lượng theo Role (Reader hoặc Customer)
// Giả sử Role của ông là Enum, nếu là String thì để String role
    long countByRoleAndIsActiveTrue(Object role);
}
