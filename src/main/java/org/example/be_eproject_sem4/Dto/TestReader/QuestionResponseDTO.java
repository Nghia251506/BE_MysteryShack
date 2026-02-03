package org.example.be_eproject_sem4.Dto.TestReader;

import lombok.*;
import org.example.be_eproject_sem4.Entity.QuestionCategory;

import java.util.List;

@Data
@AllArgsConstructor // Thêm cái này để tạo Constructor có tham số
@NoArgsConstructor
public class QuestionResponseDTO {
    private Long id;
    private String question;
    private List<String> options;
    private QuestionCategory category;
}
