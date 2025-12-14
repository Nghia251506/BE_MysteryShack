package org.example.be_eproject_sem4.Dto.Auth;

import lombok.Data;

@Data
public class UserUpdateRequest {
    private String fullName;
    private String email;
    private String password;
    private String role;
    private Boolean isActive;
}
