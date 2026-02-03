package org.example.be_eproject_sem4.Dto.TestReader;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor // Thêm cái này để tạo Constructor có tham số
@NoArgsConstructor
public class TestSubmitDTO {
    private List<AnswerDTO> answers;

    @Data
    @AllArgsConstructor // Thêm cái này để tạo Constructor có tham số
    @NoArgsConstructor
    public static class AnswerDTO {
        private Long questionId;
        private String selectedOption;
    }
}
