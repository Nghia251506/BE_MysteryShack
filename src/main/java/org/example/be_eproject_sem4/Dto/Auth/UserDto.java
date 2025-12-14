package org.example.be_eproject_sem4.Dto.Auth;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@Data
@Getter
@Setter
public class UserDto {
    private Long id;
    private String username;
    private String password;
    private String fullname;
    private Boolean isActive;
    private String email;
    private String roleName;
    private LocalDate createdDate;
    private LocalDate modifiedDate;
}
