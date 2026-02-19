package org.example.be_eproject_sem4.Repository;

import java.util.List;
import java.util.Optional;

import org.example.be_eproject_sem4.Entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    // Tìm gói đang ACTIVE và CÒN HẠN của Reader
    @Query("SELECT s FROM Subscription s WHERE s.reader.id = :readerId " +
       "AND s.status = org.example.be_eproject_sem4.Entity.SubscriptionStatus.ACTIVE " +
       "AND s.endDate >= CURRENT_TIMESTAMP")
    Optional<Subscription> findValidSubscription(@Param("readerId") Long readerId);

    // Tìm lịch sử mua gói của 1 user
    List<Subscription> findByReaderIdOrderByCreatedAtDesc(Long readerId);
    // Lấy tất cả, cái nào mới mua thì hiện lên trên
    List<Subscription> findAllByOrderByCreatedAtDesc();

    @Query("SELECT COALESCE(SUM(sub.remainingJobs), 0) FROM Subscription sub WHERE sub.reader.id = :readerId")
    Integer sumRemainingJobsByReaderId(@Param("readerId") Long readerId);
}
