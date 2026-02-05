package org.example.be_eproject_sem4.Dto.Admin;

import lombok.Data;

@Data
public class UpdateReaderStatusDto {
    private String status; // ACTIVE, LOCKED, REJECTED
    private String reason; // Lý do (đặc biệt cần khi khóa hoặc từ chối)
}
