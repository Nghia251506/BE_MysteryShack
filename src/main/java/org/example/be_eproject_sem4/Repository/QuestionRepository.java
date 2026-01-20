package org.example.be_eproject_sem4.Repository;

import java.util.Optional;

import org.example.be_eproject_sem4.Entity.ReadingSession;
import org.example.be_eproject_sem4.Entity.TopicQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<TopicQuestion, Long> {
    Optional<TopicQuestion> findById(Long questionId);
}
