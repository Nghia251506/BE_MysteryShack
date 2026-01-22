package org.example.be_eproject_sem4.Repository;

import java.util.Optional;

import org.example.be_eproject_sem4.Entity.InterpretationForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InterpretationFormRepository extends JpaRepository<InterpretationForm, Long> {
    // Tìm form dựa trên Session ID
    Optional<InterpretationForm> findByRequestIdId(Long sessionId);
}
