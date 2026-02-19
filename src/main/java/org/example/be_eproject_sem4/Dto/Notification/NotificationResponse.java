package org.example.be_eproject_sem4.Dto.Notification;
import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class NotificationResponse {
    private Long id;
    private String title;
    private String content;
    private String type;
    private String recipient;    // Logic: Nếu Specific thì trả về ID, không thì trả về nhãn nhóm
    private String status;       // SENT, SCHEDULED...
    private Integer readCount;
    private Integer totalCount;
    private String date;         // Format chuỗi: "2025-01-15 10:30" cho FE dễ dùng
    private String link;
    private String btnText;
}
