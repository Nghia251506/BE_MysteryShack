package org.example.be_eproject_sem4.Service.User;

import org.example.be_eproject_sem4.Dto.Auth.LoginDto;
import org.example.be_eproject_sem4.Dto.Auth.RegisterDto;
import org.example.be_eproject_sem4.Dto.Auth.UserDto;
import org.example.be_eproject_sem4.Dto.Auth.UserUpdateRequest;
import org.example.be_eproject_sem4.Entity.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserService {
    List<UserDto> getAll();  // Lấy tất cả người dùng

    @Transactional(readOnly = true)
    User findByUsername(String username);

    List<UserDto> getAllActiveUsers();  // Lấy tất cả người dùng đang hoạt động
    UserDto getById(Long id);  // Lấy người dùng theo id
    UserDto create(RegisterDto req);  // Tạo người dùng mới
    UserDto update(Long id, UserUpdateRequest req);  // Cập nhật người dùng
    void delete(Long id);  // Xóa người dùng
    String getFullNameByUsername(String username);
}
