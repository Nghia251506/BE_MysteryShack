package org.example.be_eproject_sem4.Dto.Auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @NotBlank(message = "Username không được để trống")
    private String username;
    @NotBlank(message = "Password không được để trống")
    private String password;
}
