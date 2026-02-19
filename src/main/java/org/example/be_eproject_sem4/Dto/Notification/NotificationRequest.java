package org.example.be_eproject_sem4.Dto.Notification;

import lombok.Data;

@Data
public class NotificationRequest {
    private String title;
    private String content;
    private String type;           // System, Promotion...
    private String recipientGroup;  // All, AllReaders, Specific
    private String specificId;      // Có thể null nếu chọn All
    private String link;
    private String btnText;
    private boolean sendEmail;      // Thêm nếm từ cái checkbox FE của ông
}
