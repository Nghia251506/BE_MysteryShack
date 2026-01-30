package org.example.be_eproject_sem4.Dto;

import org.example.be_eproject_sem4.Entity.ReadingSession;
import org.example.be_eproject_sem4.Entity.User;

import lombok.Data;
import lombok.Locked.Read;

@Data
public class RatingResponse {
    private Long id;
    private ReadingSession request;
    private User customer;
    private User reader;
    private Integer ratingValue;
    private String comment;
}
