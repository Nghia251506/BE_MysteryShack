package org.example.be_eproject_sem4.Service;

import lombok.RequiredArgsConstructor;
import org.example.be_eproject_sem4.Dto.ReaderProfile.*;
import org.example.be_eproject_sem4.Entity.Rating;
import org.example.be_eproject_sem4.Entity.User;
import org.example.be_eproject_sem4.Mapper.ReaderProfileMapper;
import org.example.be_eproject_sem4.Repository.RatingRepository;
import org.example.be_eproject_sem4.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReaderProfileService {

    private final UserRepository userRepository;
    private final RatingRepository ratingRepository;
    private final ReaderProfileMapper readerProfileMapper;

    public ReaderProfileDTO getUnifiedReaderProfile(Long readerId) {
        // 1. Lấy thông tin User (Reader)
        User reader = userRepository.findById(readerId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Reader ID: " + readerId));

        if (reader.getRole() != User.Role.READER) {
            throw new RuntimeException("User này không phải là Reader");
        }

        // 2. Lấy 5 đánh giá gần nhất
        List<Rating> recentRatings = ratingRepository.findTop5ByReaderIdOrderByCreatedAtDesc(readerId);

        // 3. Map thông tin cơ bản qua MapStruct
        ReaderProfileDTO dto = readerProfileMapper.toProfileDTO(reader, recentRatings);

        // 4. Bổ sung các thông số tính toán (Aggregated Stats)
        Double avgRating = ratingRepository.getAverageRatingByReaderId(readerId);
        dto.setAverageRating(avgRating != null ? avgRating : 0.0);
        dto.setTotalReviews(ratingRepository.countByReaderId(readerId));

        return dto;
    }
}