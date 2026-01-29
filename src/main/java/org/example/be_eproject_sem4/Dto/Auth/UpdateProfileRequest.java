package org.example.be_eproject_sem4.Dto.Auth;

import lombok.Data;

import java.util.Date;

@Data
public class UpdateProfileRequest {
    private String fullName;
    private Date birthDate;
}
