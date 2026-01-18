package org.example.be_eproject_sem4.Repository;

import org.example.be_eproject_sem4.Entity.TopicQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<TopicQuestion, Long> {
}
