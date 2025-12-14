package org.example.be_eproject_sem4.Dto.Auth;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.example.be_eproject_sem4.Entity.Role;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterDto {
    @NotBlank(message = "Full Name không được để trống")
    private String fullname;
    @NotBlank(message = "Username không được để trống")
    private String username;
    @NotBlank(message = "Password không được để trống")
    private String password;
    @NotBlank(message = "Email không được để trống")
    private String email;
    @NotBlank(message = "Confirm password không được để trống")
    private String confirmPassword;
    private String role;
}
