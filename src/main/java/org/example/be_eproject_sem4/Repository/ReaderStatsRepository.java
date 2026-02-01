package org.example.be_eproject_sem4.Repository;

import org.example.be_eproject_sem4.Entity.ReaderStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReaderStatsRepository extends JpaRepository<ReaderStats, Long> {
    Optional<ReaderStats> findByReaderId(Long readerId);

    // Dùng cho Cron Job: Lấy tất cả stats để chốt sổ cuối tháng
    @Query("SELECT s FROM ReaderStats s WHERE s.totalReviewsMonth > 0")
    List<ReaderStats> findAllActiveStats();
}
