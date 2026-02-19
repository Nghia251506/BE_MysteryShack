package org.example.be_eproject_sem4.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.example.be_eproject_sem4.Dto.Admin.ReaderManagerResponse;
import org.example.be_eproject_sem4.Entity.User;
import org.example.be_eproject_sem4.Mapper.UserMapper;
import org.example.be_eproject_sem4.Repository.RatingRepository;
import org.example.be_eproject_sem4.Repository.ReadingSessionRepository;
import org.example.be_eproject_sem4.Repository.SubscriptionRepository;
import org.example.be_eproject_sem4.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReaderService {
    @Autowired private UserRepository userRepository;
    @Autowired private RatingRepository ratingRepo;
    @Autowired private ReadingSessionRepository sessionRepo;
    @Autowired private SubscriptionRepository subRepo;
    @Autowired private UserMapper userMapper;

    public List<ReaderManagerResponse> getAllReadersWithStats() {
        // 1. Lấy tất cả Reader từ Repo cũ của ông
        List<User> readers = userRepository.findAllReader();

        // 2. Map sang DTO kèm theo việc "điền" thông số thống kê
        return readers.stream().map(user -> {
            // Lấy rating trung bình (giả định hàm trả về Double)
            Double avgRating = ratingRepo.calculateAvgRatingByReaderId(user.getId());
            
            // Đếm số phiên hoàn thành
            Integer completed = sessionRepo.countByReaderIdAndStatus(user.getId(), "COMPLETED");
            
            // Đếm số lượt mua từ Subscription
            Integer purchases = subRepo.sumRemainingJobsByReaderId(user.getId());

            // Dùng cái hàm Mapper mà anh em mình vừa bàn ở trên
            return userMapper.toReaderManagerResponse(user, avgRating, completed, purchases);
        }).collect(Collectors.toList());
    }
}
