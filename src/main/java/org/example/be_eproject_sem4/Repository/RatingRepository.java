package org.example.be_eproject_sem4.Repository;

import java.util.List;

import org.example.be_eproject_sem4.Dto.RatingResponse;
import org.example.be_eproject_sem4.Entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<RatingResponse> findByReaderId(Long readerId);
}
