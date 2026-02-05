package org.example.be_eproject_sem4.Repository;

import java.time.Instant;
import java.util.List;

import org.example.be_eproject_sem4.Entity.ReadingSession;
import org.example.be_eproject_sem4.Entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReadingSessionRepository extends JpaRepository<ReadingSession, Long>,JpaSpecificationExecutor<ReadingSession> {
        List<ReadingSession> findByReaderAndStatus(User reader, String status);

        List<ReadingSession> findByCustomerAndStatus(User customer, String status);

        @Query("SELECT s FROM ReadingSession s JOIN FETCH s.reader " +
                        "WHERE s.customer.id = :userId " +
                        "AND s.status = :status " +
                        "AND s.isRated = false")
        List<ReadingSession> findPendingRatingsByCustomerId(@Param("userId") Long userId,
                        @Param("status") String status);

        @Query("SELECT COUNT(s) FROM ReadingSession s WHERE s.reader = :reader AND s.status = :status")
        Long countCompletedSessionsByReader(@Param("reader") User reader, @Param("status") String status);

        @Query("SELECT s FROM ReadingSession s " +
                        "WHERE s.reader.id = :readerId AND s.status = 'PROCESSING' " +
                        "ORDER BY s.acceptedAt DESC")
        List<ReadingSession> findCurrentProcessingSession(@Param("readerId") Long readerId, Pageable pageable);

        // 1. Lấy 10 phiên mới nhất để đổ vào bảng
        List<ReadingSession> findTop10ByOrderByCreatedAtDesc();

        // 2. Thống kê Stats
        long countByCreatedAtAfter(Instant startOfDay);

        long countByStatus(String status); // Truyền vào "IN_PROGRESS"

        // 3. Lấy Top 3 Readers dựa trên Elo Score (từ bảng User)
        @Query("SELECT u FROM User u WHERE u.role = 'READER' AND u.isActive = true ORDER BY u.eloScore DESC")
        List<User> findTopReaders(Pageable pageable);

        @Query(value = "SELECT HOUR(created_at) as hour, COUNT(*) as count " +
                        "FROM reading_sessions " +
                        "WHERE DATE(created_at) = CURRENT_DATE " +
                        "GROUP BY HOUR(created_at) " +
                        "ORDER BY hour", nativeQuery = true)
        List<Object[]> getHourlyStatsNative();

        @Query("SELECT s FROM ReadingSession s WHERE " +
                        "CAST(s.id AS string) LIKE %:keyword% OR " +
                        "LOWER(s.customer.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                        "LOWER(s.reader.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
        List<ReadingSession> findByKeyword(@Param("keyword") String keyword);
}
