package org.example.be_eproject_sem4.Repository;

import java.util.Optional;

import org.example.be_eproject_sem4.Entity.InterpretationForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InterpretationFormRepository extends JpaRepository<InterpretationForm, Long> {
    // Tìm form dựa trên Session ID
    @Query("SELECT i FROM InterpretationForm i " +
            "JOIN FETCH i.requestId s " + // Join sang ReadingSession
            "LEFT JOIN FETCH s.reader r " + // Lấy thông tin Reader từ Session
            "LEFT JOIN FETCH s.question q " + // Lấy thông tin Question từ Session
            "WHERE s.id = :sessionId")
    Optional<InterpretationForm> findByRequestIdId(@Param("sessionId") Long sessionId);
}
