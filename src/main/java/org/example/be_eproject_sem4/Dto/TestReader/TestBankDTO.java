package org.example.be_eproject_sem4.Dto.TestReader;

import lombok.Data;
import org.example.be_eproject_sem4.Entity.QuestionCategory;

import java.util.List;

@Data
public class TestBankDTO {
    private Long id;
    private String question;
    private List<String> options;
    private String correctAnswer;
    private QuestionCategory category;
}
