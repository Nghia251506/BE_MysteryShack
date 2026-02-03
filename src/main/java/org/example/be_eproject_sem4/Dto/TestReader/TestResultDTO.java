package org.example.be_eproject_sem4.Dto.TestReader;

import lombok.*;

@Data
@AllArgsConstructor // Thêm cái này để tạo Constructor có tham số
@NoArgsConstructor
public class TestResultDTO {
    private Double score;
    private Integer correctCount;
    private Integer totalQuestions;
    private String status;
}