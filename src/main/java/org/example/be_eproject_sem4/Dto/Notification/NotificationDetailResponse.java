package org.example.be_eproject_sem4.Dto.Notification;

import java.util.List;

import lombok.Data;

@Data
public class NotificationDetailResponse {
    private NotificationResponse info;
    private List<UserReadInfo> readDetails; // Danh sách user đã đọc

    @Data
    public static class UserReadInfo {
        private String username;
        private String readAt;
    }
}