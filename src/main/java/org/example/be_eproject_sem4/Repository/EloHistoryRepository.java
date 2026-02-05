package org.example.be_eproject_sem4.Repository;

import java.util.List;

import org.example.be_eproject_sem4.Entity.EloHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EloHistoryRepository extends JpaRepository<EloHistory, Long> {
    List<EloHistory> findByReaderIdOrderByCreatedAtDesc(Long readerId);
}
