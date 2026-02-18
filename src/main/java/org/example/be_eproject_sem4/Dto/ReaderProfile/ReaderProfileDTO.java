package org.example.be_eproject_sem4.Dto.ReaderProfile;

import lombok.*;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReaderProfileDTO {
    // 1. Thông tin Reader cơ bản
    private Long id;
    private String fullName;
    private String avatarUrl; // Map từ profilePicture
    private String bio;
    private String email;
    private String birthDate; // Format String yyyy-MM-dd cho FE dễ dùng
    private String status;    // ONLINE/BUSY dựa trên isBusy
    
    // 2. Chỉ số chuyên môn (Stats)
    private Double eloScore;
    private Double reputation;
    private Integer experienceYears; // Tính từ năm tạo tài khoản đến nay
    private Double averageRating;    // BE tính trung bình rồi ném vào đây
    private Long totalReviews;       // Tổng số lượt đánh giá
    
    // 3. Danh sách đánh giá gần đây (Reviews)
    private List<ReaderReviewDTO> recentReviews;
}
