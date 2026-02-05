package org.example.be_eproject_sem4.Repository;

import org.example.be_eproject_sem4.Entity.ReadingDispute;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReadingDisputeRepository extends JpaRepository<ReadingDispute, Long> {
    
    // Tìm khiếu nại theo Session ID để check xem phiên này đã bị kiện chưa
    Optional<ReadingDispute> findBySessionId(Long sessionId);

    // Filter theo trạng thái dành cho Admin (PENDING, RESOLVED_REFUND, RESOLVED_REJECT)
    Page<ReadingDispute> findByStatus(String status, Pageable pageable);

    // Search theo tên khách hoặc tên reader (Dùng JPQL cho máu)
    @Query("SELECT d FROM ReadingDispute d WHERE " +
           "LOWER(d.customer.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(d.reader.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "CAST(d.session.id AS string) LIKE CONCAT('%', :keyword, '%')")
    Page<ReadingDispute> searchDisputes(@Param("keyword") String keyword, Pageable pageable);
}