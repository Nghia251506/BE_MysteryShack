package org.example.be_eproject_sem4.Mapper;

import org.example.be_eproject_sem4.Dto.Auth.UserDto;
import org.example.be_eproject_sem4.Entity.User;
import org.example.be_eproject_sem4.Entity.Role;

public class UserMapper {
    public static UserDto toDto(User user) {
        if(user == null) {
            return null;
        }
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setPassword(user.getPassword());
        dto.setFullname(user.getFullname());
        dto.setEmail(user.getEmail());
        dto.setIsActive(user.getIsActive());
        if(user.getRole() != null) {
            dto.setRoleName(user.getRole().getRoleName());
        }
        return dto;
    }
}
