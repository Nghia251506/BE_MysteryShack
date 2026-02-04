package org.example.be_eproject_sem4.Repository;

import java.util.List;

import org.example.be_eproject_sem4.Entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    // Tìm lịch sử theo User
    List<Payment> findByUserIdOrderByPaymentDateDesc(Long userId);
    
    // Thống kê tổng tiền (Cho Dashboard Admin)
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = 'SUCCESS'")
    Long getTotalRevenue();

    // Lấy giao dịch mới nhất hiện lên đầu
    List<Payment> findAllByOrderByPaymentDateDesc();
}
