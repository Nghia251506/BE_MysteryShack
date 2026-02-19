package org.example.be_eproject_sem4.Dto.Admin;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReaderManagerResponse {
    
    // --- THÔNG TIN CƠ BẢN (Hiển thị ở bảng danh sách) ---
    private String id;
    private String fullName;
    private String email;
    private String profilePicture; // Link ảnh đại diện (FE dùng reader.profilePicture)
    private String avatar;         // Dự phòng trường avatar nếu cần
    private String status;         // Active, Locked, Busy, Offline, Pending
    
    // --- CHỈ SỐ VÀ THỐNG KÊ (Dùng cho bảng và Modal) ---
    private Double eloScore;      // Điểm ELO (FE dùng reader.eloScore)
    private Double rating;         // Sao trung bình (vd: 4.8)
    private Integer completedSessions; // Số phiên đã xong
    private Integer totalPurchases;    // Số lượt mua/đặt lịch
    private LocalDateTime createdAt;   // Ngày tạo tài khoản (Dùng để lọc date)

    // --- THÔNG TIN CHI TIẾT (Dùng cho Modal Xem chi tiết) ---
    private String phone;
    private String account;        // Tên tài khoản hệ thống
    private String dob;            // Ngày sinh (dạng String hoặc LocalDate)
    private String address;
    private String bio;            // Tiểu sử/Giới thiệu bản thân
}
