package org.example.be_eproject_sem4.Dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.example.be_eproject_sem4.Entity.TopicQuestion;
import org.example.be_eproject_sem4.Entity.User;

@Data
public class ReadingSessionDTO {
    private Long question;           // ID của TopicQuestion
    private List<Long> selectedCards; // Mảng ID lá bài (Long)
    private String customerQuestion; // Optional
    // Field chỉ dùng khi chưa đăng nhập (khách vãng lai)
    private String fullName;                  // Họ tên (bắt buộc nếu chưa login)
    private Date birthDate;              // Ngày sinh (bắt buộc nếu chưa login)
}
