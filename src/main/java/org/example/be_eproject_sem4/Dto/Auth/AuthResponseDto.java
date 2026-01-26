package org.example.be_eproject_sem4.Dto.Auth;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponseDto {
    private String token;
    private UserDto user;
}
