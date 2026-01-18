package org.example.be_eproject_sem4.Dto;

import lombok.Data;

@Data
public class QuestionDTO {
    private Integer topicId;
    private String questionText;
    private String questionTextEn;
    private Boolean isPopular;
}
