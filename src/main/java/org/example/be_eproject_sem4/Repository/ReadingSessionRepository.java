package org.example.be_eproject_sem4.Repository;

import java.util.List;

import org.example.be_eproject_sem4.Dto.ReadingSessionSimpleDto;
import org.example.be_eproject_sem4.Entity.ReadingSession;
import org.example.be_eproject_sem4.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReadingSessionRepository extends JpaRepository<ReadingSession, Long> {
    List<ReadingSession> findByReaderAndStatus(User reader, String status);
    List<ReadingSession> findByCustomerAndStatus(User customer, String status);
}
