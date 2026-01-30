package org.example.be_eproject_sem4.Dto;

import lombok.Data;

@Data
public class RatingRequest {
    private Long requestId;
    private Long customerId;
    private Long readerId;
    private Integer ratingValue;
    private String comment;
}
