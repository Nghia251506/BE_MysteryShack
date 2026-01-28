package org.example.be_eproject_sem4.Repository;

import org.example.be_eproject_sem4.Entity.History;
import org.example.be_eproject_sem4.Entity.ReadingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HistoryRepository extends JpaRepository<History, Long> {

    // ==========================================
    // 1. NHÓM HÀM CHO CUSTOMER (KHÁCH HÀNG)
    // ==========================================

    // Lấy toàn bộ lịch sử xem của một khách hàng (Sắp xếp mới nhất trước)
    List<History> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    // Lấy lịch sử của khách hàng nhưng có phân trang (Dùng khi list quá dài)
    Page<History> findByCustomerId(Long customerId, Pageable pageable);

    // Lọc theo trạng thái (VD: Khách muốn xem các đơn "Đang chờ" hoặc "Đã xong")
    List<History> findByCustomerIdAndStatus(Long customerId, ReadingStatus status);


    // ==========================================
    // 2. NHÓM HÀM CHO READER (NGƯỜI ĐỌC BÀI)
    // ==========================================

    // Lấy danh sách việc CỦA TÔI (Reader đã nhận)
    List<History> findByReaderIdOrderByCreatedAtDesc(Long readerId);

    // Lọc việc của tôi theo trạng thái (VD: Reader muốn xem đơn nào đang PENDING để làm gấp)
    List<History> findByReaderIdAndStatus(Long readerId, ReadingStatus status);

    // QUAN TRỌNG: Tìm các đơn hàng "VÔ CHỦ" (Chưa có Reader nhận) để Reader vào nhận việc ("Vợt khách")
    // Điều kiện: reader là null VÀ status là PENDING
    List<History> findByReaderIsNullAndStatus(ReadingStatus status);


    // ==========================================
    // 3. NHÓM HÀM THỐNG KÊ & KHÁC
    // ==========================================

    // Tính điểm đánh giá trung bình của một Reader (Để hiển thị profile uy tín)
    @Query("SELECT AVG(h.rating) FROM History h WHERE h.reader.id = :readerId AND h.rating IS NOT NULL")
    Double getAverageRatingByReaderId(@Param("readerId") Long readerId);

    // Đếm số lượng đơn hoàn thành của Reader (Tính KPI)
    Long countByReaderIdAndStatus(Long readerId, ReadingStatus status);

    // Tìm chi tiết lịch sử theo Session (Nếu bạn muốn link từ ReadingSession sang)
    Optional<History> findByRequestId(Long sessionId);
}