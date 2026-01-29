package org.example.be_eproject_sem4.Dto;

import lombok.Data;

@Data // Nếu dùng Lombok, không thì tạo Getter/Setter tay
public class StatusRequest {
    private boolean active; // Chỉ cần thế này thôi
}
