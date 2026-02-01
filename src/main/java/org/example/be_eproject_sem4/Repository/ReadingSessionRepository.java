package org.example.be_eproject_sem4.Repository;

import java.util.List;

import org.example.be_eproject_sem4.Entity.ReadingSession;
import org.example.be_eproject_sem4.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReadingSessionRepository extends JpaRepository<ReadingSession, Long> {
    List<ReadingSession> findByReaderAndStatus(User reader, String status);
    List<ReadingSession> findByCustomerAndStatus(User customer, String status);
    @Query("SELECT s FROM ReadingSession s JOIN FETCH s.reader " +
            "WHERE s.customer.id = :userId " +
            "AND s.status = :status " +
            "AND s.isRated = false")
    List<ReadingSession> findPendingRatingsByCustomerId(@Param("userId") Long userId,
                                                    @Param("status") String status);
}
