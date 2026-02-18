package org.example.be_eproject_sem4.Dto.ReaderProfile;
import lombok.*;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReaderReviewDTO {
    private String customerName;
    private String customerAvatar;
    private Integer ratingValue;
    private String comment; // Trong Entity là replyComment hoặc nội dung đánh giá (ông check lại bảng Review/ReadingSession nhé)
    private String createdAt;
    private Boolean isAnonymous;
}
