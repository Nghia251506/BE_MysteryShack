package org.example.be_eproject_sem4.Dto;

import lombok.Data;

import java.util.List;

@Data
public class ReadingSessionDTO {
    private Long customerId;
    private Integer topicId;
    private Long questionId;
    private String customerQuestion;

    // Bạn có thể để là List<Integer> hoặc Object tùy vào cấu trúc card của bạn
    private List<Object> selectedCards;

    private String summaryMeaning;
    private SessionStatus status;
}
