package org.example.be_eproject_sem4.Repository;

import org.example.be_eproject_sem4.Entity.RatingMonthlyHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RatingMonthlyHistoryRepository extends JpaRepository<RatingMonthlyHistory, Long> {

    // 1. Lấy lịch sử theo Reader và sắp xếp theo thời gian
    List<RatingMonthlyHistory> findByReaderIdOrderByYearAscMonthAsc(Long readerId);

    // 2. SỬA TÊN HÀM Ở ĐÂY: averageRating -> FinalAvgRating
    // Phải khớp từng chữ với field 'finalAvgRating' trong Entity
    List<RatingMonthlyHistory> findByMonthAndYearOrderByFinalAvgRatingDesc(Integer month, Integer year);

    // 3. Kiểm tra xem tháng đó đã chốt sổ chưa
    boolean existsByReaderIdAndMonthAndYear(Long readerId, Integer month, Integer year);
}