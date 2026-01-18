package org.example.be_eproject_sem4.Repository;

import org.example.be_eproject_sem4.Entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicRepository extends JpaRepository<Topic, Integer> {
}
