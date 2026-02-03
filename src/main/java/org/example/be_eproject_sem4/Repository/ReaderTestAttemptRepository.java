package org.example.be_eproject_sem4.Repository;

import org.example.be_eproject_sem4.Entity.ReaderTestAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReaderTestAttemptRepository extends JpaRepository<ReaderTestAttempt, Long> {
    // Tìm lần thi gần nhất của reader
    Optional<ReaderTestAttempt> findFirstByReaderIdOrderByCreatedAtDesc(Long readerId);
}
