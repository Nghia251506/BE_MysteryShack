package org.example.be_eproject_sem4.Repository;

import org.example.be_eproject_sem4.Entity.ApplicationStatus;
import org.example.be_eproject_sem4.Entity.ReaderApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReaderApplicationRepository extends JpaRepository<ReaderApplication, Long> {
    // Kiểm tra xem user đã có đơn đăng ký nào chưa
    boolean existsByUserId(Long userId);

    List<ReaderApplication> findAllByStatus(ApplicationStatus status);
}
