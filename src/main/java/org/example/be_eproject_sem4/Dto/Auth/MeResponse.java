package org.example.be_eproject_sem4.Dto.Auth;

import org.example.be_eproject_sem4.Entity.User.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeResponse {

    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String phone;
    private Role role;
    private String bio;
    private String profilePicture;
    private boolean isVerified;
    private int eloScore; // Chỉ hiển thị nếu là READER
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}