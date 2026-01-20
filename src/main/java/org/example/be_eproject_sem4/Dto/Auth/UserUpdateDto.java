package org.example.be_eproject_sem4.Dto.Auth;

import org.example.be_eproject_sem4.Entity.User;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDto {

    @Size(max = 100, message = "Họ tên tối đa 100 ký tự")
    private String fullName;

    @Size(max = 20, message = "Số điện thoại tối đa 20 ký tự")
    private String phone;

    @Size(max = 500, message = "Bio tối đa 500 ký tự")
    private String bio;

    // Profile picture (URL hoặc file upload, tùy cách bạn xử lý)
    private String profilePicture;

    // Nếu cho phép update role (thường không cho, chỉ admin update)
    private User.Role role;
}
