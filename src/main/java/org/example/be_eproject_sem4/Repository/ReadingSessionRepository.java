package org.example.be_eproject_sem4.Repository;

import java.util.List;

import org.example.be_eproject_sem4.Dto.ReadingSessionSimpleDto;
import org.example.be_eproject_sem4.Entity.ReadingSession;
import org.example.be_eproject_sem4.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReadingSessionRepository extends JpaRepository<ReadingSession, Long> {
    List<ReadingSession> findByReaderAndStatus(User reader, String status);

    @Modifying
    @Query("UPDATE ReadingSession s SET s.status = :status WHERE s.id = :id")
    void updateStatus(@Param("id") Long id, @Param("status") String status);
}
