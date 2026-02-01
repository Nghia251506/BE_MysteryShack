package org.example.be_eproject_sem4.Dto.Rate;

import lombok.*;

@Data
@Builder
public class RatingResponseDTO {
    private Long id;
    private String customerName; // Hiển thị "Người dùng ẩn danh" nếu isAnonymous = true
    private String customerAvatar;
    private Integer ratingValue;
    private String comment;
    private String replyComment; // Phản hồi của Reader
    private String createdAt;    // Format: "dd/MM/yyyy HH:mm"
    private Boolean isAnonymous;
}
