package org.example.be_eproject_sem4.Dto;

import java.util.Date;
import java.util.List;

import lombok.*;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadingSessionSimpleDto {
    private Long id;
    private String status;
    private String questionName;
    private List<Long> selectedCards;
    private String fullName;                  // Họ tên (bắt buộc nếu chưa login)
    private Date birthDate;
}
