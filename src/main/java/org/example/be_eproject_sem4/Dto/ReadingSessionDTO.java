package org.example.be_eproject_sem4.Dto;

import lombok.Data;

import java.util.List;

import org.example.be_eproject_sem4.Entity.TopicQuestion;
import org.example.be_eproject_sem4.Entity.User;

@Data
public class ReadingSessionDTO {
    private User customer;
    private User reader;
    private TopicQuestion question;
    // Bạn có thể để là List<Integer> hoặc Object tùy vào cấu trúc card của bạn
    private List<Object> selectedCards;
    private SessionStatus status;
}
