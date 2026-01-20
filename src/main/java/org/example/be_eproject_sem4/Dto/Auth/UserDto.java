package org.example.be_eproject_sem4.Dto.Auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;
    
    private String username;
    
    private String email;
    
    private String fullName;
    
    private String phone;
    
    private String role; // "CUSTOMER" hoặc "READER" (string thay vì enum để dễ serialize)
    
    private String bio;
    
    private String profilePicture; // URL ảnh đại diện
    
    private boolean isVerified;
    
    private int eloScore; // Chỉ có ý nghĩa với READER
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}