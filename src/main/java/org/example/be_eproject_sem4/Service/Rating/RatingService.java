package org.example.be_eproject_sem4.Service.Rating;

import java.util.List;

import org.example.be_eproject_sem4.Dto.RatingRequest;
import org.example.be_eproject_sem4.Dto.RatingResponse;
import org.example.be_eproject_sem4.Entity.Rating;
import org.example.be_eproject_sem4.Mapper.RatingMapper;
import org.example.be_eproject_sem4.Repository.RatingRepository;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RatingService {
    private final RatingRepository ratingRepository;
    private final RatingMapper ratingMapper;

    @Transactional
    public List<RatingResponse> getAll(){
        return ratingRepository.findAll()
                .stream()
                .map(ratingMapper::toDto)
                .toList();
    }
    @Transactional
    public RatingResponse createRating(RatingRequest ratingRequest) {
        Rating rate = ratingMapper.toRequestDto(ratingRequest);
        Rating savedRating = ratingRepository.save(rate);
        return ratingMapper.toDto(savedRating);
    }

    
}
