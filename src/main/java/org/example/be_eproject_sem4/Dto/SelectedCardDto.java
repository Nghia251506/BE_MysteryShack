package org.example.be_eproject_sem4.Dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SelectedCardDto {
    private Integer pos;      // Vị trí lá bài (1, 2, 3)
    private Long cardId;      // ID để link tới bảng tarot_cards nếu cần
    private String nameVi;    // Tên tiếng Việt để hiển thị luôn
    private String imageUrl;  // Link ảnh để hiển thị luôn
}
