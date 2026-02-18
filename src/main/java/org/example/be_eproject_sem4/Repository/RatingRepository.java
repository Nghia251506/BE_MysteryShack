package org.example.be_eproject_sem4.Repository;

import java.util.List;

import org.example.be_eproject_sem4.Dto.RatingResponse;
import org.example.be_eproject_sem4.Entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    // 1. Lấy tất cả (Dùng cho cái "BE trả hết" của ông)
    List<Rating> findByReaderIdOrderByCreatedAtDesc(Long readerId);

    // 2. Lọc theo số sao (Ví dụ: Chỉ xem các đánh giá 1 sao hoặc 5 sao)
    List<Rating> findByReaderIdAndRatingValueOrderByCreatedAtDesc(Long readerId, Integer ratingValue);

    // 3. Lọc những đánh giá có bình luận (Bỏ qua những cái chỉ vote sao suông)
    @Query("SELECT r FROM Rating r WHERE r.reader.id = :readerId AND r.replyComment IS NOT NULL AND r.replyComment != ''")
    List<Rating> findByReaderIdWithComments(Long readerId);

    // 4. Kiểm tra xem Customer đã rate Session này chưa (Double check cho chắc)
    boolean existsByRequestId(Long requestId);

    // Hàm ông đang thiếu đây:
    @Query("SELECT r FROM Rating r WHERE r.reader.id = :readerId " +
            "AND r.ratingValue = :stars " +
            "AND r.replyComment IS NOT NULL AND r.replyComment != '' " +
            "ORDER BY r.createdAt DESC")
    List<Rating> findByReaderIdAndRatingValueWithComments(Long readerId, Integer stars);

    // Lấy 5 đánh giá mới nhất của 1 reader
    List<Rating> findTop5ByReaderIdOrderByCreatedAtDesc(Long readerId);

    // Tính trung bình cộng số sao của 1 reader
    @Query("SELECT AVG(r.ratingValue) FROM Rating r WHERE r.reader.id = :readerId")
    Double getAverageRatingByReaderId(Long readerId);

    // Đếm tổng số lượt đánh giá
    long countByReaderId(Long readerId);
}
