package org.example.be_eproject_sem4.Repository;

import org.example.be_eproject_sem4.Entity.TestBank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestBankRepository extends JpaRepository<TestBank, Long> {
    // Có thể lấy random câu hỏi theo số lượng yêu cầu
    @Query(value = "SELECT * FROM test_bank ORDER BY RANDOM() LIMIT :limit", nativeQuery = true)
    List<TestBank> findRandomQuestions(@Param("limit") int limit);
}
