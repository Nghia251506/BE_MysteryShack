package org.example.be_eproject_sem4.Dto;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.example.be_eproject_sem4.Entity.TopicQuestion;

import lombok.*;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadingSessionSimpleDto {
    private Long id;
    private String status;
    private TopicQuestion question;
    private String questionName;
    private List<SelectedCardDto> selectedCards;
    private String fullName;                  // Họ tên (bắt buộc nếu chưa login)
    private Date birthDate;
    private Instant matchedAt;
}
