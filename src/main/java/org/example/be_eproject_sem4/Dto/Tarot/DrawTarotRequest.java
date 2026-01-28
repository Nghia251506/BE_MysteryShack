package org.example.be_eproject_sem4.Dto.Tarot;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DrawTarotRequest {

    @NotBlank(message = "Chủ đề hỏi không được để trống")
    private String topic;  // "tình yêu", "công việc", "tài chính", "tổng quát"...
}